package com.hotel.booking.modules.inventory.dto.response;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO phản hồi thông tin chi tiết của Tiện ích phòng (RoomFeature)")
public class RoomFeatureResponse {

    @Schema(description = "ID nội bộ của tiện ích", example = "1")
    Short id;

    @Schema(description = "Mã tiện ích", example = "AIR_CONDITIONER")
    String code;

    @Schema(description = "Tên tiện ích", example = "Điều hòa nhiệt độ")
    String name;

    @Schema(description = "Tên Icon hiển thị", example = "ac_unit")
    String icon;

    @Schema(description = "Nhóm danh mục tiện ích", example = "AMENITY")
    FeatureCategory category;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
