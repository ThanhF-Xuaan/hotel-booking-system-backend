package com.hotel.booking.modules.pricing.dto.request;

import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu thông tin một tầng giảm giá trong mô hình chiết khấu theo tầng")
public class DiscountTierRequest {

    @Valid
    @NotNull(message = "DISCOUNT_RULE_TIER_CONDITIONS_NOT_NULL")
    @Schema(description = "Các điều kiện áp dụng cho tầng này", requiredMode = Schema.RequiredMode.REQUIRED)
    DiscountConditionRequest conditions;

    @NotNull(message = "DISCOUNT_RULE_DISCOUNT_TYPE_NOT_NULL")
    @Schema(description = "Loại giảm giá (PERCENT, FIXED)", example = "PERCENT", requiredMode = Schema.RequiredMode.REQUIRED)
    AdjustmentType discountType;

    @NotNull(message = "DISCOUNT_RULE_DISCOUNT_VALUE_NOT_NULL")
    @Schema(description = "Giá trị giảm giá của tầng này", example = "10.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal discountValue;
}
