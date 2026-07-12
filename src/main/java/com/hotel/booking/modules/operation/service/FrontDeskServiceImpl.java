package com.hotel.booking.modules.operation.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.entity.*;
import com.hotel.booking.modules.booking.enums.*;
import com.hotel.booking.modules.booking.repository.*;
import com.hotel.booking.modules.booking.service.BookingFinancialService;
import com.hotel.booking.modules.booking.service.InvoiceService;
import com.hotel.booking.modules.booking.service.PaymentService;
import com.hotel.booking.modules.crm.entity.Guest;
import com.hotel.booking.modules.crm.enums.GuestType;
import com.hotel.booking.modules.crm.enums.IdentityType;
import com.hotel.booking.modules.crm.repository.GuestRepository;
import com.hotel.booking.modules.inventory.entity.*;
import com.hotel.booking.modules.inventory.enums.RoomInstanceStatus;
import com.hotel.booking.modules.inventory.enums.RoomSlotStatus;
import com.hotel.booking.modules.inventory.repository.*;
import com.hotel.booking.modules.operation.dto.request.*;
import com.hotel.booking.modules.operation.dto.response.WalkInBookingResponse;
import com.hotel.booking.modules.operation.dto.response.WalkInRoomSummaryResponse;
import com.hotel.booking.modules.pos.entity.ServiceOrder;
import com.hotel.booking.modules.pos.repository.ServiceOrderRepository;
import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import com.hotel.booking.modules.pricing.entity.TaxCategory;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import com.hotel.booking.modules.pricing.repository.SurchargeRuleRepository;
import com.hotel.booking.modules.pricing.repository.TaxCategoryRepository;
import com.hotel.booking.modules.pricing.service.TaxCalculatorService;
import com.hotel.booking.modules.search.dto.request.PricingRequest;
import com.hotel.booking.modules.search.dto.request.RoomOccupancy;
import com.hotel.booking.modules.search.dto.request.RoomRequest;
import com.hotel.booking.modules.search.dto.response.PricingResponse;
import com.hotel.booking.modules.search.service.PriceAggregationService;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FrontDeskServiceImpl implements FrontDeskService {
    BookingRepository bookingRepository;
    BookingDetailRepository bookingDetailRepository;
    RoomSlotRepository roomSlotRepository;
    RoomInstanceRepository roomInstanceRepository;
    RoomAvailabilityRepository roomAvailabilityRepository;
    BookingChargeRepository bookingChargeRepository;
    InvoiceRepository invoiceRepository;
    TaxCalculatorService taxCalculatorService;
    CatalogItemRepository catalogItemRepository;
    BookingRoomRepository bookingRoomRepository;
    BookingGuestRepository bookingGuestRepository;
    GuestRepository guestRepository;
    HotelRoomTypeCatalogItemRepository hotelRoomTypeCatalogItemRepository;
    SurchargeRuleRepository surchargeRuleRepository;
    PaymentRepository paymentRepository;
    HotelRepository hotelRepository;

    PriceAggregationService priceAggregationService;
    BookingFinancialService bookingFinancialService;
    InvoiceService invoiceService;
    TaxCategoryRepository taxCategoryRepository;


    // =========================================================================
    // BƯỚC 17: CHECK-IN
    // =========================================================================
    @Override
    @Transactional
    public void processCheckIn(Long bookingId, CheckInRequest request) {
        log.info("Bắt đầu xử lý Check-in và đối chiếu khách cho Booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }

        OffsetDateTime now = OffsetDateTime.now();
        booking.setStatus(BookingStatus.CHECKED_IN);

        // Chuyển Request thành Map để dễ dàng tra cứu theo bookingDetailId
        Map<Long, List<CheckInRequest.GuestInfo>> requestGuestMap = request.getRooms().stream()
                .collect(Collectors.toMap(
                        CheckInRequest.RoomGuestDeclaration::getBookingDetailId,
                        CheckInRequest.RoomGuestDeclaration::getGuests
                ));

        for (BookingDetail detail : booking.getBookingDetails()) {

            // 1.Kiểm tra đã gán đủ phòng vật lý chưa
            long assignedRooms = bookingRoomRepository.countByBookingDetailId(detail.getId());
            if (assignedRooms < detail.getQuantity()) {
                log.error("Thiếu phòng vật lý cho Detail {}. Yêu cầu: {}, Đã gán: {}", detail.getId(), detail.getQuantity(), assignedRooms);
                throw new AppException(ErrorCode.ROOM_NOT_FULLY_ASSIGNED);
            }

            // 2. Lấy danh sách khách Lễ tân khai báo cho Detail này
            List<CheckInRequest.GuestInfo> declaredGuests = requestGuestMap.get(detail.getId());
            if (declaredGuests == null || declaredGuests.isEmpty()) {
                throw new AppException(ErrorCode.MISSING_GUEST_DECLARATION);
            }

            // 3. [ĐỐI CHIẾU SỐ LƯỢNG & LOẠI KHÁCH]
            long declaredAdults = declaredGuests.stream().filter(g -> "ADULT".equals(g.getGuestType())).count();
            long declaredChildren = declaredGuests.stream().filter(g -> "CHILD".equals(g.getGuestType())).count();
            long declaredInfants = declaredGuests.stream().filter(g -> "INFANT".equals(g.getGuestType())).count();

            // Kiểm tra tổng số lượng
            if (declaredGuests.size() > detail.getGuestCount()) {
                // Khách mang dư người so với lúc đặt. Lễ tân phải tạo Booking Charge (EXTRA_PERSON) trước khi cho Check-in.
                throw new AppException(ErrorCode.GUEST_COUNT_EXCEEDED);
            }

            // Kiểm tra chi tiết loại khách (Ví dụ: Đặt 1 người lớn, nhưng khai báo 2 người lớn)
            if (declaredAdults > detail.getAdultCount() || declaredChildren > detail.getChildCount()) {
                throw new AppException(ErrorCode.GUEST_TYPE_MISMATCH);
            }

            // 4. Lưu khách vào bảng `booking_guests` để lưu vết khai báo
            for (CheckInRequest.GuestInfo gInfo : declaredGuests) {
                if ("ADULT".equals(gInfo.getGuestType())) {
                    if (gInfo.getIdentityType() == null ||
                            gInfo.getIdentityNumber() == null ||
                            gInfo.getIdentityNumber().trim().isEmpty()) {
                        throw new AppException(ErrorCode.ADULT_MISSING_IDENTITY);
                    }
                }

                BookingGuest bookingGuest = BookingGuest.builder()
                        .bookingDetail(detail)
                        .guest(gInfo.getGuestId() != null ? guestRepository.getReferenceById(gInfo.getGuestId()) : null)
                        .fullName(gInfo.getFullName())
                        .guestType(GuestType.valueOf(gInfo.getGuestType()))
                        .birthDate(gInfo.getBirthDate())
                        .identityType(gInfo.getIdentityType())
                        .identityNumber(gInfo.getIdentityNumber())
                        .build();
                bookingGuestRepository.save(bookingGuest);
            }

            // 5. Cập nhật thời gian Check-in
            detail.setActualCheckInAt(now);

            // 6. Cập nhật trạng thái Slot & Room Instance
            List<RoomSlot> slots = roomSlotRepository.findByBookingDetailId(detail.getId());
            for (RoomSlot slot : slots) {
                if (RoomSlotStatus.RESERVED.equals(slot.getStatus()) || RoomSlotStatus.BLOCKED.equals(slot.getStatus())) {
                    slot.setStatus(RoomSlotStatus.OCCUPIED);
                }

                RoomInstance room = slot.getRoomInstance();
                if (!RoomInstanceStatus.OCCUPIED.equals(room.getCurrentStatus())) {
                    room.setCurrentStatus(RoomInstanceStatus.OCCUPIED);
                    roomInstanceRepository.save(room);
                }
            }
            roomSlotRepository.saveAll(slots);
        }

        bookingRepository.save(booking);
        log.info("Check-in thành công Booking ID: {}", bookingId);
    }

    // =========================================================================
    // BƯỚC 18: GHI NHẬN PHỤ PHÍ / POS (IN-STAY CHARGES)
    // =========================================================================
    @Override
    @Transactional
    public void addInStayCharge(Long bookingId, AddBookingChargeRequest request) {
        // 1. Lấy thông tin phòng đang ở
        BookingDetail detail = bookingDetailRepository.findById(request.getBookingDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_DETAIL_NOT_FOUND));

        ChargeType type = ChargeType.valueOf(request.getChargeType());

        // Chặn không cho gọi PACKAGE_ITEM qua API này (Bảo vệ luồng)
        if (ChargeType.PACKAGE_ITEM.equals(type)) {
            throw new AppException(ErrorCode.PACKAGE_ITEM_MANUAL_ADD_NOT_ALLOWED); // "Không thể thêm gói mặc định tại quầy"
        }

        // 2. Khởi tạo các biến chứa kết quả
        BigDecimal unitPrice = BigDecimal.ZERO;
        BigDecimal vatRate = BigDecimal.ZERO;
        String itemName = request.getItemName();
        CatalogItem catalogItem = null;

        // ==============================================================
        // 3. ĐỊNH TUYẾN TÌM GIÁ (SMART PRICING ROUTER)
        // ==============================================================
        switch (type) {

            // NHÓM 1: DỊCH VỤ MENU (Lấy từ Database Catalog/Mapping)
            case EXTRA_SERVICE:
                if (request.getCatalogItemId() == null) throw new AppException(ErrorCode.CATALOG_ITEM_ID_NOT_NULL);

                catalogItem = catalogItemRepository.findById(request.getCatalogItemId())
                        .orElseThrow(() -> new AppException(ErrorCode.CATALOG_ITEM_NOT_FOUND));
                itemName = catalogItem.getName();
                vatRate = taxCalculatorService.getTaxRate(catalogItem.getTaxCategory().getId(), LocalDate.now());

                Optional<HotelRoomTypeCatalogItem> mappedItem = hotelRoomTypeCatalogItemRepository
                        .findByHotelRoomTypeIdAndCatalogItemId(detail.getHotelRoomType().getId(), catalogItem.getId());

                unitPrice = mappedItem.isPresent() ? mappedItem.get().getPrice() : catalogItem.getBasePrice();
                break;

            // NHÓM 2: PHỤ THU QUY ĐỊNH (Lấy từ Surcharge Rules - Hỗ trợ Đa mức JSONB)
            case EXTRA_PERSON:
            case EXTRA_BED:
            case EARLY_CHECKIN:
            case LATE_CHECKOUT:

                // 1. Lấy toàn bộ rule đang kích hoạt của Hạng phòng này
                List<SurchargeRule> rules = surchargeRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(detail.getHotelRoomType().getId());

                // 2. Lọc ra các Rule trùng loại và đang trong kỳ hạn hiệu lực
                List<SurchargeRule> matchingRules = rules.stream()
                        .filter(r -> r.getStatus() == ActiveStatus.ACTIVE
                                && r.getRuleType().name().equals(type.name())
                                && (r.getStartDate() == null || !LocalDate.now().isBefore(r.getStartDate()))
                                && (r.getEndDate() == null || !LocalDate.now().isAfter(r.getEndDate())))
                        .toList();

                if (matchingRules.isEmpty()) {
                    throw new AppException(ErrorCode.ACTIVE_SURCHARGE_RULE_NOT_FOUND); // "Không có cấu hình phụ thu cho loại này!"
                }

                SurchargeRule activeRule = null;

                // ==========================================
                // THÀNH PHẦN A: XỬ LÝ PHỤ THU NGƯỜI NGOÀI (EXTRA_PERSON)
                // ==========================================
                if (ChargeType.EXTRA_PERSON.equals(type)) {
                    if (request.getGuestType() == null || request.getGuestType().isBlank()) {
                        throw new AppException(ErrorCode.MISSING_GUEST_TYPE_FOR_SURCHARGE);
                    }

                    activeRule = matchingRules.stream()
                            .filter(r -> r.getAgePolicy() != null
                                    && request.getGuestType().equalsIgnoreCase(r.getAgePolicy().getGuestType().toString()))
                            .findFirst()
                            .orElseThrow(() -> new AppException(ErrorCode.GUEST_TYPE_SURCHARGE_POLICY_NOT_FOUND));

                    if (request.getNewGuestFullName() == null || request.getNewGuestFullName().isBlank() || request.getNewGuestIdentityType() == null) {
                        throw new AppException(ErrorCode.MISSING_EXTRA_GUEST_IDENTITY_INFO);
                    }

                    BookingGuest newGuest = BookingGuest.builder()
                            .bookingDetail(detail)
                            .fullName(request.getNewGuestFullName())
                            .guestType(GuestType.valueOf(request.getGuestType().toUpperCase()))
                            .identityType(request.getNewGuestIdentityType())
                            .identityNumber(request.getNewGuestIdentityNumber())
                            .birthDate(request.getNewGuestBirthDate())
                            .build();
                    bookingGuestRepository.save(newGuest);

                    int addedQty = request.getQuantity() != null ? request.getQuantity() : 1;
                    detail.setGuestCount((short) (detail.getGuestCount() + addedQty));
                    if ("ADULT".equalsIgnoreCase(request.getGuestType())) {
                        detail.setAdultCount((short) (detail.getAdultCount() + addedQty));
                    } else if ("CHILD".equalsIgnoreCase(request.getGuestType())) {
                        detail.setChildCount((short) (detail.getChildCount() + addedQty));
                    } else {
                        detail.setInfantCount((short) (detail.getInfantCount() + addedQty));
                    }
                    bookingDetailRepository.save(detail);
                    itemName = "Phụ thu thêm người (" + request.getGuestType() + ")";

                    // Tính giá trị của EXTRA_PERSON theo rule gốc
                    if (com.hotel.booking.modules.pricing.enums.AdjustmentType.FIXED == activeRule.getAdjustmentType()) {
                        unitPrice = activeRule.getAdjustmentValue();
                    } else {
                        unitPrice = detail.getHotelRoomType().getBasePrice()
                                .multiply(activeRule.getAdjustmentValue())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }
                }
                // ==========================================
                // THÀNH PHẦN B: XỬ LÝ GIƯỜNG PHỤ (EXTRA_BED)
                // ==========================================
                else if (ChargeType.EXTRA_BED.equals(type)) {
                    activeRule = matchingRules.get(0);
                    itemName = "Phụ thu Giường phụ (Extra Bed)";

                    if (com.hotel.booking.modules.pricing.enums.AdjustmentType.FIXED == activeRule.getAdjustmentType()) {
                        unitPrice = activeRule.getAdjustmentValue();
                    } else {
                        unitPrice = detail.getHotelRoomType().getBasePrice()
                                .multiply(activeRule.getAdjustmentValue())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }
                }
                // ==========================================
                // THÀNH PHẦN C: XỬ LÝ ĐA MỨC JSONB (EARLY_CHECKIN / LATE_CHECKOUT)
                // ==========================================
                else {
                    activeRule = matchingRules.get(0); // Bốc rule cấu hình thời gian của hạng phòng ra

                    double hoursOver = 0.0;
                    if (ChargeType.EARLY_CHECKIN.equals(type)) {
                        itemName = "Phụ thu Check-in sớm";
                        int standardIn = detail.getBooking().getHotel().getCheckInTime() != null
                                ? detail.getBooking().getHotel().getCheckInTime().getHour() : 14;
                        int actualIn = OffsetDateTime.now().getHour();
                        hoursOver = Math.max(0, standardIn - actualIn); // Ví dụ: 14h - 8h = 6 tiếng
                    } else {
                        itemName = "Phụ thu Check-out muộn";
                        int standardOut = detail.getBooking().getHotel().getCheckOutTime() != null
                                ? detail.getBooking().getHotel().getCheckOutTime().getHour() : 12;
                        int actualOut = OffsetDateTime.now().getHour();
                        hoursOver = Math.max(0, actualOut - standardOut); // Ví dụ: 16h - 12h = 4 tiếng
                    }

                    // CHỐT CHẶN AN TOÀN: Nếu không bị lệch giờ (đến chuẩn giờ hoặc muộn hơn) thì KHÔNG TÍNH PHẠT
                    if (hoursOver <= 0) {
                        log.info("Khách không vi phạm giờ quy định, không phát sinh phụ thu.");
                        return; // THOÁT LUỒNG KHỎI PHẢI TẠO CHARGE
                    }

                    var conditions = activeRule.getConditions();
                    if (conditions == null || conditions.getTimeTiers() == null || conditions.getTimeTiers().isEmpty()) {
                        throw new AppException(ErrorCode.INVALID_SURCHARGE_TIME_TIER_CONFIG); // "Chưa cấu hình mảng đa mức cho thời gian!"
                    }

                    // QUÉT MẢNG JSONB TÌM BẬC PHẠT (TIME TIER) PHÙ HỢP
                    com.hotel.booking.modules.pricing.entity.pojo.TimeTier winningTier = null;

                    // Sắp xếp các mốc upToHours tăng dần để quét từ nhỏ đến lớn
                    List<com.hotel.booking.modules.pricing.entity.pojo.TimeTier> sortedTiers = conditions.getTimeTiers().stream()
                            .sorted(Comparator.comparing(t -> t.getUpToHours() == null ? Double.MAX_VALUE : t.getUpToHours()))
                            .toList();

                    for (var tier : sortedTiers) {
                        if (tier.getUpToHours() == null || hoursOver <= tier.getUpToHours()) {
                            winningTier = tier;
                            break; // Tìm thấy mốc chặn trên phù hợp là dừng ngay!
                        }
                    }

                    if (winningTier == null) {
                        throw new AppException(ErrorCode.NO_MATCHING_TIME_TIER_FOUND); // "Không tìm thấy khung phạt phù hợp"
                    }

                    // TÍNH TOÁN TIỀN PHẠT DỰA TRÊN TIME TIER BỐC ĐƯỢC
                    if (com.hotel.booking.modules.pricing.enums.AdjustmentType.FIXED == winningTier.getAdjustmentType()) {
                        unitPrice = winningTier.getAdjustmentValue();
                    } else {
                        // Nếu phạt theo PERCENT, nhân với giá gốc BasePrice của hạng phòng
                        unitPrice = detail.getHotelRoomType().getBasePrice()
                                .multiply(winningTier.getAdjustmentValue())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }

                    itemName += " (Mức phạt: " + hoursOver + " giờ)";
                }

                // Thuế gánh của phụ thu đi theo thuế tiền phòng gốc
                vatRate = detail.getVatRate();
                break;

            // NHÓM 3: PHÁT SINH THỦ CÔNG (Lấy từ Payload Frontend truyền lên)
            case PRICE_ADJUSTMENT:
            case OTHER:
                if (request.getUnitPrice() == null || request.getVatRate() == null) {
                    throw new AppException(ErrorCode.MISSING_MANUAL_CHARGE_AMOUNT); // "Phải nhập giá và thuế thủ công"
                }
                unitPrice = request.getUnitPrice();
                vatRate = request.getVatRate();
                if (itemName == null || itemName.isBlank()) itemName = "Phí khác (" + request.getDescription() + ")";
                break;
        }

        // ==============================================================
        // 4. TÍNH TOÁN & LƯU TRỮ
        // ==============================================================
        int finalQty = request.getQuantity(); // Dùng biến mới để chứa số lượng chốt cuối cùng
        BigDecimal subtotal;

        // Xử lý nhân hệ số "Số đêm" (Per Night) cho Người thêm & Giường phụ
        if (ChargeType.EXTRA_PERSON.equals(type) || ChargeType.EXTRA_BED.equals(type)) {
            LocalDate chargeStartDate = LocalDate.now();
            if (chargeStartDate.isBefore(detail.getCheckInDate())) {
                chargeStartDate = detail.getCheckInDate();
            }

            long nights = ChronoUnit.DAYS.between(chargeStartDate, detail.getCheckOutDate());
            if (nights < 1) nights = 1;

            // CỘNG DỒN SỐ LƯỢNG: Ví dụ 1 giường x 3 đêm = 3 (Bán ra 3 đơn vị giường)
            finalQty = request.getQuantity() * (int) nights;

            subtotal = unitPrice.multiply(BigDecimal.valueOf(finalQty));
            itemName += " (" + nights + " đêm)";
        } else {
            // Các loại phí 1 lần thì giữ nguyên Quantity
            subtotal = unitPrice.multiply(BigDecimal.valueOf(finalQty));
        }

        // A. Kéo Service Fee Rate từ Booking (Đồng bộ tuyệt đối với Invoice)
        BigDecimal serviceFeeRate = detail.getBooking().getServiceFeeRate();
        if (serviceFeeRate == null) serviceFeeRate = BigDecimal.ZERO;

        // B. Tính Phí dịch vụ (Dựa trên Subtotal)
        BigDecimal chargeServiceFee = subtotal.multiply(serviceFeeRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // C. Giá trị chịu thuế = Subtotal + Service Fee
        BigDecimal taxableAmount = subtotal.add(chargeServiceFee);

        // D. Tính VAT (Đánh trên Taxable Amount)
        BigDecimal vatAmount = taxableAmount.multiply(vatRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // E. Tổng tiền = Taxable Amount + VAT
        BigDecimal totalAmount = taxableAmount.add(vatAmount);

        BookingCharge charge = BookingCharge.builder()
                .bookingDetail(detail)
                .bookingGuest(request.getBookingGuestId() != null
                        ? bookingGuestRepository.getReferenceById(request.getBookingGuestId()) : null)
                .catalogItem(catalogItem)
                .chargeType(type)
                .itemName(itemName)
                .description(request.getDescription())
                .quantity(finalQty)
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .vatRate(vatRate)
                .vatAmount(vatAmount)
                .totalAmount(totalAmount)
                .issuedAt(OffsetDateTime.now())
                .build();

        bookingChargeRepository.save(charge);

        // ==============================================================
        // 5. Cập nhật tiền Real-time cho Booking tổng
        // ==============================================================
        bookingFinancialService.addAmountToBooking(
                bookingId,
                subtotal,
                chargeServiceFee,
                vatAmount,
                totalAmount
        );

        // ==============================================================
        // 6. CHÈN VÀO INVOICE DETAIL ĐỂ LÊN BILL
        // ==============================================================
        Invoice invoice = invoiceRepository.findByBookingIdAndStatus(bookingId, InvoiceStatus.DRAFT)
                .orElseGet(() -> invoiceRepository.findTopByBookingIdOrderByCreatedAtDesc(bookingId)
                        .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND)));

        InvoiceLineType lineType = switch (type) {
            case EXTRA_SERVICE -> InvoiceLineType.EXTRA_SERVICE;
            case EXTRA_PERSON, EXTRA_BED, EARLY_CHECKIN, LATE_CHECKOUT -> InvoiceLineType.SURCHARGE;
            case PRICE_ADJUSTMENT, OTHER -> InvoiceLineType.OTHER;
            default -> throw new AppException(ErrorCode.INVALID_CHARGE_TYPE_FOR_INVOICE);
        };

        invoiceService.addLine(
                invoice.getId(),
                lineType,
                itemName,
                finalQty, // <--- NÉM SỐ LƯỢNG ĐÃ NHÂN ĐÊM VÀO ĐỂ INVOICE TÍNH ĐÚNG TOÁN HỌC
                unitPrice,
                vatRate
        );
    }

    // =========================================================================
    // BƯỚC 19: CHECK-OUT & CHỐT HÓA ĐƠN (FINALIZATION)
    // =========================================================================
    @Override
    @Transactional
    public void processCheckOut(Long bookingId) {
        log.info("Bắt đầu xử lý Check-out cho Booking ID: {}", bookingId);

        // 1. VALIDATE TRẠNG THÁI
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!BookingStatus.CHECKED_IN.equals(booking.getStatus())) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }

        BigDecimal totalDebt = Optional.ofNullable(booking.getTotalAmount()).orElse(BigDecimal.ZERO);
        List<Payment> successfulPayments = paymentRepository.findByBookingIdAndStatus(bookingId, PaymentStatus.SUCCESS);
        BigDecimal totalPaid = successfulPayments.stream()
                .map(Payment::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebt.compareTo(totalPaid) > 0) {
            BigDecimal remaining = totalDebt.subtract(totalPaid);

            // Tự động tạo bản ghi thanh toán cho số tiền còn thiếu
            Payment autoPayment = Payment.builder()
                    .booking(booking)
                    .totalAmount(remaining)
                    .paymentMethod(PaymentMethod.BANK_TRANSFER)
                    .status(PaymentStatus.SUCCESS)
                    .paidAt(OffsetDateTime.now())
                    .build();
            paymentRepository.save(autoPayment);

            Invoice draftInvoice = invoiceRepository.findByBookingIdAndStatus(bookingId, InvoiceStatus.DRAFT)
                    .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

            // Chèn dòng cấn trừ vào hóa đơn
            invoiceService.addLine(
                    draftInvoice.getId(),
                    InvoiceLineType.PAYMENT,
                    "Thanh toán phần còn thiếu tại quầy",
                    1,
                    remaining.negate(),
                    BigDecimal.ZERO
            );

            log.info("Auto-settled remaining balance: {} for booking: {}", remaining, bookingId);
        }

        LocalDate today = LocalDate.now();
        OffsetDateTime now = OffsetDateTime.now();

        // 2. XỬ LÝ KHO PHÒNG & EARLY CHECK-OUT
        for (BookingDetail detail : booking.getBookingDetails()) {
            detail.setActualCheckOutAt(now);

            List<RoomInstance> assignedRooms = bookingRoomRepository.findRoomInstancesByBookingDetailId(detail.getId());

            for (RoomInstance room : assignedRooms) {
                // A. Cập nhật phòng vật lý -> CLEANING
                if (RoomInstanceStatus.OCCUPIED.equals(room.getCurrentStatus())) {
                    room.setCurrentStatus(RoomInstanceStatus.CLEANING);
                    roomInstanceRepository.save(room);
                }

                // B. Xử lý Room Slots (Từng đêm)
                List<RoomSlot> slots = roomSlotRepository.findByRoomInstanceIdAndBookingDetailId(room.getId(), detail.getId());
                for (RoomSlot slot : slots) {
                    if (!slot.getSlotDate().isBefore(today)) {
                        slot.setStatus(RoomSlotStatus.READY);
                        slot.setBookingDetail(null);
                    } else {
                        slot.setStatus(RoomSlotStatus.OCCUPIED);
                    }
                }
                roomSlotRepository.saveAll(slots);
            }

            // C. Cập nhật Inventory (Nhả kho nếu Early Check-out)
            if (today.isBefore(detail.getCheckOutDate())) {
                List<RoomAvailability> availabilities = roomAvailabilityRepository
                        .findByHotelRoomTypeIdAndDateBetween(
                                detail.getHotelRoomType().getId(),
                                today,
                                detail.getCheckOutDate().minusDays(1)
                        );

                for (RoomAvailability avail : availabilities) {
                    avail.setBookedRooms(Math.max(0, avail.getBookedRooms() - detail.getQuantity()));
                }
                roomAvailabilityRepository.saveAll(availabilities);
            }
        }

        // 3. CHỐT HÓA ĐƠN & ĐƠN HÀNG
        booking.setStatus(BookingStatus.CHECKED_OUT);

        Invoice invoice = invoiceRepository.findByBookingIdAndStatus(bookingId, InvoiceStatus.DRAFT)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setIssuedAt(now);

        invoiceRepository.save(invoice);
        bookingRepository.save(booking);

        log.info("Check-out thành công Booking ID: {}. Tổng tiền cuối cùng: {}. Phòng chuyển sang CLEANING.",
                bookingId, booking.getTotalAmount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalkInBookingResponse processWalkInBooking(Short hotelId, WalkInBookingRequest request) {

        String repFullName = request.getRepLastName() + " " + request.getRepFirstName();
        log.info("Xử lý Walk-in Booking cho đại diện: {} (SĐT: {})", repFullName, request.getRepPhone());

        TaxCategory roomTaxCategory = taxCategoryRepository.findAllByIsDeletedFalse().stream()
                .filter(tc -> "ROOM".equalsIgnoreCase(tc.getCategoryCode()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.TAX_CATEGORY_NOT_FOUND));

        // ======================================================================
        // BƯỚC 1: TRA CỨU NGƯỜI ĐẠI DIỆN THEO PHONE )
        // ======================================================================
        Guest repGuest = guestRepository.findByPhone(request.getRepPhone())
                .orElseGet(() -> {
                    Guest newGuest = Guest.builder()
                            .firstName(request.getRepFirstName())
                            .lastName(request.getRepLastName())
                            .fullName(repFullName)
                            .phone(request.getRepPhone())
                            .identityType(request.getRepIdentityType())
                            .identityNumber(request.getRepIdentityNumber())
                            .build();
                    return guestRepository.save(newGuest);
                });

        // ======================================================================
        // BƯỚC 2: KHỞI TẠO BOOKING MASTER & INVOICE DRAFT TRỐNG
        // ======================================================================
        String datePart = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

        // Lấy 6 ký tự đầu của UUID, chuyển thành chữ IN HOA (Ví dụ: 9F2A)
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        String bookingNumber = "BKG-" + datePart + "-" + randomPart;

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        // Lấy Tỷ lệ Phí Dịch Vụ của Khách sạn NGAY TỪ ĐẦU!
        BigDecimal hotelServiceFeeRate = hotel.getServiceFeePercent() != null ? hotel.getServiceFeePercent() : BigDecimal.ZERO;

        Booking booking = Booking.builder()
                .bookingNumber(bookingNumber)
                .guest(repGuest)
                .hotel(hotel)
                .status(BookingStatus.CHECKED_IN)
                .subtotalAmount(BigDecimal.ZERO)
                .serviceFeeRate(hotelServiceFeeRate)
                .serviceFeeAmount(BigDecimal.ZERO)
                .totalVatAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();
        booking = bookingRepository.saveAndFlush(booking);

        // Sinh hóa đơn DRAFT
        Invoice invoice = Invoice.builder()
                .booking(booking)
                .invoiceNumber("INV-" + System.currentTimeMillis())
                .subTotal(BigDecimal.ZERO)
                .serviceFeeRate(hotelServiceFeeRate)
                .serviceFeeAmount(BigDecimal.ZERO)
                .vatAmount(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .status(InvoiceStatus.DRAFT)
                .build();
        invoiceRepository.save(invoice);

        List<WalkInRoomSummaryResponse> roomSummaries = new ArrayList<>();

        // ======================================================================
        // BƯỚC 3: VÒNG LẶP XỬ LÝ TỪNG PHÒNG VẬN HÀNH
        // ======================================================================
        for (WalkInRoomRequest roomReq : request.getRooms()) {

            if (!roomReq.getCheckInDate().equals(LocalDate.now())) {
                log.error("Lỗi: Cố tình Walk-in cho ngày tương lai: {}", roomReq.getCheckInDate());
                throw new AppException(ErrorCode.INVALID_WALKIN_DATE);
            }

            RoomInstance roomInstance = roomInstanceRepository.findById(roomReq.getRoomInstanceId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_INSTANCE_NOT_FOUND));

            // [PESSIMISTIC LOCK 1]: Khóa row cứng các Slot từng đêm
            List<RoomSlot> slots = getOrGenerateSlots(roomInstance, roomReq.getCheckInDate(), roomReq.getCheckOutDate());

            for (RoomSlot slot : slots) {
                if (slot.getStatus() != RoomSlotStatus.READY) {
                    throw new AppException(ErrorCode.ROOM_ALREADY_TAKEN);
                }
            }

            // [PESSIMISTIC LOCK 2]: Khóa row cứng Tồn kho ảo hạng phòng để trừ kho khả dụng
            List<RoomAvailability> availabilities = roomAvailabilityRepository.findAvailabilityForUpdate(
                    roomInstance.getHotelRoomType().getId(), roomReq.getCheckInDate(), roomReq.getCheckOutDate());
            for (RoomAvailability avail : availabilities) {
                if (avail.getAvailableCount() <= 0) {
                    throw new AppException(ErrorCode.OUT_OF_INVENTORY);
                }
                avail.setBookedRooms(avail.getBookedRooms() + 1);
            }

            // ======================================================================
            //MAPPING ADD-ON MỚI CÓ QUANTITY ĐỂ NÉM VÀO MÁY XAY TIỀN
            // ======================================================================
            List<RoomRequest.SelectedAddOn> mappedAddOns = roomReq.getAddOns() != null ?
                    roomReq.getAddOns().stream()
                            .map(addOn -> RoomRequest.SelectedAddOn.builder()
                                    .catalogItemId(addOn.getCatalogItemId())
                                    .quantity(addOn.getQuantity()) // Lấy đúng số lượng khách mua
                                    .build())
                            .toList() : new ArrayList<>();

            RoomRequest virtualRoomReq = RoomRequest.builder()
                    .hotelRoomTypeId(roomInstance.getHotelRoomType().getId())
                    .occupancy(RoomOccupancy.builder()
                            .adults(roomReq.getAdultCount())
                            .children(roomReq.getChildCount())
                            .infants(roomReq.getInfantCount()).build())
                    .addOns(mappedAddOns)
                    .build();

            PricingRequest pricingRequest = PricingRequest.builder()
                    .hotelId(hotelId)
                    .checkIn(roomReq.getCheckInDate())
                    .checkOut(roomReq.getCheckOutDate())
                    .rooms(List.of(virtualRoomReq))
                    .build();

            // XAY TIỀN PHÒNG ĐƠN LẺ!
            PricingResponse priceResp = priceAggregationService.calculatePrice(pricingRequest);
            PricingResponse.PriceDetail roomPriceDetail = priceResp.getRooms().get(0).getPriceDetail();

            BigDecimal roomVatRate = taxCalculatorService.getTaxRate(roomTaxCategory.getId(), roomReq.getCheckInDate());

            // Lưu dữ liệu vào bảng booking_details
            BookingDetail detail = BookingDetail.builder()
                    .booking(booking)
                    .hotelRoomType(roomInstance.getHotelRoomType())
                    .roomTypeName(roomInstance.getHotelRoomType().getRoomType().getName())
                    .quantity((short) 1)
                    .adultCount((short) roomReq.getAdultCount())
                    .childCount((short) roomReq.getChildCount())
                    .infantCount((short) roomReq.getInfantCount())
                    .guestCount((short) (roomReq.getAdultCount() + roomReq.getChildCount()))
                    .checkInDate(roomReq.getCheckInDate())
                    .checkOutDate(roomReq.getCheckOutDate())
                    .actualCheckInAt(OffsetDateTime.now())
                    .roomAmount(roomPriceDetail.getBasePrice()
                            .add(roomPriceDetail.getSurchargeAmount())
                            .subtract(roomPriceDetail.getDiscountAmount()))
                    .discountAmount(roomPriceDetail.getDiscountAmount())
                    .vatRate(roomVatRate)
                    .vatAmount(roomPriceDetail.getTaxAmount())
                    .finalAmount(roomPriceDetail.getFinalPrice())
                    .build();
            detail = bookingDetailRepository.save(detail);

            BookingRoomId bookingRoomId = new BookingRoomId(detail.getId(), roomInstance.getId());

            BookingRoom bookingRoom = BookingRoom.builder()
                    .id(bookingRoomId)
                    .bookingDetail(detail)
                    .roomInstance(roomInstance)
                    .build();
            bookingRoomRepository.save(bookingRoom);

            // SỬA LỖI #2: Save slot vào DB sau khi đổi trạng thái
            roomSlotRepository.saveAll(slots);

            // Lưu danh sách khách thực tế ngủ trong phòng này (booking_guests)
            for (WalkInGuestRequest guestReq : roomReq.getRoomGuests()) {
                BookingGuest bg = BookingGuest.builder()
                        .bookingDetail(detail)
                        .fullName(guestReq.getFullName())
                        .guestType(GuestType.valueOf(guestReq.getGuestType().toUpperCase()))
                        .identityType(guestReq.getIdentityType() != null ? guestReq.getIdentityType() : null)
                        .identityNumber(guestReq.getIdentityNumber())
                        .birthDate(guestReq.getBirthDate())
                        .build();
                bookingGuestRepository.save(bg);
            }

            // Đổi màu các đêm RoomSlot vật lý -> OCCUPIED
            for (RoomSlot slot : slots) {
                slot.setStatus(RoomSlotStatus.OCCUPIED);
                slot.setBookingDetail(detail);
            }

            // Đổi trạng thái hiển thị tức thời của phòng vật lý trên Dashboard sơ đồ
            if (roomReq.getCheckInDate().equals(LocalDate.now())) {
                roomInstance.setCurrentStatus(RoomInstanceStatus.OCCUPIED);
            }

            // Tính toán gốc tiền phòng (roomAmount thuần) và addOnAmount thuần
            BigDecimal roomPureAmount = roomPriceDetail.getBasePrice().add(roomPriceDetail.getSurchargeAmount()).subtract(roomPriceDetail.getDiscountAmount());
            BigDecimal addOnPureAmount = roomPriceDetail.getAddOnAmount();

            // Tính toán cục subtotal của phòng này theo đúng công thức ở Step 5 của máy xay tiền
            BigDecimal roomSubtotalComponent = roomPureAmount.add(addOnPureAmount);

            // ======================================================================
            // BƯỚC 4: BẮN SANG KẾ TOÁN GHI SỔ (SỬ DỤNG BOOKING FINANCIAL SERVICE)
            // ======================================================================
            bookingFinancialService.addAmountToBooking(
                    booking.getId(),
                    roomSubtotalComponent,              // subtotal phát sinh phòng này
                    roomPriceDetail.getServiceFeeAmount(), // serviceFee phát sinh phòng này
                    roomPriceDetail.getTaxAmount(),        // vatAmount phát sinh phòng này
                    roomPriceDetail.getFinalPrice()        // totalAmount phát sinh phòng này
            );

            // Nạp dữ liệu vào mảng tóm tắt in bill
            roomSummaries.add(WalkInRoomSummaryResponse.builder()
                    .bookingDetailId(detail.getId())
                    .roomNumber(roomInstance.getRoomNumber())
                    .roomTypeName(roomInstance.getHotelRoomType().getRoomType().getName())
                    .checkInDate(roomReq.getCheckInDate())
                    .checkOutDate(roomReq.getCheckOutDate())
                    .roomAmount(roomPureAmount)
                    .addOnAmount(addOnPureAmount)
                    .roomServiceFee(roomPriceDetail.getServiceFeeAmount())
                    .roomVatAmount(roomPriceDetail.getTaxAmount())
                    .finalAmount(roomPriceDetail.getFinalPrice())
                    .guestSummary(roomReq.getAdultCount() + " ADULT" + (roomReq.getChildCount() > 0 ? ", " + roomReq.getChildCount() + " CHILD" : ""))
                    .build());

            BigDecimal calculatedVatRate = BigDecimal.ZERO;
            if (roomPriceDetail.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
                // Giá chưa thuế = Final - Tax
                BigDecimal priceBeforeTax = roomPriceDetail.getFinalPrice().subtract(roomPriceDetail.getTaxAmount());
                calculatedVatRate = roomPriceDetail.getTaxAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(priceBeforeTax, 2, RoundingMode.HALF_UP);
            }

            // 1. ADD DÒNG TIỀN PHÒNG
            invoiceService.addLine(
                    invoice.getId(),
                    InvoiceLineType.ROOM,
                    "Tiền phòng: " + roomInstance.getRoomNumber() + " (" + roomReq.getCheckInDate() + " - " + roomReq.getCheckOutDate() + ")",
                    1,
                    roomPureAmount,
                    roomVatRate
            );

            if (roomReq.getAddOns() != null && !roomReq.getAddOns().isEmpty()) {
                for (AddOnRequest addOn : roomReq.getAddOns()) {
                    CatalogItem item = catalogItemRepository.findById(addOn.getCatalogItemId())
                            .orElseThrow(() -> new AppException(ErrorCode.CATALOG_ITEM_NOT_FOUND));

                    // 1. Xác định Giá Item (Ưu tiên giá override của Hạng phòng, nếu không có lấy giá gốc)
                    Optional<HotelRoomTypeCatalogItem> mapped = hotelRoomTypeCatalogItemRepository
                            .findByHotelRoomTypeIdAndCatalogItemId(roomInstance.getHotelRoomType().getId(), addOn.getCatalogItemId());
                    BigDecimal itemPrice = mapped.isPresent() ? mapped.get().getPrice() : item.getBasePrice();

                    // 2. Tính Subtotal
                    BigDecimal qty = BigDecimal.valueOf(addOn.getQuantity());
                    BigDecimal addOnSubtotal = itemPrice.multiply(qty);

                    // 3. Tính Phí dịch vụ (Dựa trên Subtotal)
                    BigDecimal serviceFeeRate = booking.getServiceFeeRate() != null ? booking.getServiceFeeRate() : BigDecimal.ZERO;
                    BigDecimal addOnServiceFee = addOnSubtotal.multiply(serviceFeeRate)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    // 4. Giá trị chịu thuế = Subtotal + Service Fee
                    BigDecimal addOnTaxableAmount = addOnSubtotal.add(addOnServiceFee);

                    // 5. TẬN DỤNG TAX CALCULATOR SERVICE ĐỂ TÍNH THUẾ TỰ ĐỘNG
                    Integer taxCategoryId = item.getTaxCategory().getId();
                    LocalDate checkInDate = roomReq.getCheckInDate();

                    BigDecimal itemVatRate = taxCalculatorService.getTaxRate(taxCategoryId, checkInDate);
                    //Calculate tax trên addOnTaxableAmount (đã có service fee)
                    BigDecimal addOnVatAmount = taxCalculatorService.calculateTax(taxCategoryId, addOnTaxableAmount, checkInDate);

                    BigDecimal addOnTotalAmount = addOnTaxableAmount.add(addOnVatAmount);

                    // 6. Lưu vào bảng booking_charges
                    BookingCharge charge = BookingCharge.builder()
                            .bookingDetail(detail)
                            .catalogItem(item)
                            .chargeType(ChargeType.PACKAGE_ITEM)
                            .itemName("Add-on: " + item.getName())
                            .quantity(addOn.getQuantity())
                            .unitPrice(itemPrice)
                            .subtotal(addOnSubtotal)
                            .vatRate(itemVatRate)
                            .vatAmount(addOnVatAmount)
                            .totalAmount(addOnTotalAmount) // Đã bao gồm cả Phí dịch vụ + VAT
                            .issuedAt(OffsetDateTime.now())
                            .build();
                    bookingChargeRepository.save(charge);

                    // 7. Chèn món vào Hóa đơn (Invoice)
                    invoiceService.addLine(
                            invoice.getId(),
                            InvoiceLineType.PACKAGE_ITEM,
                            "Add-on: " + item.getName(),
                            addOn.getQuantity(),
                            itemPrice,
                            itemVatRate
                    );
                }
            }
        }

        // BƯỚC 4.5: KIỂM TRA THANH TOÁN
        BigDecimal expectedFormTotal = roomSummaries.stream()
                .map(WalkInRoomSummaryResponse::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.getAmountPaid().compareTo(expectedFormTotal) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_PAYMENT);
        }

//        BigDecimal currentTotal = bookingRepository.findById(booking.getId()).get().getTotalAmount();
//        if (request.getAmountPaid().compareTo(currentTotal) < 0) {
//            throw new AppException(ErrorCode.INSUFFICIENT_PAYMENT);
//        }

        // Ghi nhận thanh toán thực tế vào database
        Payment payment = Payment.builder()
                .booking(booking)
                .totalAmount(request.getAmountPaid())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .status(PaymentStatus.SUCCESS)
                .paidAt(OffsetDateTime.now())
                .build();
        paymentRepository.save(payment);

        invoiceService.addLine(
                invoice.getId(),
                InvoiceLineType.PAYMENT,
                "Khách thanh toán tại quầy (" + request.getPaymentMethod() + ")",
                1,
                request.getAmountPaid().negate(), // Số tiền âm (cấn trừ)
                BigDecimal.ZERO
        );

        // ======================================================================
        // BƯỚC 5: ĐỌC DỮ LIỆU ĐÃ ĐỒNG BỘ TỪ DB ĐỂ TRẢ VỀ DTO (FINALIZE)
        // ======================================================================
        // BẮT BUỘC PHẢI REFRESH BOOKING TỪ DB ĐỂ LẤY TỔNG TIỀN MỚI NHẤT (Bao gồm cả phí Early Check-in vừa tự động cộng ngầm)
        Booking refreshedBooking = bookingRepository.findById(booking.getId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        BigDecimal grandTotal = refreshedBooking.getTotalAmount();

        BigDecimal remainingBalance = grandTotal.subtract(request.getAmountPaid());

        return WalkInBookingResponse.builder()
                .bookingId(refreshedBooking.getId())
                .bookingNumber(refreshedBooking.getBookingNumber())
                .invoiceNumber(invoice.getInvoiceNumber())
                .repFullName(repGuest.getFullName())
                .status(BookingStatus.CHECKED_IN)
                .totalRoomAmount(roomSummaries.stream().map(WalkInRoomSummaryResponse::getRoomAmount).reduce(BigDecimal.ZERO, BigDecimal::add))
                .totalAddOnAmount(roomSummaries.stream().map(WalkInRoomSummaryResponse::getAddOnAmount).reduce(BigDecimal.ZERO, BigDecimal::add))

                // LẤY DỮ LIỆU TỪ REFRESHED BOOKING
                .serviceFeeAmount(refreshedBooking.getServiceFeeAmount())
                .totalVatAmount(refreshedBooking.getTotalVatAmount())
                .grandTotal(grandTotal)

                .amountPaid(request.getAmountPaid())
                .remainingBalance(remainingBalance) // Trả về số tiền nợ chuẩn đét
                .roomSummaries(roomSummaries)
                .build();
    }

    private List<RoomSlot> getOrGenerateSlots(RoomInstance room, LocalDate checkIn, LocalDate checkOut) {
        List<RoomSlot> existingSlots = roomSlotRepository.findSlotsForUpdate(room.getId(), checkIn, checkOut);

        // Mảng kết quả cuối cùng
        List<RoomSlot> allSlots = new ArrayList<>();

        LocalDate current = checkIn;
        while (current.isBefore(checkOut)) {
            final LocalDate date = current;

            // Tìm slot đã có trong DB
            RoomSlot slot = existingSlots.stream()
                    .filter(s -> s.getSlotDate().equals(date))
                    .findFirst()
                    .orElseGet(() -> {
                        // NẾU CHƯA CÓ -> THÌ ĐÂY CHÍNH LÀ READY
                        // Mày có thể tạo mới ngay ở đây hoặc trả về 1 Object ảo
                        return RoomSlot.builder()
                                .roomInstance(room)
                                .slotDate(date)
                                .status(RoomSlotStatus.READY)
                                .build();
                    });
            allSlots.add(slot);
            current = current.plusDays(1);
        }
        return  allSlots;
    }
}