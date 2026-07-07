package com.hotel.booking.modules.operation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockRoomRequest {
    @NotNull(message = "START_DATE_NOT_NULL")
    LocalDate startDate;

    @NotNull(message = "END_DATE_NOT_NULL")
    LocalDate endDate;

    String reason;
}
