package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.HotelAgePolicyResponse;

import java.util.List;

public interface HotelAgePolicyService {
    HotelAgePolicyResponse createPolicy(HotelAgePolicyCreateRequest request);
    HotelAgePolicyResponse getPolicyById(Short id);
    List<HotelAgePolicyResponse> getPolicies(Short hotelId);
    HotelAgePolicyResponse updatePolicy(Short id, HotelAgePolicyUpdateRequest request);
    void deletePolicy(Short id);
}
