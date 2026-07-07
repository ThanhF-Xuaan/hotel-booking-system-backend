package com.hotel.booking.modules.operation.dto.request;

import com.hotel.booking.modules.inventory.enums.RoomInstanceStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoomStatusRequest {
    @NotBlank(message = "STATUS_NOT_BLANK")
    RoomInstanceStatus status;
    LocalDate startDate;
    LocalDate endDate;
}
