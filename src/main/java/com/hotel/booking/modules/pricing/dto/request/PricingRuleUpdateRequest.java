package com.hotel.booking.modules.pricing.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu cập nhật quy tắc giá (PricingRule)")
public class PricingRuleUpdateRequest {

    @NotNull(message = "PRICING_RULE_HOTEL_ROOM_TYPE_ID_NOT_NULL")
    @Schema(description = "ID của loại phòng khách sạn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer hotelRoomTypeId;

    @Schema(description = "ID của lịch ngày lễ (chỉ truyền khi ruleType = HOLIDAY)", example = "1")
    Integer holidayCalendarId;

    @NotNull(message = "PRICING_RULE_RULE_TYPE_NOT_NULL")
    @Schema(description = "Mã loại quy tắc cấu hình", example = "HOLIDAY", requiredMode = Schema.RequiredMode.REQUIRED)
    String ruleTypeCode;

    @NotNull(message = "PRICING_RULE_ADJUSTMENT_TYPE_NOT_NULL")
    @Schema(description = "Loại điều chỉnh (PERCENT, FIXED)", example = "PERCENT", requiredMode = Schema.RequiredMode.REQUIRED)
    AdjustmentType adjustmentType;

    @NotNull(message = "PRICING_RULE_ADJUSTMENT_VALUE_NOT_NULL")
    @Schema(description = "Giá trị điều chỉnh (phải > 0)", example = "15.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal adjustmentValue;

    @NotNull(message = "PRICING_RULE_START_DATE_NOT_NULL")
    @Schema(description = "Ngày bắt đầu áp dụng", example = "2026-07-01", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate startDate;

    @NotNull(message = "PRICING_RULE_END_DATE_NOT_NULL")
    @Schema(description = "Ngày kết thúc áp dụng", example = "2026-07-05", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate endDate;

    @NotNull(message = "PRICING_RULE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
