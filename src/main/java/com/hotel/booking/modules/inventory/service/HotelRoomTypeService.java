package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeResponse;

import java.util.List;

public interface HotelRoomTypeService {
    HotelRoomTypeResponse createHotelRoomType(HotelRoomTypeCreationRequest request);
    HotelRoomTypeResponse updateHotelRoomType(Integer id, HotelRoomTypeUpdateRequest request);
    HotelRoomTypeResponse getHotelRoomType(Integer id);
    List<HotelRoomTypeResponse> getHotelRoomTypes(Short hotelId);
    void deleteHotelRoomType(Integer id);
}
