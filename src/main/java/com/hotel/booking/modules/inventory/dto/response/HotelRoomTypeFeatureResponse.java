package com.hotel.booking.modules.inventory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Thông tin chi tiết về tiện ích của loại phòng")
public class HotelRoomTypeFeatureResponse {

    @Schema(description = "ID của tiện ích phòng (RoomFeature)", example = "1")
    Short roomFeatureId;

    @Schema(description = "Tên của tiện ích phòng", example = "Ban công")
    String featureName;
}
