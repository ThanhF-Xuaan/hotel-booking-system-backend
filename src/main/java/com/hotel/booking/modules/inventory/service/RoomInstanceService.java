package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.RoomInstanceCreateRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomInstanceStatusUpdateRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomInstanceUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomInstanceResponse;

import java.util.List;

public interface RoomInstanceService {
    RoomInstanceResponse createRoomInstance(RoomInstanceCreateRequest request);
    RoomInstanceResponse getRoomInstanceById(Integer id);
    List<RoomInstanceResponse> getRoomInstances(Short hotelId);
    RoomInstanceResponse updateRoomInstance(Integer id, RoomInstanceUpdateRequest request);
    RoomInstanceResponse updateRoomInstanceStatus(Integer id, RoomInstanceStatusUpdateRequest request);
    void deleteRoomInstance(Integer id);
}
