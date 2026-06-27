package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.entity.DiscountRuleTypeConfig;
import com.hotel.booking.modules.pricing.mapper.DiscountRuleTypeConfigMapper;
import com.hotel.booking.modules.pricing.repository.DiscountRuleTypeConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountRuleTypeConfigServiceImplTest {

    @Mock
    DiscountRuleTypeConfigRepository discountRuleTypeConfigRepository;

    @Mock
    DiscountRuleTypeConfigMapper discountRuleTypeConfigMapper;

    @InjectMocks
    DiscountRuleTypeConfigServiceImpl discountRuleTypeConfigService;

    @Test
    void createDiscountRuleType_codeExists_throwsAppException() {
        DiscountRuleTypeCreateRequest request = DiscountRuleTypeCreateRequest.builder()
                .code("LONG_STAY")
                .displayName("Long Stay Rule")
                .priority((short) 100)
                .status(ActiveStatus.ACTIVE)
                .build();

        when(discountRuleTypeConfigRepository.existsById("LONG_STAY")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> 
            discountRuleTypeConfigService.createDiscountRuleType(request)
        );

        assertEquals(ErrorCode.DISCOUNT_RULE_TYPE_ALREADY_EXISTS, ex.getErrorCode());
        verify(discountRuleTypeConfigRepository, never()).save(any());
    }

    @Test
    void updateDiscountRuleType_notFound_throwsAppException() {
        DiscountRuleTypeUpdateRequest request = DiscountRuleTypeUpdateRequest.builder()
                .displayName("Long Stay Rule Updated")
                .priority((short) 110)
                .build();

        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> 
            discountRuleTypeConfigService.updateDiscountRuleType("LONG_STAY", request)
        );

        assertEquals(ErrorCode.DISCOUNT_RULE_TYPE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deleteDiscountRuleType_notFound_throwsAppException() {
        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> 
            discountRuleTypeConfigService.deleteDiscountRuleType("LONG_STAY")
        );

        assertEquals(ErrorCode.DISCOUNT_RULE_TYPE_NOT_FOUND, ex.getErrorCode());
    }
}
