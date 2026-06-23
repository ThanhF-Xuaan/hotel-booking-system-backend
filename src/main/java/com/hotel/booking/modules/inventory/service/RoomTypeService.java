package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.RoomTypeCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomTypeUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomTypeResponse;

import java.util.List;

public interface RoomTypeService {
    RoomTypeResponse createRoomType(RoomTypeCreationRequest request);
    List<RoomTypeResponse> getAllRoomTypes();
    RoomTypeResponse getRoomTypeById(Short id);
    RoomTypeResponse updateRoomType(Short id, RoomTypeUpdateRequest request);
    void deleteRoomType(Short id);
}
