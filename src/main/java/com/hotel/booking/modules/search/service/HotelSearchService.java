package com.hotel.booking.modules.search.service;

import com.hotel.booking.modules.search.dto.request.HotelSearchRequest;
import com.hotel.booking.modules.search.dto.response.HotelSearchResultResponse;

import java.util.List;

public interface HotelSearchService {
    List<HotelSearchResultResponse> searchHotels(HotelSearchRequest request);
}
