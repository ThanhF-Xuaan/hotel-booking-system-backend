package com.hotel.booking.modules.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(description = "Yêu cầu đồng bộ danh sách giường cấu hình cho Loại phòng khách sạn (HotelRoomType)")
public class HotelRoomTypeBedSyncRequest {

    @NotNull(message = "HOTEL_ROOM_TYPE_BEDS_NOT_NULL")
    @NotEmpty(message = "HOTEL_ROOM_TYPE_BEDS_NOT_EMPTY")
    @Schema(description = "Danh sách cấu hình giường", requiredMode = Schema.RequiredMode.REQUIRED)
    List<@Valid RoomBedSyncItemRequest> beds;
}
