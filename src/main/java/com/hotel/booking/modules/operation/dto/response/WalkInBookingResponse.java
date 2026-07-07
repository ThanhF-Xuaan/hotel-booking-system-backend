package com.hotel.booking.modules.operation.dto.response;

import com.hotel.booking.modules.booking.enums.BookingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalkInBookingResponse {

    // ==========================================
    // 1. THÔNG TIN CHUNG (HEADER HÓA ĐƠN)
    // ==========================================
    Long bookingId;
    String bookingNumber; // VD: BKG-20260705-999
    String invoiceNumber; // VD: INV-20260705-888 (Dùng để in bill)

    String repFullName; // Tên người đại diện (Lấy từ bảng Guest)
    BookingStatus status; // Luôn là "CHECKED_IN" với Walk-in

    // ==========================================
    // 2. TỔNG QUAN TÀI CHÍNH (FINANCIAL SUMMARY)
    // ==========================================
    private BigDecimal totalRoomAmount;  // (1) Tổng tiền phòng thuần (Đã tính Phụ thu, trừ Khuyến mãi)
    private BigDecimal totalAddOnAmount; // (2) Tổng tiền dịch vụ bán kèm (PACKAGE_ITEM)

    private BigDecimal serviceFeeAmount; // (4) = (3) * ServiceFee%
    private BigDecimal totalVatAmount;   // (5) = (3 + 4) * VAT%

    private BigDecimal grandTotal;       // TỔNG CỘNG: (3) + (4) + (5)

    private BigDecimal amountPaid;       // Số tiền Lễ tân thực thu
    private BigDecimal remainingBalance; // Còn nợ

    // ==========================================
    // 3. CHI TIẾT TỪNG PHÒNG (BODY HÓA ĐƠN)
    // ==========================================
    List<WalkInRoomSummaryResponse> roomSummaries;
}
