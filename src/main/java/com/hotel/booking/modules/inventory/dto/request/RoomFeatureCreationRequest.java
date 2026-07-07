package com.hotel.booking.modules.inventory.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import com.hotel.booking.modules.inventory.enums.FeatureCategory;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO dùng để tạo mới Tiện ích phòng (RoomFeature)")
public class RoomFeatureCreationRequest {

    @NotBlank(message = "ROOM_FEATURE_CODE_NOT_BLANK")
    @Size(max = 50, message = "ROOM_FEATURE_CODE_MAX_LENGTH")
    @Schema(description = "Mã định danh duy nhất của tiện ích", example = "AIR_CONDITIONER", requiredMode = Schema.RequiredMode.REQUIRED)
    String code;

    @NotBlank(message = "ROOM_FEATURE_NAME_NOT_BLANK")
    @Size(max = 150, message = "ROOM_FEATURE_NAME_MAX_LENGTH")
    @Schema(description = "Tên tiện ích", example = "Điều hòa nhiệt độ", requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @Schema(description = "Tên Icon hiển thị", example = "ac_unit")
    String icon;

    @Schema(description = "Nhóm danh mục tiện ích", example = "AMENITY")
    FeatureCategory category;

    @Builder.Default
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status = ActiveStatus.ACTIVE;
}
