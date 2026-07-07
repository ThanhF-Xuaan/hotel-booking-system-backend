package com.hotel.booking.modules.pricing.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu cập nhật thông tin chiến dịch marketing (Campaign)")
public class CampaignUpdateRequest {

    @NotNull(message = "CAMPAIGN_HOTEL_ID_NOT_NULL")
    @Schema(description = "ID của khách sạn sở hữu chiến dịch", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Short hotelId;

    @NotBlank(message = "CAMPAIGN_NAME_NOT_BLANK")
    @Schema(description = "Tên chiến dịch", example = "Mùa hè sôi động", requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @Schema(description = "Mô tả chiến dịch", example = "Chiến dịch giảm giá tri ân khách hàng dịp hè")
    String description;

    @NotNull(message = "CAMPAIGN_START_DATE_NOT_NULL")
    @Schema(description = "Ngày bắt đầu áp dụng", example = "2026-07-01", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate startDate;

    @NotNull(message = "CAMPAIGN_END_DATE_NOT_NULL")
    @Schema(description = "Ngày kết thúc áp dụng", example = "2026-08-31", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate endDate;

    @NotNull(message = "CAMPAIGN_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
