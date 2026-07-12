package com.hotel.booking.modules.operation.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.operation.dto.request.UpdateRoomStatusRequest;
import com.hotel.booking.modules.operation.service.BookingOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Booking Operations", description = "Các API thao tác với trạng thái vòng đời Booking")
public class BookingOperationController {

    BookingOperationService bookingOperationService;

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Hủy Đặt Phòng (Cancel)", description = "Khách hàng hoặc Lễ tân hủy phòng trước ngày Check-in. Giải phóng kho phòng (Inventory) và Room Slots.")
    public ApiResponse<String> cancelBooking(@PathVariable("id") Long bookingId) {

        bookingOperationService.cancelBooking(bookingId);

        return ApiResponse.<String>builder()
                .result("Hủy phòng thành công. Đã giải phóng tài nguyên!")
                .build();
    }
}