package com.hotel.booking.modules.booking.service;

import com.hotel.booking.modules.booking.dto.request.RoomConfirmRequest;

public interface RoomConfirmationService {
    void confirmRoom(String bookingNumber, RoomConfirmRequest request);
}
