package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.PricingRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.PricingRuleResponse;

import java.util.List;

public interface PricingRuleService {

    PricingRuleResponse createPricingRule(PricingRuleCreateRequest request);

    PricingRuleResponse getPricingRuleById(Integer id);

    List<PricingRuleResponse> getPricingRules(Integer hotelRoomTypeId);

    PricingRuleResponse updatePricingRule(Integer id, PricingRuleUpdateRequest request);

    void deletePricingRule(Integer id);
}
