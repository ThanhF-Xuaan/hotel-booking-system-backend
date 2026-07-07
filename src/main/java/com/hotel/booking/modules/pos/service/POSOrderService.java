package com.hotel.booking.modules.pos.service;

import com.hotel.booking.modules.pos.dto.request.CreateServiceOrderRequest;

public interface POSOrderService {
    void createRoomServiceOrder(Long bookingId, CreateServiceOrderRequest request);
}
