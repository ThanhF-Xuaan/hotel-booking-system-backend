package com.hotel.booking.modules.booking.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.booking.dto.request.RoomBlockRequest;
import com.hotel.booking.modules.booking.dto.request.RoomConfirmRequest;
import com.hotel.booking.modules.booking.service.RoomAssignmentService;
import com.hotel.booking.modules.booking.service.RoomConfirmationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Room Assignment", description = "API Gán phòng vật lý")
public class RoomAssignmentController {
    RoomAssignmentService roomAssignmentService;
    RoomConfirmationService roomConfirmationService;

    @PostMapping("/{bookingNumber}/rooms/block")
    @Operation(summary = "Block phòng vật lý (BƯỚC 14 & 15)", description = "Dùng cho cả chọn phòng mới (oldRoom = null) và đổi phòng (oldRoom != null)")
    public ApiResponse<Void> blockRoom(
            @PathVariable String bookingNumber,
            @RequestBody @Valid RoomBlockRequest request) {

        roomAssignmentService.blockRoom(bookingNumber, request);

        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Phòng đã được giữ thành công")
                .build();
    }

    @PostMapping("/{bookingNumber}/rooms/confirm")
    @Operation(summary = "Chốt phòng cứng (BƯỚC 16)", description = "Chuyển trạng thái từ BLOCKED sang RESERVED và ghi lịch sử vào booking_rooms. Từ lúc này không được tự ý đổi phòng nữa.")
    public ApiResponse<Void> confirmRoom(
            @PathVariable String bookingNumber,
            @RequestBody @Valid RoomConfirmRequest request) {

        roomConfirmationService.confirmRoom(bookingNumber, request);

        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Phòng đã được xác nhận thành công!")
                .build();
    }
}
