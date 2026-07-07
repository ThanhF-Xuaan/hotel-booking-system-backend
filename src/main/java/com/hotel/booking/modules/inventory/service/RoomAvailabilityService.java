package com.hotel.booking.modules.inventory.service;

public interface RoomAvailabilityService {
    void lockRoomForPayment(int typeId, int quantity);
}
