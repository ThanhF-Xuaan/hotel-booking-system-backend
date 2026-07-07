package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.dto.request.CatalogItemCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.CatalogItemUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.CatalogItemResponse;

import java.util.List;

public interface CatalogItemService {
    CatalogItemResponse createCatalogItem(CatalogItemCreationRequest request);
    CatalogItemResponse updateCatalogItem(Integer id, CatalogItemUpdateRequest request);
    CatalogItemResponse getCatalogItem(Integer id);
    List<CatalogItemResponse> getCatalogItems(Short hotelId);
    void deleteCatalogItem(Integer id);
}
