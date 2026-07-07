package com.hotel.booking.modules.pricing.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Yêu cầu cập nhật quy tắc giảm giá (DiscountRule)")
public class DiscountRuleUpdateRequest {

    @NotNull(message = "DISCOUNT_RULE_HOTEL_ROOM_TYPE_ID_NOT_NULL")
    @Schema(description = "ID của loại phòng khách sạn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer hotelRoomTypeId;

    @Schema(description = "ID của chiến dịch marketing liên kết (tùy chọn)", example = "1")
    Integer campaignId;

    @NotNull(message = "DISCOUNT_RULE_RULE_TYPE_NOT_NULL")
    @Schema(description = "Mã loại quy tắc giảm giá", example = "LONG_STAY", requiredMode = Schema.RequiredMode.REQUIRED)
    String ruleTypeCode;

    @Valid
    @NotNull(message = "DISCOUNT_RULE_TIER_CONDITIONS_NOT_NULL")
    @Schema(description = "Các điều kiện áp dụng", requiredMode = Schema.RequiredMode.REQUIRED)
    DiscountConditionRequest conditions;

    @NotNull(message = "DISCOUNT_RULE_DISCOUNT_TYPE_NOT_NULL")
    @Schema(description = "Loại giảm giá (PERCENT, FIXED)", example = "PERCENT", requiredMode = Schema.RequiredMode.REQUIRED)
    AdjustmentType discountType;

    @NotNull(message = "DISCOUNT_RULE_DISCOUNT_VALUE_NOT_NULL")
    @Schema(description = "Giá trị giảm giá (phải > 0)", example = "10.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal discountValue;

    @NotNull(message = "DISCOUNT_RULE_START_DATE_NOT_NULL")
    @Schema(description = "Ngày bắt đầu áp dụng", example = "2026-07-01", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate startDate;

    @NotNull(message = "DISCOUNT_RULE_END_DATE_NOT_NULL")
    @Schema(description = "Ngày kết thúc áp dụng", example = "2026-08-31", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate endDate;

    @NotNull(message = "DISCOUNT_RULE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
