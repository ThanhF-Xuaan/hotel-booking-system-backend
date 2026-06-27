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
@Schema(description = "Yêu cầu cập nhật DiscountRuleTypeConfig")
public class DiscountRuleTypeUpdateRequest {

    @NotBlank(message = "DISCOUNT_RULE_TYPE_NAME_NOT_BLANK")
    @Schema(description = "Tên hiển thị loại quy tắc", example = "Quy tắc ở dài ngày", requiredMode = Schema.RequiredMode.REQUIRED)
    String displayName;

    @NotNull(message = "DISCOUNT_RULE_TYPE_PRIORITY_NOT_NULL")
    @Schema(description = "Độ ưu tiên áp dụng", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    Short priority;
}
