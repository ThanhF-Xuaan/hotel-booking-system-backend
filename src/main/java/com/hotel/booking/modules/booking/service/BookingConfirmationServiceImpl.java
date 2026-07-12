package com.hotel.booking.modules.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.dto.request.ConfirmBookingRequest;
import com.hotel.booking.modules.booking.dto.request.InitiateBookingRequest;
import com.hotel.booking.modules.booking.entity.*;
import com.hotel.booking.modules.booking.enums.*;
import com.hotel.booking.modules.booking.repository.BookingChargeRepository;
import com.hotel.booking.modules.booking.repository.BookingRepository;
import com.hotel.booking.modules.booking.repository.InvoiceRepository;
import com.hotel.booking.modules.booking.repository.PaymentRepository;
import com.hotel.booking.modules.crm.entity.Guest;
import com.hotel.booking.modules.crm.repository.GuestRepository;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeCatalogItem;
import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import com.hotel.booking.modules.inventory.enums.ItemUsage;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeCatalogItemRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import com.hotel.booking.modules.pricing.service.TaxCalculatorService;
import com.hotel.booking.modules.search.dto.request.PricingRequest;
import com.hotel.booking.modules.search.dto.request.RoomOccupancy;
import com.hotel.booking.modules.search.dto.request.RoomRequest;
import com.hotel.booking.modules.search.dto.response.PricingResponse;
import com.hotel.booking.modules.search.service.PriceAggregationService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingConfirmationServiceImpl implements BookingConfirmationService {

    StringRedisTemplate redisTemplate;
    ObjectMapper objectMapper;
    PriceAggregationService priceAggregationService;

    GuestRepository guestRepository;
    BookingRepository bookingRepository;
    InvoiceRepository invoiceRepository;
    RoomAvailabilityRepository availabilityRepository;
    HotelRepository hotelRepository;
    HotelRoomTypeRepository hotelRoomTypeRepository;
    BookingChargeRepository bookingChargeRepository;
    HotelRoomTypeCatalogItemRepository mappingRepository;
    TaxCalculatorService taxCalculatorService;
    InvoiceService invoiceService;

    PaymentRepository paymentRepository;

    @Transactional
    public String confirmBooking(ConfirmBookingRequest request) {
        log.info("Start confirmBooking - BookingConfirmationServiceImpl - booking module");
        String sessionId = request.getSessionId();
        String dataKey = "payment:data:" + sessionId;
        String expireKey = "payment:expire:" + sessionId;

        // 1. Lấy dữ liệu từ Redis
        String bookingDraftJson = redisTemplate.opsForValue().get(dataKey);
        if (bookingDraftJson == null) {
            throw new RuntimeException("Phiên giao dịch không tồn tại hoặc đã hết hạn!");
        }

        try {
            InitiateBookingRequest draftData = objectMapper.readValue(bookingDraftJson, InitiateBookingRequest.class);

            // 2. Tính tiền bằng Pricing Engine của Module Search
            PricingRequest pricingReq = buildPricingRequest(draftData);
            PricingResponse pricingData = priceAggregationService.calculatePrice(pricingReq);
            PricingResponse.PriceDetail grandTotal = pricingData.getGrandTotal();

            // 3. Xử lý thông tin Guest & Hotel
            Guest guest = createOrUpdateGuest(draftData.getGuestInfo());
            Hotel hotel = hotelRepository.findById(draftData.getHotelId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Khách sạn"));

            // 4. Khởi tạo Entity Booking
            Booking booking = Booking.builder()
                    .hotel(hotel)
                    .guest(guest)
                    .bookingNumber(generateBookingNumber())
                    .subtotalAmount(grandTotal.getBasePrice()
                            .add(grandTotal.getPriceAdjustment())    // Cộng điều chỉnh (tăng/giảm giá lễ/cuối tuần)
                            .subtract(grandTotal.getDiscountAmount()) // Trừ giảm giá
                            .add(grandTotal.getSurchargeAmount()))    // Cộng phụ thu (extra person)
                    .serviceFeeRate(hotel.getServiceFeePercent() != null ? hotel.getServiceFeePercent() : BigDecimal.ZERO)
                    .serviceFeeAmount(grandTotal.getServiceFeeAmount())
                    .totalVatAmount(grandTotal.getTaxAmount())
                    .totalAmount(grandTotal.getFinalPrice())
                    .status(BookingStatus.CONFIRMED)
                    .issuedAt(OffsetDateTime.now())
                    .build();

            booking = bookingRepository.save(booking);

            // 5. Khởi tạo Entity Invoice
            Invoice invoice = Invoice.builder()
                    .booking(booking)
                    .invoiceNumber(generateInvoiceNumber())
                    .status(InvoiceStatus.DRAFT)
                    .subTotal(booking.getSubtotalAmount())
                    .serviceFeeRate(booking.getServiceFeeRate())
                    .serviceFeeAmount(booking.getServiceFeeAmount())
                    .vatAmount(booking.getTotalVatAmount())
                    .grandTotal(booking.getTotalAmount())
                    .issuedAt(OffsetDateTime.now())
                    .build();

            invoice = invoiceRepository.save(invoice);

            // 6. Xử lý Booking Details và Cập nhật Kho (Inventory)
            long numberOfNights = ChronoUnit.DAYS.between(draftData.getCheckIn(), draftData.getCheckOut());

            int roomIndex = 0;
            for (InitiateBookingRequest.RoomSelection roomInfo : draftData.getRooms()) {

                HotelRoomType roomType = hotelRoomTypeRepository.findById(roomInfo.getHotelRoomTypeId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Loại phòng"));

                BigDecimal detailBase = BigDecimal.ZERO;
                BigDecimal detailDiscount = BigDecimal.ZERO;
                BigDecimal detailVat = BigDecimal.ZERO;
                BigDecimal detailFinal = BigDecimal.ZERO;

                // Cộng dồn tiền từ mảng kết quả của PricingEngine
                for (int i = 0; i < roomInfo.getQuantity(); i++) {
                    PricingResponse.PriceDetail singleRoomPrice = pricingData.getRooms().get(roomIndex).getPriceDetail();

                    detailBase = detailBase.add(singleRoomPrice.getBasePrice()
                            .add(singleRoomPrice.getPriceAdjustment()) // Cộng adjustment
                            .subtract(singleRoomPrice.getDiscountAmount()) // Trừ discount
                            .add(singleRoomPrice.getSurchargeAmount()));  // Cộng surcharge
                    detailDiscount = detailDiscount.add(singleRoomPrice.getDiscountAmount());
                    detailVat = detailVat.add(singleRoomPrice.getTaxAmount());
                    detailFinal = detailFinal.add(singleRoomPrice.getFinalPrice());

                    roomIndex++;
                }

                int totalGuestsAllRooms = 0;
                int totalAdultCount = 0;
                int totalChildrenCount = 0;
                int totalInfantCount = 0;
                if (roomInfo.getOccupancies() != null) {
                    for (InitiateBookingRequest.RoomOccupancy occ : roomInfo.getOccupancies()) {
                        totalGuestsAllRooms += (occ.getAdults() != null ? occ.getAdults() : 0)
                                + (occ.getChildren() != null ? occ.getChildren() : 0);
                        totalAdultCount += occ.getAdults() != null ? occ.getAdults() : 0;
                        totalChildrenCount += occ.getChildren() != null ? occ.getChildren() : 0;
                        totalInfantCount += occ.getInfants() != null ? occ.getInfants() : 0;
                    }
                } else {
                    totalGuestsAllRooms = 2 * roomInfo.getQuantity(); // Fallback nếu dữ liệu lỗi
                }

                BigDecimal calculatedVatRate = BigDecimal.ZERO;

                if (detailFinal.compareTo(detailVat) > 0 && detailVat.compareTo(BigDecimal.ZERO) > 0) {
                    calculatedVatRate = detailVat.multiply(BigDecimal.valueOf(100))
                            .divide(detailFinal.subtract(detailVat), 2, java.math.RoundingMode.HALF_UP);
                }

                BookingDetail detail = BookingDetail.builder()
                        .booking(booking)
                        .hotelRoomType(roomType)
                        .roomTypeName(roomType.getRoomType().getName())
                        .quantity(roomInfo.getQuantity().shortValue())
                        .adultCount((short) totalAdultCount)
                        .childCount((short) totalChildrenCount)
                        .infantCount((short) totalInfantCount)
                        .guestCount((short) totalGuestsAllRooms)
                        .checkInDate(draftData.getCheckIn())
                        .checkOutDate(draftData.getCheckOut())
                        .roomAmount(detailBase)
                        .discountAmount(detailDiscount)
                        .vatRate(calculatedVatRate)
                        .vatAmount(detailVat)
                        .finalAmount(detailFinal)
                        .build();

                booking.getBookingDetails().add(detail);

                var mappedItems = mappingRepository.findAllByHotelRoomTypeId(roomInfo.getHotelRoomTypeId());
                List<BookingCharge> chargesToSave = new ArrayList<>();

                // A. Quét các dịch vụ MANDATORY (Bắt buộc)
                for (var mapped : mappedItems) {
                    if (ItemUsage.MANDATORY.equals(mapped.getItemUsage())) {
                        chargesToSave.add(buildPackageCharge(detail, mapped, roomInfo.getQuantity(), numberOfNights, draftData.getCheckIn(), invoice.getId()));
                    }
                }

                // B. Quét các dịch vụ OPTIONAL (Khách chủ động mua thêm ở Giỏ hàng)
                if (roomInfo.getAddOns() != null && !roomInfo.getAddOns().isEmpty()) {
                    final Long finalInvoiceId = invoice.getId();

                    // 1. CHUYỂN LIST THÀNH MAP TRƯỚC VÒNG LẶP (Độ phức tạp O(M))
                    // Key: CatalogItemId, Value: HotelRoomTypeCatalogItem
                    Map<Integer, HotelRoomTypeCatalogItem> optionalItemsMap = mappedItems.stream()
                            .filter(m -> ItemUsage.OPTIONAL.equals(m.getItemUsage()))
                            .collect(Collectors.toMap(
                                    m -> m.getCatalogItem().getId(),
                                    m -> m,
                                    (existing, replacement) -> existing // Xử lý an toàn nếu vô tình có 2 map trùng item
                            ));

                    // 2. VÒNG LẶP CHECK DATA CHỈ CÒN LÀ O(N) VÀ LOOKUP O(1)
                    for (var addOn : roomInfo.getAddOns()) {
                        HotelRoomTypeCatalogItem mapped = optionalItemsMap.get(addOn.getCatalogItemId());

                        if (mapped != null) {
                            // Số lượng charge = quantity khách chọn * số đêm lưu trú (Đã xử lý logic nhân ngày ở trong hàm buildPackageCharge)
                            chargesToSave.add(buildPackageCharge(
                                    detail,
                                    mapped,
                                    addOn.getQuantity(),
                                    numberOfNights,
                                    draftData.getCheckIn(),
                                    finalInvoiceId
                            ));
                        } else {
                            log.warn("Không tìm thấy Mapping hợp lệ cho OPTIONAL AddOn với CatalogItemId: {}", addOn.getCatalogItemId());
                        }
                    }
                }

                // Lưu charge ngay vào DB
                bookingChargeRepository.saveAll(chargesToSave);

                // CHỐT INVENTORY
                List<RoomAvailability> availabilities = availabilityRepository
                        .findByHotelRoomTypeIdAndDateBetween(
                                roomInfo.getHotelRoomTypeId(),
                                draftData.getCheckIn(),
                                draftData.getCheckOut().minusDays(1)
                        );

                if(availabilities.isEmpty()){
                    throw new AppException(ErrorCode.ROOM_AVAILABILITY_NOT_FOUND);
                }

                for (RoomAvailability avail : availabilities) {
                    // Dịch chuyển trạng thái: Khóa -> Đã đặt
                    avail.setLockedRooms(Math.max(0, avail.getLockedRooms() - roomInfo.getQuantity()));
                    avail.setBookedRooms(avail.getBookedRooms() + roomInfo.getQuantity());
                }
                availabilityRepository.saveAll(availabilities);
            }

            bookingRepository.save(booking);
            invoiceRepository.save(invoice);

            OffsetDateTime deadline = OffsetDateTime.now().plusMinutes(15);
            for (BookingDetail detail : booking.getBookingDetails()) {
                detail.setSelectionDeadline(deadline);

                invoiceService.addLine(
                        invoice.getId(),
                        InvoiceLineType.ROOM,
                        detail.getRoomTypeName() + " (" + detail.getCheckInDate() + " - " + detail.getCheckOutDate() + ")",
                        (int) detail.getQuantity(),
                        detail.getRoomAmount(),
                        detail.getVatRate()
                );
            }

            bookingRepository.save(booking);

            Payment payment = Payment.builder()
                    .booking(booking)
                    .totalAmount(booking.getTotalAmount()) // Ghi nhận toàn bộ số tiền
                    .paymentMethod(PaymentMethod.VNPAY)     // Giả lập CASH
                    .status(PaymentStatus.SUCCESS)
                    .paidAt(OffsetDateTime.now())
                    .build();
            paymentRepository.save(payment);

            invoiceService.addLine(
                    invoice.getId(),
                    InvoiceLineType.PAYMENT,
                    "Thanh toán Online (Giả lập khi Confirm)",
                    1,
                    booking.getTotalAmount().negate(), // negate() để ra số ÂM
                    BigDecimal.ZERO
            );

            log.info("Booking [{}] confirmed & auto-paid: {}", booking.getBookingNumber(), booking.getTotalAmount());

            // 7. Hủy phiên Redis (Ngăn Worker 10 phút chạy nhả phòng)
            redisTemplate.delete(List.of(dataKey, expireKey));

            log.info("Xác nhận Booking [{}] thành công! Tổng tiền: {}", booking.getBookingNumber(), booking.getTotalAmount());
            return booking.getBookingNumber();

        } catch (Exception e) {
            log.error("Lỗi khi Confirm Booking", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private PricingRequest buildPricingRequest(InitiateBookingRequest draftData) {
        List<RoomRequest> pricingRooms = new ArrayList<>();

        for (InitiateBookingRequest.RoomSelection rs : draftData.getRooms()) {

            // Lặp qua đúng số lượng phòng khách đặt (quantity)
            for (int i = 0; i < rs.getQuantity(); i++) {
                List<RoomRequest.SelectedAddOn> pricingAddOns = new ArrayList<>();
                if (rs.getAddOns() != null) {
                    for (var addon : rs.getAddOns()) {
                        pricingAddOns.add(new RoomRequest.SelectedAddOn(addon.getCatalogItemId(), addon.getQuantity()));
                    }
                }

                // Lấy thông tin occupancy của phòng thứ i.
                // Xử lý an toàn: Nếu Frontend lỡ truyền thiếu phần tử, fallback về mặc định 2 NL, 0 TE.
                InitiateBookingRequest.RoomOccupancy occ =
                        (rs.getOccupancies() != null && rs.getOccupancies().size() > i)
                                ? rs.getOccupancies().get(i)
                                : new InitiateBookingRequest.RoomOccupancy(1, 0, 0);

                // Build đối tượng Occupancy của module Search
                RoomOccupancy searchOccupancy = RoomOccupancy.builder()
                        .adults(occ.getAdults() != null ? occ.getAdults() : 0)
                        .children(occ.getChildren() != null ? occ.getChildren() : 0)
                        .infants(occ.getInfants() != null ? occ.getInfants() : 0)
                        .build();

                // Nạp phòng này vào danh sách để Pricing Engine tính tiền
                pricingRooms.add(RoomRequest.builder()
                        .hotelRoomTypeId(rs.getHotelRoomTypeId())
                        .occupancy(searchOccupancy)
                        .addOns(pricingAddOns)
                        .build());
            }
        }

        return PricingRequest.builder()
                .hotelId(draftData.getHotelId())
                .checkIn(draftData.getCheckIn())
                .checkOut(draftData.getCheckOut())
                .rooms(pricingRooms)
                .build();
    }

    private Guest createOrUpdateGuest(InitiateBookingRequest.GuestInfo guestInfo) {
        Guest guest = guestRepository.findByPhoneAndIsDeletedFalse(guestInfo.getPhone())
                .orElseGet(() -> {
                    Guest newGuest = new Guest();
                    newGuest.setPublicId(UUID.randomUUID());
                    return newGuest;
                });

        guest.setFullName(guestInfo.getFullName());
        guest.setFirstName(guestInfo.getFirstName());
        guest.setLastName(guestInfo.getLastName());
        guest.setPhone(guestInfo.getPhone());
        guest.setEmail(guestInfo.getEmail());

        return guestRepository.save(guest);
    }

    private String generateBookingNumber() {
        String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        // Cắt 4 ký tự đầu của một chuỗi UUID ngẫu nhiên và viết hoa
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        return "BKG-" + timestamp + "-" + randomSuffix;
        // Kết quả: BKG-260709213015-A1B2
    }

    private String generateInvoiceNumber() {
        // Lấy ngày giờ đến giây
        String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

        //4 ký tự ngẫu nhiên (Chữ + Số) viết hoa
        String randomSuffix = RandomStringUtils.randomAlphanumeric(6).toUpperCase();

        // Kết quả: INV-260710123015-X9QW
        return "INV-" + timestamp + "-" + randomSuffix;
    }

    private BookingCharge buildPackageCharge(
            BookingDetail detail,
            HotelRoomTypeCatalogItem mapped,
            Integer requestedQty,
            long numberOfNights,
            LocalDate checkInDate,
            Long invoiceId) {

        boolean isPerStay = "PER_STAY".equalsIgnoreCase(mapped.getPricingType().toString());
        int totalQuantity = isPerStay ? requestedQty : (requestedQty * (int) numberOfNights);

        // 1. Tính Subtotal
        BigDecimal unitPrice = mapped.getPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(totalQuantity));

        // 2. Lấy Service Fee của Booking
        BigDecimal serviceFeeRate = detail.getBooking().getServiceFeeRate();
        if (serviceFeeRate == null) serviceFeeRate = BigDecimal.ZERO;

        // 3. Tính Phí dịch vụ riêng cho cái gói Add-on này
        BigDecimal serviceFeeAmount = subtotal.multiply(serviceFeeRate)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

        // 4. Giá trị chịu thuế = Subtotal + Service Fee
        BigDecimal taxableAmount = subtotal.add(serviceFeeAmount);

        // 5. Tính VAT (Đánh trên Taxable Amount)
        BigDecimal vatPercent = taxCalculatorService.getTaxRate(mapped.getCatalogItem().getTaxCategory().getId(), checkInDate);
        BigDecimal vatAmount = taxableAmount.multiply(vatPercent)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

        // 6. Tính Tổng tiền của riêng Add-on này
        BigDecimal totalAmount = taxableAmount.add(vatAmount);

        invoiceService.addLine(
                invoiceId,
                InvoiceLineType.PACKAGE_ITEM,
                mapped.getCatalogItem().getName(),
                totalQuantity,
                unitPrice,
                vatPercent
        );

        return BookingCharge.builder()
                .bookingDetail(detail)
                .catalogItem(mapped.getCatalogItem())
                .chargeType(ChargeType.PACKAGE_ITEM)
                .itemName(mapped.getCatalogItem().getName())
                .quantity(totalQuantity)
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .vatRate(vatPercent)
                .vatAmount(vatAmount)
                .totalAmount(totalAmount)    // Đã gồm Subtotal + Fee + VAT
                .issuedAt(OffsetDateTime.now())
                .build();
    }
}