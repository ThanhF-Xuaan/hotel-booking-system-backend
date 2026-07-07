package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.PricingRuleTypeResponse;
import com.hotel.booking.modules.pricing.entity.PricingRuleTypeConfig;
import com.hotel.booking.modules.pricing.mapper.PricingRuleTypeConfigMapper;
import com.hotel.booking.modules.pricing.repository.PricingRuleTypeConfigRepository;
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
public class PricingRuleTypeConfigServiceImpl implements PricingRuleTypeConfigService {

    PricingRuleTypeConfigRepository pricingRuleTypeConfigRepository;
    PricingRuleTypeConfigMapper pricingRuleTypeConfigMapper;

    @Override
    @Transactional
    public PricingRuleTypeResponse createPricingRuleType(PricingRuleTypeCreateRequest request) {
        log.info("Creating new pricing rule type with code: {}", request.getCode());

        if (pricingRuleTypeConfigRepository.existsById(request.getCode())) {
            throw new AppException(ErrorCode.PRICING_RULE_TYPE_ALREADY_EXISTS);
        }

        PricingRuleTypeConfig entity = pricingRuleTypeConfigMapper.toEntity(request);
        entity.setIsDeleted(false);
        if (entity.getStatus() == null) {
            entity.setStatus(ActiveStatus.ACTIVE);
        }

        PricingRuleTypeConfig saved = pricingRuleTypeConfigRepository.save(entity);
        log.info("Pricing rule type created successfully: {}", saved.getCode());
        return pricingRuleTypeConfigMapper.toResponse(saved);
    }

    @Override
    public List<PricingRuleTypeResponse> getAllPricingRuleTypes() {
        log.info("Fetching all active pricing rule types");
        return pricingRuleTypeConfigRepository.findAllByIsDeletedFalse().stream()
                .map(pricingRuleTypeConfigMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PricingRuleTypeResponse updatePricingRuleType(String code, PricingRuleTypeUpdateRequest request) {
        log.info("Updating pricing rule type: {}", code);

        PricingRuleTypeConfig entity = pricingRuleTypeConfigRepository.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new AppException(ErrorCode.PRICING_RULE_TYPE_NOT_FOUND));

        pricingRuleTypeConfigMapper.updateEntity(request, entity);

        PricingRuleTypeConfig updated = pricingRuleTypeConfigRepository.save(entity);
        log.info("Pricing rule type updated successfully: {}", updated.getCode());
        return pricingRuleTypeConfigMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deletePricingRuleType(String code) {
        log.info("Soft-deleting pricing rule type: {}", code);

        PricingRuleTypeConfig entity = pricingRuleTypeConfigRepository.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new AppException(ErrorCode.PRICING_RULE_TYPE_NOT_FOUND));

        entity.setIsDeleted(true);
        entity.setStatus(ActiveStatus.INACTIVE);
        pricingRuleTypeConfigRepository.save(entity);
        log.info("Pricing rule type soft-deleted successfully: {}", code);
    }
}
