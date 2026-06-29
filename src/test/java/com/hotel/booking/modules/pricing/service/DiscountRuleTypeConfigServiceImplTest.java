package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.DiscountRuleTypeResponse;
import com.hotel.booking.modules.pricing.entity.DiscountRuleTypeConfig;
import com.hotel.booking.modules.pricing.mapper.DiscountRuleTypeConfigMapper;
import com.hotel.booking.modules.pricing.repository.DiscountRuleTypeConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    void createDiscountRuleType_success() {
        DiscountRuleTypeCreateRequest request = DiscountRuleTypeCreateRequest.builder()
                .code("LONG_STAY")
                .displayName("Long Stay Rule")
                .priority((short) 100)
                .status(ActiveStatus.ACTIVE)
                .build();

        DiscountRuleTypeConfig entity = DiscountRuleTypeConfig.builder()
                .code("LONG_STAY")
                .displayName("Long Stay Rule")
                .priority((short) 100)
                .status(ActiveStatus.ACTIVE)
                .build();

        DiscountRuleTypeResponse response = DiscountRuleTypeResponse.builder()
                .code("LONG_STAY")
                .displayName("Long Stay Rule")
                .priority((short) 100)
                .status(ActiveStatus.ACTIVE)
                .build();

        when(discountRuleTypeConfigRepository.existsById("LONG_STAY")).thenReturn(false);
        when(discountRuleTypeConfigMapper.toEntity(request)).thenReturn(entity);
        when(discountRuleTypeConfigRepository.save(entity)).thenReturn(entity);
        when(discountRuleTypeConfigMapper.toResponse(entity)).thenReturn(response);

        DiscountRuleTypeResponse result = discountRuleTypeConfigService.createDiscountRuleType(request);

        assertNotNull(result);
        assertEquals("LONG_STAY", result.getCode());
        assertEquals("Long Stay Rule", result.getDisplayName());
        assertFalse(entity.getIsDeleted());
        assertEquals(ActiveStatus.ACTIVE, entity.getStatus());

        verify(discountRuleTypeConfigRepository, times(1)).existsById("LONG_STAY");
        verify(discountRuleTypeConfigRepository, times(1)).save(entity);
    }

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
    void getAllDiscountRuleTypes_success() {
        DiscountRuleTypeConfig entity1 = DiscountRuleTypeConfig.builder().code("LONG_STAY").isDeleted(false).build();
        DiscountRuleTypeConfig entity2 = DiscountRuleTypeConfig.builder().code("EARLY_BIRD").isDeleted(false).build();

        DiscountRuleTypeResponse resp1 = DiscountRuleTypeResponse.builder().code("LONG_STAY").build();
        DiscountRuleTypeResponse resp2 = DiscountRuleTypeResponse.builder().code("EARLY_BIRD").build();

        when(discountRuleTypeConfigRepository.findAllByIsDeletedFalse()).thenReturn(List.of(entity1, entity2));
        when(discountRuleTypeConfigMapper.toResponse(entity1)).thenReturn(resp1);
        when(discountRuleTypeConfigMapper.toResponse(entity2)).thenReturn(resp2);

        List<DiscountRuleTypeResponse> result = discountRuleTypeConfigService.getAllDiscountRuleTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("LONG_STAY", result.get(0).getCode());
        assertEquals("EARLY_BIRD", result.get(1).getCode());

        verify(discountRuleTypeConfigRepository, times(1)).findAllByIsDeletedFalse();
    }

    @Test
    void updateDiscountRuleType_success() {
        DiscountRuleTypeUpdateRequest request = DiscountRuleTypeUpdateRequest.builder()
                .displayName("Long Stay Rule Updated")
                .priority((short) 110)
                .build();

        DiscountRuleTypeConfig entity = DiscountRuleTypeConfig.builder()
                .code("LONG_STAY")
                .displayName("Long Stay Rule")
                .priority((short) 100)
                .isDeleted(false)
                .build();

        DiscountRuleTypeResponse response = DiscountRuleTypeResponse.builder()
                .code("LONG_STAY")
                .displayName("Long Stay Rule Updated")
                .priority((short) 110)
                .status(ActiveStatus.ACTIVE)
                .build();

        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.of(entity));
        when(discountRuleTypeConfigRepository.save(entity)).thenReturn(entity);
        when(discountRuleTypeConfigMapper.toResponse(entity)).thenReturn(response);

        DiscountRuleTypeResponse result = discountRuleTypeConfigService.updateDiscountRuleType("LONG_STAY", request);

        assertNotNull(result);
        assertEquals("LONG_STAY", result.getCode());
        assertEquals("Long Stay Rule Updated", result.getDisplayName());
        assertEquals((short) 110, result.getPriority());

        verify(discountRuleTypeConfigRepository, times(1)).findByCodeAndIsDeletedFalse("LONG_STAY");
        verify(discountRuleTypeConfigMapper, times(1)).updateEntity(request, entity);
        verify(discountRuleTypeConfigRepository, times(1)).save(entity);
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
        verify(discountRuleTypeConfigRepository, never()).save(any());
    }

    @Test
    void deleteDiscountRuleType_success() {
        DiscountRuleTypeConfig entity = DiscountRuleTypeConfig.builder()
                .code("LONG_STAY")
                .isDeleted(false)
                .status(ActiveStatus.ACTIVE)
                .build();

        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.of(entity));

        discountRuleTypeConfigService.deleteDiscountRuleType("LONG_STAY");

        assertTrue(entity.getIsDeleted());
        assertEquals(ActiveStatus.INACTIVE, entity.getStatus());

        verify(discountRuleTypeConfigRepository, times(1)).findByCodeAndIsDeletedFalse("LONG_STAY");
        verify(discountRuleTypeConfigRepository, times(1)).save(entity);
    }

    @Test
    void deleteDiscountRuleType_notFound_throwsAppException() {
        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> 
            discountRuleTypeConfigService.deleteDiscountRuleType("LONG_STAY")
        );

        assertEquals(ErrorCode.DISCOUNT_RULE_TYPE_NOT_FOUND, ex.getErrorCode());
        verify(discountRuleTypeConfigRepository, never()).save(any());
    }
}
