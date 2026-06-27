package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.SurchargeRuleResponse;

import java.util.List;

public interface SurchargeRuleService {

    SurchargeRuleResponse createSurchargeRule(SurchargeRuleCreateRequest request);

    SurchargeRuleResponse getSurchargeRuleById(Integer id);

    List<SurchargeRuleResponse> getSurchargeRules(Integer hotelRoomTypeId);

    SurchargeRuleResponse updateSurchargeRule(Integer id, SurchargeRuleUpdateRequest request);

    void deleteSurchargeRule(Integer id);
}
