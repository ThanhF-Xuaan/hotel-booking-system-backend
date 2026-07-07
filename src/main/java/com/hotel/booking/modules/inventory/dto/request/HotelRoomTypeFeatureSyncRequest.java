package com.hotel.booking.modules.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu đồng bộ danh sách tiện ích cấu hình cho Loại phòng khách sạn (HotelRoomType)")
public class HotelRoomTypeFeatureSyncRequest {

    @NotNull(message = "HOTEL_ROOM_TYPE_FEATURE_IDS_NOT_NULL")
    @Schema(description = "Danh sách ID của các tiện ích phòng", requiredMode = Schema.RequiredMode.REQUIRED)
    List<Short> roomFeatureIds;
}
