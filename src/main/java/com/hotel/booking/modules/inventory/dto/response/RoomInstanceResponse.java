package com.hotel.booking.modules.inventory.dto.response;

import com.hotel.booking.modules.inventory.enums.RoomInstanceStatus;
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
@Schema(description = "Thông tin chi tiết RoomInstance phản hồi cho client")
public class RoomInstanceResponse {

    @Schema(description = "ID của RoomInstance", example = "1")
    Integer id;

    @Schema(description = "ID của khách sạn", example = "1")
    Short hotelId;

    @Schema(description = "ID của loại phòng khách sạn (HotelRoomType)", example = "1")
    Integer hotelRoomTypeId;

    @Schema(description = "Số phòng", example = "101")
    String roomNumber;

    @Schema(description = "Trạng thái hiện tại của phòng", example = "READY")
    RoomInstanceStatus currentStatus;
}
