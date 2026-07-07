package com.hotel.booking.modules.pricing.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
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
@Schema(description = "Yêu cầu tạo mới PricingRuleTypeConfig")
public class PricingRuleTypeCreateRequest {

    @NotBlank(message = "PRICING_RULE_TYPE_CODE_NOT_BLANK")
    @Schema(description = "Mã loại quy tắc giá (Primary Key)", example = "WEEKEND", requiredMode = Schema.RequiredMode.REQUIRED)
    String code;

    @NotBlank(message = "PRICING_RULE_TYPE_NAME_NOT_BLANK")
    @Schema(description = "Tên hiển thị loại quy tắc", example = "Quy tắc cuối tuần", requiredMode = Schema.RequiredMode.REQUIRED)
    String displayName;

    @NotNull(message = "PRICING_RULE_TYPE_PRIORITY_NOT_NULL")
    @Schema(description = "Độ ưu tiên áp dụng", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    Short priority;

    @NotNull(message = "PRICING_RULE_TYPE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
