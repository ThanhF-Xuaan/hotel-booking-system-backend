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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu cập nhật chính sách độ tuổi khách sạn")
public class HotelAgePolicyUpdateRequest {

    @NotNull(message = "AGE_POLICY_HOTEL_ID_NOT_NULL")
    @Schema(description = "ID của khách sạn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Short hotelId;

    @NotBlank(message = "AGE_POLICY_GUEST_TYPE_NOT_BLANK")
    @Schema(description = "Loại khách (ADULT, CHILD, INFANT)", example = "CHILD", requiredMode = Schema.RequiredMode.REQUIRED)
    String guestType;

    @NotNull(message = "AGE_POLICY_MIN_AGE_NOT_NULL")
    @Schema(description = "Tuổi tối thiểu", example = "6", requiredMode = Schema.RequiredMode.REQUIRED)
    Short minAge;

    @NotNull(message = "AGE_POLICY_MAX_AGE_NOT_NULL")
    @Schema(description = "Tuổi tối đa", example = "11", requiredMode = Schema.RequiredMode.REQUIRED)
    Short maxAge;

    @NotNull(message = "AGE_POLICY_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
