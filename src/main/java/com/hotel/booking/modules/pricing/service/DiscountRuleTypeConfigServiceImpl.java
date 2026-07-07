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
public class DiscountRuleTypeConfigServiceImpl implements DiscountRuleTypeConfigService {

    DiscountRuleTypeConfigRepository discountRuleTypeConfigRepository;
    DiscountRuleTypeConfigMapper discountRuleTypeConfigMapper;

    @Override
    @Transactional
    public DiscountRuleTypeResponse createDiscountRuleType(DiscountRuleTypeCreateRequest request) {
        log.info("Creating new discount rule type with code: {}", request.getCode());

        if (discountRuleTypeConfigRepository.existsById(request.getCode())) {
            throw new AppException(ErrorCode.DISCOUNT_RULE_TYPE_ALREADY_EXISTS);
        }

        DiscountRuleTypeConfig entity = discountRuleTypeConfigMapper.toEntity(request);
        entity.setIsDeleted(false);
        if (entity.getStatus() == null) {
            entity.setStatus(ActiveStatus.ACTIVE);
        }

        DiscountRuleTypeConfig saved = discountRuleTypeConfigRepository.save(entity);
        log.info("Discount rule type created successfully: {}", saved.getCode());
        return discountRuleTypeConfigMapper.toResponse(saved);
    }

    @Override
    public List<DiscountRuleTypeResponse> getAllDiscountRuleTypes() {
        log.info("Fetching all active discount rule types");
        return discountRuleTypeConfigRepository.findAllByIsDeletedFalse().stream()
                .map(discountRuleTypeConfigMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DiscountRuleTypeResponse updateDiscountRuleType(String code, DiscountRuleTypeUpdateRequest request) {
        log.info("Updating discount rule type: {}", code);

        DiscountRuleTypeConfig entity = discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_RULE_TYPE_NOT_FOUND));

        discountRuleTypeConfigMapper.updateEntity(request, entity);

        DiscountRuleTypeConfig updated = discountRuleTypeConfigRepository.save(entity);
        log.info("Discount rule type updated successfully: {}", updated.getCode());
        return discountRuleTypeConfigMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDiscountRuleType(String code) {
        log.info("Soft-deleting discount rule type: {}", code);

        DiscountRuleTypeConfig entity = discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_RULE_TYPE_NOT_FOUND));

        entity.setIsDeleted(true);
        entity.setStatus(ActiveStatus.INACTIVE);
        discountRuleTypeConfigRepository.save(entity);
        log.info("Discount rule type soft-deleted successfully: {}", code);
    }
}
