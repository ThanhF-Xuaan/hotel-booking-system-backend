package com.hotel.booking.modules.booking.service;

import java.math.BigDecimal;

public interface BookingFinancialService {
    void addAmountToBooking(
            Long bookingId,
            BigDecimal subtotal,
            BigDecimal serviceFeeAmount,
            BigDecimal vatAmount,
            BigDecimal totalAmount
    );
}
