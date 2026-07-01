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

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Thông tin chi tiết chính sách độ tuổi của khách sạn")
public class HotelAgePolicyResponse {

    @Schema(description = "ID của chính sách độ tuổi", example = "1")
    Short id;

    @Schema(description = "ID của khách sạn", example = "1")
    Short hotelId;

    @Schema(description = "Loại khách", example = "CHILD")
    String guestType;

    @Schema(description = "Tuổi tối thiểu", example = "6")
    Short minAge;

    @Schema(description = "Tuổi tối đa", example = "11")
    Short maxAge;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;

    @Schema(description = "Thời gian tạo")
    OffsetDateTime createdAt;

    @Schema(description = "Thời gian cập nhật")
    OffsetDateTime updatedAt;
}
