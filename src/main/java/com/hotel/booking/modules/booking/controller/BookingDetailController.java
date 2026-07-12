package com.hotel.booking.modules.booking.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.booking.service.BookingDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Booking", description = "API Quản lý giao dịch đặt phòng")
public class BookingDetailController {
    BookingDetailService bookingDetailService;

    @GetMapping("/detail/{bookingDetailId}")
    public ApiResponse<Map<String, Object>> getBookingDetailOccupancy(
            @PathVariable("bookingDetailId") Long bookingDetailId) {
        java.util.Map<String, Object> result = bookingDetailService.getBookingDetailOccupancy(bookingDetailId);
        return ApiResponse.<java.util.Map<String, Object>>builder()
                .message("Lấy thông tin occupancy thành công")
                .result(result)
                .build();
    }
}
