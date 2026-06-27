package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.entity.PricingRule;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PricingEngineServiceImpl implements PricingEngineService {

    @Override
    public Optional<PricingRule> resolveWinningRule(List<PricingRule> rules, BigDecimal basePrice) {
        if (rules == null || rules.isEmpty()) {
            log.debug("No pricing rules provided for resolution.");
            return Optional.empty();
        }

        // Step 1: Absolute Override (The 'Manager's Force' Rule)
        // Check for priority >= 100.
        PricingRule overrideWinner = null;
        for (PricingRule rule : rules) {
            if (rule != null && rule.getRuleType() != null && rule.getRuleType().getPriority() != null && rule.getRuleType().getPriority() >= 100) {
                short rulePriority = rule.getRuleType().getPriority();
                short overrideWinnerPriority = (overrideWinner != null && overrideWinner.getRuleType() != null && overrideWinner.getRuleType().getPriority() != null) 
                        ? overrideWinner.getRuleType().getPriority() : 0;
                if (overrideWinner == null || rulePriority > overrideWinnerPriority) {
                    overrideWinner = rule;
                }
            }
        }

        if (overrideWinner != null) {
            log.info("Absolute override applied: Rule ID {} with priority {}", 
                    overrideWinner.getId(), overrideWinner.getRuleType().getPriority());
            return Optional.of(overrideWinner);
        }

        // Step 2 & 3: Max Value Competition & Tie-breaker (Priority Resolution)
        PricingRule mathematicalWinner = null;
        BigDecimal maxAdjustmentAmount = BigDecimal.valueOf(-1);

        for (PricingRule rule : rules) {
            if (rule == null) {
                continue;
            }

            BigDecimal adjustmentAmount = calculateAdjustmentAmount(rule, basePrice);
            if (mathematicalWinner == null) {
                mathematicalWinner = rule;
                maxAdjustmentAmount = adjustmentAmount;
                continue;
            }

            int amountComparison = adjustmentAmount.compareTo(maxAdjustmentAmount);
            if (amountComparison > 0) {
                // Step 2: Pick the rule with the highest adjustment amount
                mathematicalWinner = rule;
                maxAdjustmentAmount = adjustmentAmount;
            } else if (amountComparison == 0) {
                // Step 3: Tie-breaker on priority
                short rulePriority = (rule.getRuleType() != null && rule.getRuleType().getPriority() != null) ? rule.getRuleType().getPriority() : 0;
                short winnerPriority = (mathematicalWinner.getRuleType() != null && mathematicalWinner.getRuleType().getPriority() != null) ? mathematicalWinner.getRuleType().getPriority() : 0;
                if (rulePriority > winnerPriority) {
                    mathematicalWinner = rule;
                }
            }
        }

        if (mathematicalWinner != null) {
            short priorityVal = (mathematicalWinner.getRuleType() != null && mathematicalWinner.getRuleType().getPriority() != null) 
                    ? mathematicalWinner.getRuleType().getPriority() : 0;
            log.info("Mathematical winner resolved: Rule ID {} with adjustment yield {} and priority {}", 
                    mathematicalWinner.getId(), maxAdjustmentAmount, priorityVal);
        }

        return Optional.ofNullable(mathematicalWinner);
    }

    private BigDecimal calculateAdjustmentAmount(PricingRule rule, BigDecimal basePrice) {
        if (rule.getAdjustmentValue() == null) {
            return BigDecimal.ZERO;
        }

        if (rule.getAdjustmentType() == AdjustmentType.FIXED) {
            return rule.getAdjustmentValue();
        } else if (rule.getAdjustmentType() == AdjustmentType.PERCENT) {
            if (basePrice == null) {
                return BigDecimal.ZERO;
            }
            // basePrice * (adjustmentValue / 100)
            return basePrice.multiply(rule.getAdjustmentValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }
}
