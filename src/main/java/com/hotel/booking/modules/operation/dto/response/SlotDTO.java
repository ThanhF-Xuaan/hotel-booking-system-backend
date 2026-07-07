package com.hotel.booking.modules.operation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotel.booking.modules.booking.enums.BookingStatus;
import com.hotel.booking.modules.inventory.enums.RoomSlotStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Ẩn các field NULL cho nhẹ JSON
public class SlotDTO {
    LocalDate date;
    RoomSlotStatus status; // "READY", "OCCUPIED", "RESERVED", "MAINTENANCE"

    // Các trường này chỉ xuất hiện khi status != READY & MAINTENANCE
    Long bookingDetailId;
    Long bookingId;
    String guestName;
    BookingStatus bookingStatus;
}
