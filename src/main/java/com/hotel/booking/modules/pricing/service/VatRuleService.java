package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.VatRuleCreationRequest;
import com.hotel.booking.modules.pricing.dto.request.VatRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.VatRuleResponse;

import java.util.List;

public interface VatRuleService {
    VatRuleResponse createVatRule(VatRuleCreationRequest request);
    List<VatRuleResponse> getAllVatRules();
    VatRuleResponse getVatRuleById(Integer id);
    VatRuleResponse updateVatRule(Integer id, VatRuleUpdateRequest request);
    void deleteVatRule(Integer id);
}
