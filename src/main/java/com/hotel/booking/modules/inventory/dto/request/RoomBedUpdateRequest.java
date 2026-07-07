package com.hotel.booking.modules.inventory.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Schema(description = "DTO dùng để cập nhật thông tin Giường (RoomBed)")
public class RoomBedUpdateRequest {

    @NotBlank(message = "ROOM_BED_NAME_NOT_BLANK")
    @Size(max = 100, message = "ROOM_BED_NAME_MAX_LENGTH")
    @Schema(description = "Tên loại giường", example = "Giường đơn King Size", requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @Size(max = 50, message = "ROOM_BED_SIZE_MAX_LENGTH")
    @Schema(description = "Kích thước giường", example = "1.8m x 2m")
    String size;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
