package com.hotel.booking.modules.booking.service;

import com.hotel.booking.modules.booking.dto.request.PaymentRequest;

public interface PaymentService {
    void addPayment(Long bookingId, PaymentRequest request);
}
