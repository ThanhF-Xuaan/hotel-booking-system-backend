package com.hotel.booking.modules.booking.service;

import com.hotel.booking.modules.booking.dto.request.ConfirmBookingRequest;

public interface BookingConfirmationService {
    String confirmBooking(ConfirmBookingRequest request);
}
