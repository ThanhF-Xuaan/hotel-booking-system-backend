package com.hotel.booking.modules.pricing.dto.request;

import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeTierRequest {
    Double upToHours;
    AdjustmentType adjustmentType;
    BigDecimal adjustmentValue;
}
