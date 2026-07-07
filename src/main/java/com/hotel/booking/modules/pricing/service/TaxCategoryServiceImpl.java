package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.dto.request.TaxCategoryCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.TaxCategoryUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.TaxCategoryResponse;
import com.hotel.booking.modules.pricing.entity.TaxCategory;
import com.hotel.booking.modules.pricing.mapper.TaxCategoryMapper;
import com.hotel.booking.modules.pricing.repository.TaxCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class TaxCategoryServiceImpl implements TaxCategoryService {

    TaxCategoryRepository taxCategoryRepository;
    TaxCategoryMapper taxCategoryMapper;

    @Override
    @Transactional
    public TaxCategoryResponse createTaxCategory(TaxCategoryCreateRequest request) {
        log.info("Creating new tax category with code: {}", request.getCategoryCode());

        if (taxCategoryRepository.existsByCategoryCodeAndIsDeletedFalse(request.getCategoryCode())) {
            throw new AppException(ErrorCode.TAX_CATEGORY_CODE_EXISTED);
        }

        TaxCategory entity = taxCategoryMapper.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(ActiveStatus.ACTIVE);
        }
        entity.setIsDeleted(false);

        TaxCategory saved = taxCategoryRepository.save(entity);
        log.info("Tax category created successfully with ID: {}", saved.getId());
        return taxCategoryMapper.toResponse(saved);
    }

    @Override
    public TaxCategoryResponse getTaxCategoryById(Integer id) {
        log.info("Fetching tax category by ID: {}", id);
        TaxCategory entity = taxCategoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAX_CATEGORY_NOT_FOUND));
        return taxCategoryMapper.toResponse(entity);
    }

    @Override
    public List<TaxCategoryResponse> getAllTaxCategories() {
        log.info("Fetching all active tax categories");
        return taxCategoryRepository.findAllByIsDeletedFalse().stream()
                .map(taxCategoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TaxCategoryResponse updateTaxCategory(Integer id, TaxCategoryUpdateRequest request) {
        log.info("Updating tax category with ID: {}", id);

        TaxCategory entity = taxCategoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAX_CATEGORY_NOT_FOUND));

        if (taxCategoryRepository.existsByCategoryCodeAndIdNotAndIsDeletedFalse(request.getCategoryCode(), id)) {
            throw new AppException(ErrorCode.TAX_CATEGORY_CODE_EXISTED);
        }

        taxCategoryMapper.updateEntity(request, entity);
        TaxCategory updated = taxCategoryRepository.save(entity);
        log.info("Tax category updated successfully with ID: {}", updated.getId());
        return taxCategoryMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTaxCategory(Integer id) {
        log.info("Soft-deleting tax category with ID: {}", id);
        TaxCategory entity = taxCategoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAX_CATEGORY_NOT_FOUND));

        entity.setIsDeleted(true);
        taxCategoryRepository.save(entity);
        log.info("Tax category soft-deleted successfully with ID: {}", id);
    }
}
