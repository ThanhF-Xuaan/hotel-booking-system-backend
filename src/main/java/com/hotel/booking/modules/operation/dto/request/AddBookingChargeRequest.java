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
public class AddChargeRequest {
    private Long bookingDetailId;
    private Long bookingGuestId;  // (Optional) Ai là người gọi dịch vụ
    private Integer catalogItemId; // (Optional) Nếu là món ăn/dịch vụ từ Catalog
    private String chargeType;    // CATALOG_ITEM, EXTRA_PERSON, EARLY_CHECKIN, OTHER
    private String itemName;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal vatRate;
}
