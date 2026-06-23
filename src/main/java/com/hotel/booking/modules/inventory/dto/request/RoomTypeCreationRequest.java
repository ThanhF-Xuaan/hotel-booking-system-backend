package com.hotel.booking.modules.inventory.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
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
@Schema(description = "DTO dùng để tạo mới Loại phòng (RoomType)")
public class RoomTypeCreationRequest {

    @NotBlank(message = "ROOM_TYPE_CODE_NOT_BLANK")
    @Schema(description = "Mã định danh duy nhất của loại phòng", example = "DELUXE", requiredMode = Schema.RequiredMode.REQUIRED)
    String code;

    @NotBlank(message = "ROOM_TYPE_NAME_NOT_BLANK")
    @Schema(description = "Tên hiển thị của loại phòng", example = "Phòng Deluxe cao cấp", requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @NotNull(message = "ROOM_TYPE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động của loại phòng", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
