package com.hotel.booking.modules.booking.service;

import java.util.Map;

public interface BookingDetailService {
    Map<String, Object> getBookingDetailOccupancy(Long bookingDetailId);
}
