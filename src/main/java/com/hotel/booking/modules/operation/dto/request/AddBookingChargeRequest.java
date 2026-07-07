package com.hotel.booking.modules.operation.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddBookingChargeRequest {
    Long bookingDetailId;
    Long bookingGuestId;  // (Optional) Ai là người gọi dịch vụ

    Integer catalogItemId; // BỔ SUNG: Dùng để xác định dịch vụ (Thuê xe, Giặt là...)

    String chargeType;    // EXTRA_PERSON, EARLY_CHECKIN, OTHER

    String guestType;

    String itemName;
    String description;
    Integer quantity;

    BigDecimal unitPrice;
    BigDecimal vatRate;
}
