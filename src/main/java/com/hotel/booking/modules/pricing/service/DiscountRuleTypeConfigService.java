package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.DiscountRuleTypeResponse;

import java.util.List;

public interface DiscountRuleTypeConfigService {
    DiscountRuleTypeResponse createDiscountRuleType(DiscountRuleTypeCreateRequest request);
    List<DiscountRuleTypeResponse> getAllDiscountRuleTypes();
    DiscountRuleTypeResponse updateDiscountRuleType(String code, DiscountRuleTypeUpdateRequest request);
    void deleteDiscountRuleType(String code);
}
