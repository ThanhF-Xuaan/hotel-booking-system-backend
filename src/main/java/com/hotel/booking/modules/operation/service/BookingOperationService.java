package com.hotel.booking.modules.operation.service;

public interface BookingOperationService {
    // BƯỚC 20: Hủy phòng
    void cancelBooking(Long bookingId);

    // BƯỚC 21: Xử lý khách không đến (No-Show)
    void processNoShow(Long bookingId);
}
