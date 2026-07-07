package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeFeatureSyncRequest;
import com.hotel.booking.modules.inventory.dto.response.AssignedFeatureResponse;

import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeFeatureResponse;

import java.util.List;

public interface HotelRoomTypeFeatureService {
    List<AssignedFeatureResponse> syncFeatures(Integer hotelRoomTypeId, HotelRoomTypeFeatureSyncRequest request);
    List<HotelRoomTypeFeatureResponse> getFeaturesByHotelRoomTypeId(Integer hotelRoomTypeId);
}
