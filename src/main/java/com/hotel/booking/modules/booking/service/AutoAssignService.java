package com.hotel.booking.modules.booking.service;

import com.hotel.booking.modules.booking.entity.BookingDetail;

public interface AutoAssignService {
    void processAutoAssignmentForDetail(BookingDetail detail);
}
