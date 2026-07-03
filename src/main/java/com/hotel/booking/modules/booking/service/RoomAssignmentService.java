package com.hotel.booking.modules.booking.service;

import com.hotel.booking.modules.booking.dto.request.RoomBlockRequest;

public interface RoomAssignmentService {
    void blockRoom(String bookingNumber, RoomBlockRequest request);
}
