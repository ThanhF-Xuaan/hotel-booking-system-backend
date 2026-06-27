package com.hotel.booking.modules.pricing.dto.response;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Thông tin chi tiết PricingRuleTypeConfig phản hồi cho client")
public class PricingRuleTypeResponse {

    @Schema(description = "Mã loại quy tắc giá", example = "WEEKEND")
    String code;

    @Schema(description = "Tên hiển thị", example = "Quy tắc cuối tuần")
    String displayName;

    @Schema(description = "Độ ưu tiên áp dụng", example = "10")
    Short priority;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
