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
        assertTrue(pricingEngineService.resolveWinningRule(null, BigDecimal.TEN).isEmpty());
        assertTrue(pricingEngineService.resolveWinningRule(Collections.emptyList(), BigDecimal.TEN).isEmpty());
    }

    @Test
    void resolveWinningRule_absoluteOverride_highestPriorityWins() {
        PricingRule normal1 = createRule(1, (short) 10, AdjustmentType.FIXED, BigDecimal.TEN);
        PricingRule override1 = createRule(2, (short) 100, AdjustmentType.FIXED, BigDecimal.ONE);
        PricingRule override2 = createRule(3, (short) 120, AdjustmentType.FIXED, BigDecimal.ONE);

        List<PricingRule> rules = List.of(normal1, override1, override2);
        Optional<PricingRule> winner = pricingEngineService.resolveWinningRule(rules, BigDecimal.valueOf(100));

        assertTrue(winner.isPresent());
        assertEquals(3, winner.get().getId());
    }

    @Test
    void resolveWinningRule_noOverride_maxValueCompetitionWins() {
        // Base price is 100
        // Fixed: $15
        PricingRule ruleFixed = createRule(1, (short) 5, AdjustmentType.FIXED, BigDecimal.valueOf(15));
        // Percent: 10% of 100 = $10
        PricingRule rulePercent = createRule(2, (short) 10, AdjustmentType.PERCENT, BigDecimal.valueOf(10));

        List<PricingRule> rules = List.of(ruleFixed, rulePercent);
        Optional<PricingRule> winner = pricingEngineService.resolveWinningRule(rules, BigDecimal.valueOf(100));

        assertTrue(winner.isPresent());
        assertEquals(1, winner.get().getId()); // FIXED rule yield $15 > PERCENT rule yield $10
    }

    @Test
    void resolveWinningRule_tieAdjustmentAmount_priorityTiebreakerWins() {
        // Base price is 100
        // Fixed: $15, priority 5
        PricingRule rule1 = createRule(1, (short) 5, AdjustmentType.FIXED, BigDecimal.valueOf(15));
        // Percent: 15% of 100 = $15, priority 15
        PricingRule rule2 = createRule(2, (short) 15, AdjustmentType.PERCENT, BigDecimal.valueOf(15));

        List<PricingRule> rules = List.of(rule1, rule2);
        Optional<PricingRule> winner = pricingEngineService.resolveWinningRule(rules, BigDecimal.valueOf(100));

        assertTrue(winner.isPresent());
        assertEquals(2, winner.get().getId()); // Rule 2 has higher priority (15 > 5)
    }
}
