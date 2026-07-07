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
@Schema(description = "Thông tin chi tiết về giường đã được gán cho loại phòng")
public class AssignedBedResponse {

    @Schema(description = "ID của loại giường (RoomBed)", example = "1")
    Short roomBedId;

    @Schema(description = "Tên của loại giường", example = "Giường đơn King")
    String bedName;

    @Schema(description = "Số lượng giường", example = "2")
    Short quantity;
}
