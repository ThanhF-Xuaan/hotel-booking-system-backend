package com.hotel.booking.modules.search.service;

import com.hotel.booking.modules.search.dto.request.PricingRequest;
import com.hotel.booking.modules.search.dto.response.PricingResponse;

public interface PriceAggregationService {
    PricingResponse calculatePrice(PricingRequest request);
}
