package com.hotel.booking.modules.pricing.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Thông tin chi tiết quy tắc giảm giá phản hồi cho client")
public class DiscountRuleResponse {

    @Schema(description = "ID của quy tắc giảm giá", example = "1")
    Integer id;

    @Schema(description = "ID của loại phòng khách sạn được cấu hình", example = "1")
    Integer hotelRoomTypeId;

    @Schema(description = "Tên của loại phòng khách sạn được cấu hình", example = "Deluxe Room")
    String hotelRoomTypeName;

    @Schema(description = "ID của chiến dịch liên kết (nếu có)", example = "1")
    Integer campaignId;

    @Schema(description = "Mã loại quy tắc giảm giá", example = "LONG_STAY")
    String ruleTypeCode;

    @Schema(description = "Các điều kiện đi kèm của quy tắc", requiredMode = Schema.RequiredMode.REQUIRED)
    DiscountConditionResponse conditions;

    @Schema(description = "Loại giảm giá (PERCENT, FIXED)", example = "PERCENT")
    AdjustmentType discountType;

    @Schema(description = "Giá trị giảm giá", example = "10.00")
    BigDecimal discountValue;

    @Schema(description = "Ngày bắt đầu áp dụng", example = "2026-07-01")
    LocalDate startDate;

    @Schema(description = "Ngày kết thúc áp dụng", example = "2026-08-31")
    LocalDate endDate;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
