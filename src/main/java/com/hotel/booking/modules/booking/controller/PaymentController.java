package com.hotel.booking.modules.booking.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.booking.dto.request.PaymentRequest;
import com.hotel.booking.modules.booking.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("hotel/api/v1/booking/{bookingId}/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    @PostMapping
    public ApiResponse<String> addPayment(@PathVariable Long bookingId, @RequestBody PaymentRequest request) {
        paymentService.addPayment(bookingId, request);
        return ApiResponse.<String>builder()
                .message("Payment recorded successfully")
                .result("OK")
                .build();
    }
}
