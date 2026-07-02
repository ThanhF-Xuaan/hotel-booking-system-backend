package com.hotel.booking.modules.pricing.validator;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
import com.hotel.booking.modules.pricing.repository.SurchargeRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurchargeRuleValidatorTest {

    @Mock
    SurchargeRuleRepository surchargeRuleRepository;

    @InjectMocks
    SurchargeRuleValidator surchargeRuleValidator;

    @Test
    void validateNoOverlap_differentAgePolicy_shouldSuccess() {
        // Arrange
        Integer hotelRoomTypeId = 1;
        SurchargeRuleType ruleType = SurchargeRuleType.EXTRA_PERSON;
        Short agePolicyId = 2;
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 10);
        Integer currentRuleId = null;

        when(surchargeRuleRepository.existsOverlapping(
                hotelRoomTypeId, ruleType, agePolicyId, startDate, endDate, currentRuleId))
                .thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> surchargeRuleValidator.validateNoOverlap(
                hotelRoomTypeId, ruleType, agePolicyId, startDate, endDate, currentRuleId));

        verify(surchargeRuleRepository).existsOverlapping(
                hotelRoomTypeId, ruleType, agePolicyId, startDate, endDate, currentRuleId);
    }

    @Test
    void validateNoOverlap_sameAgePolicyOrWildcard_shouldFail() {
        // Arrange
        Integer hotelRoomTypeId = 1;
        SurchargeRuleType ruleType = SurchargeRuleType.EXTRA_PERSON;
        Short agePolicyId = 2;
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 10);
        Integer currentRuleId = null;

        when(surchargeRuleRepository.existsOverlapping(
                hotelRoomTypeId, ruleType, agePolicyId, startDate, endDate, currentRuleId))
                .thenReturn(true);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () ->
                surchargeRuleValidator.validateNoOverlap(
                        hotelRoomTypeId, ruleType, agePolicyId, startDate, endDate, currentRuleId));

        assertEquals(ErrorCode.SURCHARGE_RULE_OVERLAP, exception.getErrorCode());
        verify(surchargeRuleRepository).existsOverlapping(
                hotelRoomTypeId, ruleType, agePolicyId, startDate, endDate, currentRuleId);
    }

    @Test
    void validateNoOverlap_updateExistingRuleSameParameters_shouldSuccess() {
        // Arrange
        Integer hotelRoomTypeId = 1;
        SurchargeRuleType ruleType = SurchargeRuleType.EXTRA_PERSON;
        Short agePolicyId = 2;
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 10);
        Integer currentRuleId = 42;

        when(surchargeRuleRepository.existsOverlapping(
                hotelRoomTypeId, ruleType, agePolicyId, startDate, endDate, currentRuleId))
                .thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> surchargeRuleValidator.validateNoOverlap(
                hotelRoomTypeId, ruleType, agePolicyId, startDate, endDate, currentRuleId));

        verify(surchargeRuleRepository).existsOverlapping(
                hotelRoomTypeId, ruleType, agePolicyId, startDate, endDate, currentRuleId);
    }
}
