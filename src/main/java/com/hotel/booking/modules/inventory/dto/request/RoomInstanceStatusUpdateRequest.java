package com.hotel.booking.modules.inventory.dto.request;

import com.hotel.booking.modules.inventory.enums.RoomInstanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Yêu cầu cập nhật trạng thái RoomInstance")
public class RoomInstanceStatusUpdateRequest {

    @NotNull(message = "ROOM_INSTANCE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái mới của phòng", example = "READY", requiredMode = Schema.RequiredMode.REQUIRED)
    RoomInstanceStatus currentStatus;
}
