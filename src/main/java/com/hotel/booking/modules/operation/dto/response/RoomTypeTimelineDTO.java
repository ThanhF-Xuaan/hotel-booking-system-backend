package com.hotel.booking.modules.operation.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeTimelineDTO {
    Integer hotelRoomTypeId;
    String roomTypeName;
    List<RoomTimeLineDTO> rooms;
}
