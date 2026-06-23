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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi thông tin chi tiết của Giường (RoomBed)")
public class RoomBedResponse {

    @Schema(description = "ID nội bộ của giường", example = "1")
    Short id;

    @Schema(description = "Tên loại giường", example = "Giường đơn King Size")
    String name;

    @Schema(description = "Kích thước giường", example = "1.8m x 2m")
    String size;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
