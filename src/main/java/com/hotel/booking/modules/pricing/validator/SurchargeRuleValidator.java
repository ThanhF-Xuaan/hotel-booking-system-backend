package com.hotel.booking.modules.pricing.validator;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
import com.hotel.booking.modules.pricing.repository.SurchargeRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SurchargeRuleValidator {

    SurchargeRuleRepository surchargeRuleRepository;

    /**
     * Checks if there's any existing surcharge rule that overlaps with the new/updated one.
     * Throws an AppException with ErrorCode.SURCHARGE_RULE_OVERLAP if a conflict is found.
     */
    public void validateNoOverlap(
            Integer hotelRoomTypeId,
            SurchargeRuleType ruleType,
            Short agePolicyId,
            LocalDate startDate,
            LocalDate endDate,
            Integer currentRuleId) {

        boolean hasOverlap = surchargeRuleRepository.existsOverlapping(
                hotelRoomTypeId,
                ruleType,
                agePolicyId,
                startDate,
                endDate,
                currentRuleId
        );

        if (hasOverlap) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_OVERLAP);
        }
    }
}
