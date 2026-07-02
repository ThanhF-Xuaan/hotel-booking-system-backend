package com.hotel.booking.modules.booking.dto.request;


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
public class InitiateBookingRequest {
    Short hotelId;
    LocalDate checkIn;
    LocalDate checkOut;
    GuestInfo guestInfo;
    List<RoomSelection> rooms;

    @Data
    public static class GuestInfo {
        private String firstName;
        private String lastName;
        private String fullName;
        LocalDate birthDate;
        String nationality;
        private String email;
        private String phone;
    }

    @Data
    public static class RoomSelection {
        private Integer hotelRoomTypeId;
        private Integer quantity;

        private List<RoomOccupancy> occupancies;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomOccupancy {
        private Integer adults;
        private Integer children;
        private Integer infants;
    }
}
