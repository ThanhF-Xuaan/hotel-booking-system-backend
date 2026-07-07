package com.hotel.booking.modules.booking.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.booking.dto.request.InitiateBookingRequest;
import com.hotel.booking.modules.booking.dto.response.InitiateBookingResponse;
import com.hotel.booking.modules.booking.service.BookingInitiationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hotel/api/v1/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingInitiateController {
    private final BookingInitiationService initiationService;

    @PostMapping("/initiate")
    public ApiResponse<InitiateBookingResponse> initiateBooking(@RequestBody InitiateBookingRequest request) {
        String sessionId = initiationService.initiate(request);

        InitiateBookingResponse response = InitiateBookingResponse.builder()
                .sessionId(sessionId)
                .expiresIn(600L)
                .build();

        return ApiResponse.<InitiateBookingResponse>builder()
                .result(response)
                .message("Đã giữ phòng thành công. Vui lòng thanh toán trong 10 phút.")
                .build();
    }
}
