package com.hotel.booking.modules.operation.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.operation.dto.request.BlockRoomRequest;
import com.hotel.booking.modules.operation.dto.request.UpdateRoomStatusRequest;
import com.hotel.booking.modules.operation.service.RoomOperationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("hotel/api/v1/operation/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomOperationController {
    private final RoomOperationService roomOperationService;

    @PostMapping("/{roomInstanceId}/maintainence")
    public ApiResponse<String> maintainenceRoom(
            @PathVariable Integer roomInstanceId,
            @RequestBody BlockRoomRequest request) {

        return ApiResponse.<String>builder()
                .message(roomOperationService.blockRoomForMaintenance(roomInstanceId, request))
                .build();
    }

    @PatchMapping("/{roomInstanceId}/status")
    public ApiResponse<String> updateRoomStatus(
            @PathVariable Integer roomInstanceId,
            @RequestBody UpdateRoomStatusRequest request) {

        return ApiResponse.<String>builder()
                .message(roomOperationService.updateRoomStatus(roomInstanceId, request))
                .build();
    }
}
