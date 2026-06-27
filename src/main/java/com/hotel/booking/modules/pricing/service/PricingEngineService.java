package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.entity.PricingRule;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PricingEngineService {
    Optional<PricingRule> resolveWinningRule(List<PricingRule> rules, BigDecimal basePrice);
}
