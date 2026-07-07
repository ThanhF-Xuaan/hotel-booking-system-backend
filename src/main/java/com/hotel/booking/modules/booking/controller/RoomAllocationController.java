package com.hotel.booking.modules.booking.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.booking.dto.response.RoomSelectionUIResponse;
import com.hotel.booking.modules.booking.service.RoomAllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hotel/api/v1/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Room Allocation", description = "API Phân bổ phòng vật lý")
public class RoomAllocationController {

    RoomAllocationService roomAllocationService;

    @GetMapping("/{bookingNumber}/available-rooms")
    @Operation(summary = "Lấy danh sách phòng vật lý chọn lọc (Chỉ chứa READY/BLOCKED)")
    public ApiResponse<RoomSelectionUIResponse> getAvailableRooms(@PathVariable String bookingNumber) {
        return ApiResponse.<RoomSelectionUIResponse>builder()
                .code(1000)
                .result(roomAllocationService.getAvailableRoomsForBooking(bookingNumber))
                .message("Lấy danh sách phòng vật lý thành công")
                .build();
    }
}