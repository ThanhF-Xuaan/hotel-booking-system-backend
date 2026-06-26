package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeCatalogItemSyncRequest;
import com.hotel.booking.modules.inventory.dto.response.AssignedCatalogItemResponse;

import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeCatalogItemResponse;

import java.util.List;

public interface HotelRoomTypeCatalogItemService {
    List<AssignedCatalogItemResponse> syncCatalogItems(Integer hotelRoomTypeId, HotelRoomTypeCatalogItemSyncRequest request);
    List<HotelRoomTypeCatalogItemResponse> getCatalogItemsByHotelRoomTypeId(Integer hotelRoomTypeId);
}
