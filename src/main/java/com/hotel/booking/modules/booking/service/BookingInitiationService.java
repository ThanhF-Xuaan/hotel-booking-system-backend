package com.hotel.booking.modules.booking.service;

import com.hotel.booking.modules.booking.dto.request.InitiateBookingRequest;

public interface BookingInitiationService {
    String initiate(InitiateBookingRequest request);

}
