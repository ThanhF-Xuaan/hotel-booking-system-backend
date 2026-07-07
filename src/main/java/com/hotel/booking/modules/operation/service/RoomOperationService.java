package com.hotel.booking.modules.operation.service;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.operation.dto.request.BlockRoomRequest;
import com.hotel.booking.modules.operation.dto.request.UpdateRoomStatusRequest;

public interface RoomOperationService {
    String blockRoomForMaintenance(Integer roomInstanceId, BlockRoomRequest request);

    String updateRoomStatus(Integer roomInstanceId, UpdateRoomStatusRequest request);
}
