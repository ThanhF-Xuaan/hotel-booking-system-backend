package com.hotel.booking.modules.pricing.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
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
@Schema(description = "Yêu cầu tạo mới quy tắc phụ phí (SurchargeRule)")
public class SurchargeRuleCreateRequest {

    @NotNull(message = "SURCHARGE_RULE_HOTEL_ROOM_TYPE_ID_NOT_NULL")
    @Schema(description = "ID của loại phòng khách sạn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer hotelRoomTypeId;

    @NotNull(message = "SURCHARGE_RULE_RULE_TYPE_NOT_NULL")
    @Schema(description = "Loại phụ phí (EXTRA_PERSON, EXTRA_BED, EARLY_CHECKIN, LATE_CHECKOUT)", example = "EXTRA_BED", requiredMode = Schema.RequiredMode.REQUIRED)
    SurchargeRuleType ruleType;

    @Schema(description = "ID của chính sách độ tuổi áp dụng (bắt buộc đối với EXTRA_PERSON)", example = "1")
    Short agePolicyId;

    @Schema(description = "Điều kiện phụ phí", requiredMode = Schema.RequiredMode.REQUIRED)
    SurchargeConditionRequest conditions;

    @NotNull(message = "SURCHARGE_RULE_ADJUSTMENT_TYPE_NOT_NULL")
    @Schema(description = "Loại điều chỉnh (PERCENT, FIXED)", example = "FIXED", requiredMode = Schema.RequiredMode.REQUIRED)
    AdjustmentType adjustmentType;

    @NotNull(message = "SURCHARGE_RULE_ADJUSTMENT_VALUE_NOT_NULL")
    @Schema(description = "Giá trị phụ phí (phải >= 0)", example = "200000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal adjustmentValue;

    @NotNull(message = "SURCHARGE_RULE_START_DATE_NOT_NULL")
    @Schema(description = "Ngày bắt đầu hiệu lực", example = "2026-07-01", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate startDate;

    @NotNull(message = "SURCHARGE_RULE_END_DATE_NOT_NULL")
    @Schema(description = "Ngày kết thúc hiệu lực", example = "2026-08-31", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate endDate;

    @NotNull(message = "SURCHARGE_RULE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}

