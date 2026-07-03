package com.hotel.booking.modules.booking.service;

import com.hotel.booking.modules.booking.dto.response.RoomSelectionUIResponse;

public interface RoomAllocationService {
    RoomSelectionUIResponse getAvailableRoomsForBooking(String bookingNumber);
}
