package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.dto.request.TaxCategoryCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.TaxCategoryUpdateRequest;
import com.hotel.booking.modules.pricing.entity.TaxCategory;
import com.hotel.booking.modules.pricing.mapper.TaxCategoryMapper;
import com.hotel.booking.modules.pricing.repository.TaxCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxCategoryServiceImplTest {

    @Mock
    TaxCategoryRepository taxCategoryRepository;

    @Mock
    TaxCategoryMapper taxCategoryMapper;

    @InjectMocks
    TaxCategoryServiceImpl taxCategoryService;

    @Test
    void createTaxCategory_codeExisted_throwsException() {
        TaxCategoryCreateRequest request = TaxCategoryCreateRequest.builder()
                .categoryCode("ROOM")
                .categoryName("Room Service Tax")
                .build();

        when(taxCategoryRepository.existsByCategoryCodeAndIsDeletedFalse("ROOM")).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            taxCategoryService.createTaxCategory(request);
        });

        assertEquals(ErrorCode.TAX_CATEGORY_CODE_EXISTED, exception.getErrorCode());
    }

    @Test
    void updateTaxCategory_notFound_throwsException() {
        TaxCategoryUpdateRequest request = TaxCategoryUpdateRequest.builder()
                .categoryCode("ROOM")
                .categoryName("Room Service Tax")
                .build();

        when(taxCategoryRepository.findByIdAndIsDeletedFalse(99)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            taxCategoryService.updateTaxCategory(99, request);
        });

        assertEquals(ErrorCode.TAX_CATEGORY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateTaxCategory_codeExisted_throwsException() {
        TaxCategoryUpdateRequest request = TaxCategoryUpdateRequest.builder()
                .categoryCode("ROOM")
                .categoryName("Room Service Tax")
                .build();

        TaxCategory entity = new TaxCategory();
        when(taxCategoryRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(entity));
        when(taxCategoryRepository.existsByCategoryCodeAndIdNotAndIsDeletedFalse("ROOM", 1)).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            taxCategoryService.updateTaxCategory(1, request);
        });

        assertEquals(ErrorCode.TAX_CATEGORY_CODE_EXISTED, exception.getErrorCode());
    }
}
