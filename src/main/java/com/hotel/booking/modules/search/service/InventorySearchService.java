package com.hotel.booking.modules.search.service;

import com.hotel.booking.modules.search.dto.request.AvailabilitySearchRequest;
import com.hotel.booking.modules.search.dto.response.AvailableRoomTypeResponse;

import java.util.List;

public interface InventorySearchService {
    List<AvailableRoomTypeResponse> searchAvailableRooms(AvailabilitySearchRequest request);
}
