package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.RoomFeatureCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomFeatureUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomFeatureResponse;

import java.util.List;

public interface RoomFeatureService {
    RoomFeatureResponse createRoomFeature(RoomFeatureCreationRequest request);
    List<RoomFeatureResponse> getAllRoomFeatures();
    RoomFeatureResponse getRoomFeatureById(Short id);
    RoomFeatureResponse updateRoomFeature(Short id, RoomFeatureUpdateRequest request);
    void deleteRoomFeature(Short id);
}
