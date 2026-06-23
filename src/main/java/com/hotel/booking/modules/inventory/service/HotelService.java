package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.HotelCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.HotelUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.HotelResponse;

import java.util.List;

public interface HotelService {
    HotelResponse createHotel(HotelCreationRequest request);
    List<HotelResponse> getAllHotels();
    HotelResponse getHotelById(Short id);
    HotelResponse updateHotel(Short id, HotelUpdateRequest request);
    void deleteHotel(Short id);
}
