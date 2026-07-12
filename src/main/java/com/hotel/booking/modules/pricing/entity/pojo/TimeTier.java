package com.hotel.booking.modules.pricing.entity.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TimeTier {

    // Số giờ tối đa của bậc này (Ví dụ: 5.0 -> Dưới 5 tiếng)
    // Nếu null nghĩa là mốc cuối cùng
    @JsonProperty("up_to_hours")
    Double upToHours;

    @JsonProperty("adjustment_type")
    AdjustmentType adjustmentType;

    @JsonProperty("adjustment_value")
    BigDecimal adjustmentValue;
}
