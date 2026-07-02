package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.entity.PricingRule;
import com.hotel.booking.modules.pricing.entity.PricingRuleTypeConfig;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PricingEngineServiceTest {

    private PricingEngineService pricingEngineService;

    @BeforeEach
    void setUp() {
        pricingEngineService = new PricingEngineServiceImpl();
    }

    private PricingRule createRule(Integer id, short priority, AdjustmentType adjustmentType, BigDecimal value) {
        return PricingRule.builder()
                .id(id)
                .ruleType(PricingRuleTypeConfig.builder().priority(priority).build())
                .adjustmentType(adjustmentType)
                .adjustmentValue(value)
                .build();
    }

    @Test
    void resolveWinningRule_emptyOrNullRules_returnsEmpty() {
        assertTrue(pricingEngineService.resolveWinningRule(null, java.math.BigDecimal.TEN).isEmpty());
        assertTrue(pricingEngineService.resolveWinningRule(Collections.emptyList(), java.math.BigDecimal.TEN).isEmpty());
    }

    @Test
    void resolveWinningRule_differentTypes_selectsHighestBenefit() {
        // Base price is 2000
        // FIXED rule: 500.0 (yields 500.0 benefit, priority 5)
        PricingRule fixedRule = createRule(1, (short) 5, AdjustmentType.FIXED, BigDecimal.valueOf(500));
        // PERCENT rule: 20% (yields 400.0 benefit, priority 10)
        PricingRule percentRule = createRule(2, (short) 10, AdjustmentType.PERCENT, BigDecimal.valueOf(20));

        List<PricingRule> rules = List.of(fixedRule, percentRule);
        Optional<PricingRule> winner = pricingEngineService.resolveWinningRule(rules, BigDecimal.valueOf(2000));

        assertTrue(winner.isPresent());
        assertEquals(1, winner.get().getId()); // FIXED rule (500) > PERCENT rule (400) despite lower priority
    }

    @Test
    void resolveWinningRule_peakSeasonOverridesHoliday_regardlessOfPriority() {
        // Base price is 1000
        // Holiday: 30% adjustment (yields 300.0 benefit, priority 10)
        PricingRule holidayRule = createRule(1, (short) 10, AdjustmentType.PERCENT, BigDecimal.valueOf(30));
        // Peak Season: 40% adjustment (yields 400.0 benefit, priority 5)
        PricingRule peakSeasonRule = createRule(2, (short) 5, AdjustmentType.PERCENT, BigDecimal.valueOf(40));

        List<PricingRule> rules = List.of(holidayRule, peakSeasonRule);
        Optional<PricingRule> winner = pricingEngineService.resolveWinningRule(rules, BigDecimal.valueOf(1000));

        assertTrue(winner.isPresent());
        assertEquals(2, winner.get().getId()); // Peak Season (400) > Holiday (300) despite lower priority (5 < 10)
    }

    @Test
    void resolveWinningRule_sameBenefitDifferentPriority_selectsHigherPriority() {
        // Base price is 1000
        // Rule A: 30% adjustment (yields 300.0 benefit, priority 10)
        PricingRule ruleA = createRule(1, (short) 10, AdjustmentType.PERCENT, BigDecimal.valueOf(30));
        // Rule B: 30% adjustment (yields 300.0 benefit, priority 20)
        PricingRule ruleB = createRule(2, (short) 20, AdjustmentType.PERCENT, BigDecimal.valueOf(30));

        List<PricingRule> rules = List.of(ruleA, ruleB);
        Optional<PricingRule> winner = pricingEngineService.resolveWinningRule(rules, BigDecimal.valueOf(1000));

        assertTrue(winner.isPresent());
        assertEquals(2, winner.get().getId()); // Rule B is selected due to higher priority (20 > 10)
    }
}
