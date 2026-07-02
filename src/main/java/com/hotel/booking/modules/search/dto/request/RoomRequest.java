package com.hotel.booking.modules.search.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "Yêu cầu đặt phòng chi tiết sức chứa")
public class RoomRequest {

    @NotNull(message = "ROOM_HOTEL_ROOM_TYPE_ID_NOT_NULL")
    @Schema(description = "ID của cấu hình loại phòng", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer hotelRoomTypeId;

    @NotNull(message = "ROOM_OCCUPANCY_NOT_NULL")
    @Valid
    @Schema(description = "Chi tiết sức chứa/khách đặt phòng", requiredMode = Schema.RequiredMode.REQUIRED)
    RoomOccupancy occupancy;
}
