package com.hotel.booking.modules.pricing.dto.response;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
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
@Schema(description = "Thông tin chi tiết quy tắc giá phản hồi cho client")
public class PricingRuleResponse {

    @Schema(description = "ID của quy tắc giá", example = "1")
    Integer id;

    @Schema(description = "ID của loại phòng khách sạn được cấu hình", example = "1")
    Integer hotelRoomTypeId;

    @Schema(description = "ID của lịch ngày lễ liên kết", example = "1")
    Integer holidayCalendarId;

    @Schema(description = "Mã loại quy tắc cấu hình", example = "HOLIDAY")
    String ruleTypeCode;

    @Schema(description = "Loại điều chỉnh (PERCENT, FIXED)", example = "PERCENT")
    AdjustmentType adjustmentType;

    @Schema(description = "Giá trị điều chỉnh", example = "15.00")
    BigDecimal adjustmentValue;

    @Schema(description = "Ngày bắt đầu áp dụng", example = "2026-07-01")
    LocalDate startDate;

    @Schema(description = "Ngày kết thúc áp dụng", example = "2026-07-05")
    LocalDate endDate;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
