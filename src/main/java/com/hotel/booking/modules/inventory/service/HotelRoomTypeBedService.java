package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeBedSyncRequest;
import com.hotel.booking.modules.inventory.dto.response.AssignedBedResponse;

import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeBedResponse;

import java.util.List;

public interface HotelRoomTypeBedService {
    List<AssignedBedResponse> syncBeds(Integer hotelRoomTypeId, HotelRoomTypeBedSyncRequest request);
    List<HotelRoomTypeBedResponse> getBedsByHotelRoomTypeId(Integer hotelRoomTypeId);
}
