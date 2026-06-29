package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.DiscountRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.DiscountRuleResponse;

import java.util.List;

public interface DiscountRuleService {

    List<DiscountRuleResponse> createDiscountRule(DiscountRuleCreateRequest request);

    DiscountRuleResponse getDiscountRuleById(Integer id);

    List<DiscountRuleResponse> getDiscountRules(Integer hotelRoomTypeId);

    DiscountRuleResponse updateDiscountRule(Integer id, DiscountRuleUpdateRequest request);

    void deleteDiscountRule(Integer id);
}
