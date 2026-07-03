package com.hotel.booking.modules.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.dto.request.ConfirmBookingRequest;
import com.hotel.booking.modules.booking.dto.request.InitiateBookingRequest;
import com.hotel.booking.modules.booking.entity.Booking;
import com.hotel.booking.modules.booking.entity.BookingDetail;
import com.hotel.booking.modules.booking.entity.Invoice;
import com.hotel.booking.modules.booking.enums.BookingStatus;
import com.hotel.booking.modules.booking.enums.InvoiceStatus;
import com.hotel.booking.modules.booking.repository.BookingRepository;
import com.hotel.booking.modules.booking.repository.InvoiceRepository;
import com.hotel.booking.modules.crm.entity.Guest;
import com.hotel.booking.modules.crm.repository.GuestRepository;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Transactional
    public String confirmBooking(ConfirmBookingRequest request) {
        log.info("Start confirmBooking - BookingConfirmationServiceImpl - booking module");
        String sessionId = request.getSessionId();
        String dataKey = "payment:data:" + sessionId;
        String expireKey = "payment:expire:" + sessionId;

        // 1. Lấy dữ liệu giỏ hàng từ Redis
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

            // 5. Khởi tạo Entity Invoice
            Invoice invoice = Invoice.builder()
                    .booking(booking)
                    .invoiceNumber("INV-" + System.currentTimeMillis())
                    .status(InvoiceStatus.DRAFT)
                    .subTotal(booking.getSubtotalAmount())
                    .serviceFeeRate(booking.getServiceFeeRate())
                    .serviceFeeAmount(booking.getServiceFeeAmount())
                    .vatAmount(booking.getTotalVatAmount())
                    .grandTotal(booking.getTotalAmount())
                    .issuedAt(OffsetDateTime.now())
                    .build();

            // 6. Xử lý Booking Details và Cập nhật Kho (Inventory)
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
                if (roomInfo.getOccupancies() != null) {
                    for (InitiateBookingRequest.RoomOccupancy occ : roomInfo.getOccupancies()) {
                        totalGuestsAllRooms += (occ.getAdults() != null ? occ.getAdults() : 0)
                                + (occ.getChildren() != null ? occ.getChildren() : 0);
                    }
                } else {
                    totalGuestsAllRooms = 2 * roomInfo.getQuantity(); // Fallback nếu dữ liệu lỗi
                }

                BookingDetail detail = BookingDetail.builder()
                        .booking(booking)
                        .hotelRoomType(roomType)
                        .roomTypeName(roomType.getRoomType().getName())
                        .quantity(roomInfo.getQuantity().shortValue())
                        .guestCount((short) totalGuestsAllRooms)
                        .checkInDate(draftData.getCheckIn())
                        .checkOutDate(draftData.getCheckOut())
                        .roomAmount(detailBase)
                        .discountAmount(detailDiscount)
                        .vatRate(BigDecimal.valueOf(10))
                        .vatAmount(detailVat)
                        .finalAmount(detailFinal)
                        .build();

                booking.getBookingDetails().add(detail);

                // CHỐT INVENTORY (Cực kỳ quan trọng)
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
            }

            bookingRepository.save(booking);

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
        return "BKG-" + OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
    }
}