package com.hotel.booking.modules.inventory.dto.request;

import com.hotel.booking.modules.inventory.enums.RoomInstanceStatus;
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
@Schema(description = "Yêu cầu tạo mới RoomInstance (phòng vật lý)")
public class RoomInstanceCreateRequest {

    @NotNull(message = "ROOM_INSTANCE_HOTEL_ID_NOT_NULL")
    @Schema(description = "ID của khách sạn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Short hotelId;

    @NotNull(message = "ROOM_INSTANCE_ROOM_TYPE_ID_NOT_NULL")
    @Schema(description = "ID của loại phòng khách sạn (HotelRoomType)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer hotelRoomTypeId;

    @NotBlank(message = "ROOM_INSTANCE_ROOM_NUMBER_NOT_BLANK")
    @Schema(description = "Số phòng (ví dụ: 101, 102)", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    String roomNumber;

    @NotNull(message = "ROOM_INSTANCE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hiện tại của phòng", example = "READY", requiredMode = Schema.RequiredMode.REQUIRED)
    RoomInstanceStatus currentStatus;
}
