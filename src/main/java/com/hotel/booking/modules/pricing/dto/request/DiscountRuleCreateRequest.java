package com.hotel.booking.modules.pricing.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Yêu cầu tạo mới quy tắc giảm giá theo tầng (DiscountRule)")
public class DiscountRuleCreateRequest {

    @Schema(description = "ID của chiến dịch marketing liên kết (tùy chọn)", example = "1")
    Integer campaignId;

    @NotBlank(message = "DISCOUNT_RULE_RULE_TYPE_NOT_NULL")
    @Schema(description = "Mã loại quy tắc giảm giá", example = "LONG_STAY", requiredMode = Schema.RequiredMode.REQUIRED)
    String ruleTypeCode;

    @NotNull(message = "DISCOUNT_RULE_START_DATE_NOT_NULL")
    @Schema(description = "Ngày bắt đầu áp dụng", example = "2026-07-01", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate startDate;

    @NotNull(message = "DISCOUNT_RULE_END_DATE_NOT_NULL")
    @Schema(description = "Ngày kết thúc áp dụng", example = "2026-08-31", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate endDate;

    @NotEmpty(message = "DISCOUNT_RULE_ROOM_TYPES_REQUIRED")
    @Schema(description = "Danh sách ID loại phòng được áp dụng quy tắc này", requiredMode = Schema.RequiredMode.REQUIRED)
    Set<Integer> appliedRoomTypeIds;

    @Valid
    @NotEmpty(message = "DISCOUNT_RULE_TIERS_REQUIRED")
    @Schema(description = "Danh sách các tầng giảm giá cấu hình", requiredMode = Schema.RequiredMode.REQUIRED)
    List<DiscountTierRequest> tiers;

    @NotNull(message = "DISCOUNT_RULE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
