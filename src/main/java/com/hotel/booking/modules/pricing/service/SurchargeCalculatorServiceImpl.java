package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.entity.HotelAgePolicy;
import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SurchargeCalculatorServiceImpl implements SurchargeCalculatorService {

    @Override
    public BigDecimal calculateSurcharge(SurchargeRule rule, BigDecimal basePrice, Map<String, Object> context) {
        if (rule == null || basePrice == null || context == null || rule.getConditions() == null) {
            return BigDecimal.ZERO;
        }

        boolean applies = false;
        SurchargeRuleType ruleType = rule.getRuleType();

        if (ruleType == SurchargeRuleType.EXTRA_PERSON) {
            HotelAgePolicy policy = rule.getAgePolicy();
            Number guestAgeNum = (Number) context.get("guestAge");
            if (policy != null && guestAgeNum != null) {
                int guestAge = guestAgeNum.intValue();
                if (guestAge >= policy.getMinAge() && guestAge <= policy.getMaxAge()) {
                    applies = true;
                }
            }
        } else if (ruleType == SurchargeRuleType.EXTRA_BED) {
            applies = Boolean.TRUE.equals(context.get("extraBedRequested"));
        } else if (ruleType == SurchargeRuleType.EARLY_CHECKIN) {
            Double minHours = rule.getConditions().getMinHours();
            Number earlyHours = (Number) context.get("earlyHours");
            if (minHours != null && earlyHours != null && earlyHours.doubleValue() >= minHours) {
                applies = true;
            }
        } else if (ruleType == SurchargeRuleType.LATE_CHECKOUT) {
            Double minHours = rule.getConditions().getMinHours();
            Number lateHours = (Number) context.get("lateHours");
            if (minHours != null && lateHours != null && lateHours.doubleValue() >= minHours) {
                applies = true;
            }
        }

        if (applies) {
            if (rule.getAdjustmentType() == AdjustmentType.FIXED) {
                return rule.getAdjustmentValue();
            } else if (rule.getAdjustmentType() == AdjustmentType.PERCENT) {
                return basePrice.multiply(rule.getAdjustmentValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }
        }

        return BigDecimal.ZERO;
    }
}
