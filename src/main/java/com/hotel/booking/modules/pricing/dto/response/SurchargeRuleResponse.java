package com.hotel.booking.modules.pricing.dto.response;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.pricing.entity.pojo.SurchargeCondition;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Thông tin chi tiết quy tắc phụ phí phản hồi cho client")
public class SurchargeRuleResponse {

    @Schema(description = "ID của quy tắc phụ phí", example = "1")
    Integer id;

    @Schema(description = "ID của loại phòng khách sạn được cấu hình", example = "1")
    Integer hotelRoomTypeId;

    @Schema(description = "Loại phụ phí", example = "EXTRA_BED")
    SurchargeRuleType ruleType;

    @Schema(description = "ID của chính sách độ tuổi áp dụng", example = "1")
    Short agePolicyId;

    @Schema(description = "Điều kiện phụ phí")
    SurchargeCondition conditions;

    @Schema(description = "Loại điều chỉnh (PERCENT, FIXED)", example = "FIXED")
    AdjustmentType adjustmentType;

    @Schema(description = "Giá trị phụ phí", example = "200000.00")
    BigDecimal adjustmentValue;

    @Schema(description = "Ngày bắt đầu hiệu lực", example = "2026-07-01")
    LocalDate startDate;

    @Schema(description = "Ngày kết thúc hiệu lực", example = "2026-08-31")
    LocalDate endDate;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}

