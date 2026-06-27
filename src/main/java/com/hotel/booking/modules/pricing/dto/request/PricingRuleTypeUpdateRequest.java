package com.hotel.booking.modules.pricing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu cập nhật PricingRuleTypeConfig")
public class PricingRuleTypeUpdateRequest {

    @NotBlank(message = "PRICING_RULE_TYPE_NAME_NOT_BLANK")
    @Schema(description = "Tên hiển thị loại quy tắc", example = "Quy tắc cuối tuần", requiredMode = Schema.RequiredMode.REQUIRED)
    String displayName;

    @NotNull(message = "PRICING_RULE_TYPE_PRIORITY_NOT_NULL")
    @Schema(description = "Độ ưu tiên áp dụng", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    Short priority;
}
