package com.hotel.booking.modules.booking.dto.response;

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
public class RoomSelectionUIResponse {
    String bookingNumber;
    LocalDate checkIn;
    LocalDate checkOut;
    List<RoomTypeGroup> roomTypeGroups;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RoomTypeGroup {
        Integer hotelRoomTypeId;
        String roomTypeName;
        Integer requiredQuantity;
        List<PhysicalRoom> physicalRooms;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PhysicalRoom {
        Integer roomInstanceId;
        String roomNumber;
        String status; // READY & BLOCKED
    }
}
