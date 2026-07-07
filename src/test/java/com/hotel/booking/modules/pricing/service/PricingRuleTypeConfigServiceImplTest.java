package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.entity.PricingRuleTypeConfig;
import com.hotel.booking.modules.pricing.mapper.PricingRuleTypeConfigMapper;
import com.hotel.booking.modules.pricing.repository.PricingRuleTypeConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingRuleTypeConfigServiceImplTest {

    @Mock
    PricingRuleTypeConfigRepository pricingRuleTypeConfigRepository;

    @Mock
    PricingRuleTypeConfigMapper pricingRuleTypeConfigMapper;

    @InjectMocks
    PricingRuleTypeConfigServiceImpl pricingRuleTypeConfigService;

    @Test
    void createPricingRuleType_codeExists_throwsAppException() {
        PricingRuleTypeCreateRequest request = PricingRuleTypeCreateRequest.builder()
                .code("HOLIDAY")
                .displayName("Holiday Rule")
                .priority((short) 100)
                .status(ActiveStatus.ACTIVE)
                .build();

        when(pricingRuleTypeConfigRepository.existsById("HOLIDAY")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> 
            pricingRuleTypeConfigService.createPricingRuleType(request)
        );

        assertEquals(ErrorCode.PRICING_RULE_TYPE_ALREADY_EXISTS, ex.getErrorCode());
        verify(pricingRuleTypeConfigRepository, never()).save(any());
    }

    @Test
    void updatePricingRuleType_notFound_throwsAppException() {
        PricingRuleTypeUpdateRequest request = PricingRuleTypeUpdateRequest.builder()
                .displayName("Holiday Rule Updated")
                .priority((short) 110)
                .build();

        when(pricingRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("HOLIDAY")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> 
            pricingRuleTypeConfigService.updatePricingRuleType("HOLIDAY", request)
        );

        assertEquals(ErrorCode.PRICING_RULE_TYPE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deletePricingRuleType_notFound_throwsAppException() {
        when(pricingRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("HOLIDAY")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> 
            pricingRuleTypeConfigService.deletePricingRuleType("HOLIDAY")
        );

        assertEquals(ErrorCode.PRICING_RULE_TYPE_NOT_FOUND, ex.getErrorCode());
    }
}
