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
@Schema(description = "Thông tin chi tiết DiscountRuleTypeConfig phản hồi cho client")
public class DiscountRuleTypeResponse {

    @Schema(description = "Mã loại quy tắc giảm giá", example = "LONG_STAY")
    String code;

    @Schema(description = "Tên hiển thị", example = "Quy tắc ở dài ngày")
    String displayName;

    @Schema(description = "Độ ưu tiên áp dụng", example = "20")
    Short priority;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
