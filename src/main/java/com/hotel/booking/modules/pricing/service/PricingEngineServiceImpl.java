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

        class PricingEvaluationContext {
            final PricingRule rule;
            final BigDecimal benefit;

            PricingEvaluationContext(PricingRule rule, BigDecimal benefit) {
                this.rule = rule;
                this.benefit = benefit;
            }

            BigDecimal getBenefit() {
                return benefit;
            }

            short getPriority() {
                return rule.getRuleType() != null && rule.getRuleType().getPriority() != null
                        ? rule.getRuleType().getPriority() : 0;
            }
        }

        PricingRule mathematicalWinner = rules.stream()
                .filter(java.util.Objects::nonNull)
                .map(rule -> new PricingEvaluationContext(rule, calculateAdjustment(rule, basePrice)))
                .max(java.util.Comparator.comparing(PricingEvaluationContext::getBenefit)
                        .thenComparing(PricingEvaluationContext::getPriority))
                .map(ctx -> ctx.rule)
                .orElse(null);

        if (mathematicalWinner != null) {
            short priorityVal = (mathematicalWinner.getRuleType() != null && mathematicalWinner.getRuleType().getPriority() != null)
                    ? mathematicalWinner.getRuleType().getPriority() : 0;
            log.info("Winner resolved: Rule ID {} with priority {}",
                    mathematicalWinner.getId(), priorityVal);
        }

        return Optional.ofNullable(mathematicalWinner);
    }

    private BigDecimal calculateAdjustment(PricingRule rule, BigDecimal basePrice) {
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
