package com.hotel.booking.modules.operation.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimelineResponse {
    LocalDate startDate;
    LocalDate endDate;
    List<RoomTypeTimelineDTO> roomTypes;
}
