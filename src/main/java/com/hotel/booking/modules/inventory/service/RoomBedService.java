package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.RoomBedCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomBedUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomBedResponse;

import java.util.List;

public interface RoomBedService {
    RoomBedResponse createRoomBed(RoomBedCreationRequest request);
    List<RoomBedResponse> getAllRoomBeds();
    RoomBedResponse getRoomBedById(Short id);
    RoomBedResponse updateRoomBed(Short id, RoomBedUpdateRequest request);
    void deleteRoomBed(Short id);
}
