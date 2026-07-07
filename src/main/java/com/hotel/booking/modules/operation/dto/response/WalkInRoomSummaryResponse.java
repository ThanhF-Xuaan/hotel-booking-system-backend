package com.hotel.booking.modules.operation.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalkInRoomSummaryResponse {
    Long bookingDetailId;

    String roomNumber;     // VD: "101"
    String roomTypeName;   // VD: "Standard Double"

    // Lịch trình của phòng này
    LocalDate checkInDate;
    LocalDate checkOutDate;

    private BigDecimal roomAmount;   // Tiền phòng của riêng phòng này
    private BigDecimal addOnAmount;  // Tiền dịch vụ bán kèm của riêng phòng này

    BigDecimal roomServiceFee;
    BigDecimal roomVatAmount;

    // Số tiền của RIÊNG phòng này (Đã tính VAT, Phụ thu)
    BigDecimal finalAmount;

    // (Tùy chọn) Ghi chú nhỏ trên bill, ví dụ: "2 Người lớn, 1 Trẻ em"
    String guestSummary;
}
