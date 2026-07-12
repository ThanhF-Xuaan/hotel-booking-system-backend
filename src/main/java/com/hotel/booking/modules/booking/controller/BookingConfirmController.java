package com.hotel.booking.modules.booking.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.booking.dto.request.ConfirmBookingRequest;
import com.hotel.booking.modules.booking.service.BookingConfirmationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Booking", description = "API Quản lý giao dịch đặt phòng")
public class BookingConfirmController {

    BookingConfirmationService confirmationService;

    @PostMapping("/confirm")
    @Operation(summary = "Xác nhận và chốt đơn đặt phòng (BƯỚC 12)")
    public ApiResponse<String> confirmBooking(@RequestBody ConfirmBookingRequest request) {
        String bookingNumber = confirmationService.confirmBooking(request);

        return ApiResponse.<String>builder()
                .code(1000)
                .result(bookingNumber)
                .message("Đơn đặt phòng đã được xác nhận cứng và chốt tồn kho thành công!")
                .build();
    }
}
