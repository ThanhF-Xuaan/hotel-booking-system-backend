package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.PricingRuleTypeResponse;

import java.util.List;

public interface PricingRuleTypeConfigService {
    PricingRuleTypeResponse createPricingRuleType(PricingRuleTypeCreateRequest request);
    List<PricingRuleTypeResponse> getAllPricingRuleTypes();
    PricingRuleTypeResponse updatePricingRuleType(String code, PricingRuleTypeUpdateRequest request);
    void deletePricingRuleType(String code);
}
