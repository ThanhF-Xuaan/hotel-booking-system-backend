package com.hotel.booking.modules.pricing.dto.response;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Thông tin chi tiết chiến dịch marketing phản hồi cho client")
public class CampaignResponse {

    @Schema(description = "ID của chiến dịch", example = "1")
    Integer id;

    @Schema(description = "ID của khách sạn sở hữu chiến dịch", example = "1")
    Short hotelId;

    @Schema(description = "Tên chiến dịch", example = "Mùa hè sôi động")
    String name;

    @Schema(description = "Mô tả chiến dịch", example = "Chiến dịch giảm giá tri ân khách hàng dịp hè")
    String description;

    @Schema(description = "Ngày bắt đầu áp dụng", example = "2026-07-01")
    LocalDate startDate;

    @Schema(description = "Ngày kết thúc áp dụng", example = "2026-08-31")
    LocalDate endDate;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
