package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.entity.SurchargeRule;

import java.math.BigDecimal;
import java.util.Map;

public interface SurchargeCalculatorService {
    BigDecimal calculateSurcharge(SurchargeRule rule, BigDecimal basePrice, Map<String, Object> context);
}
