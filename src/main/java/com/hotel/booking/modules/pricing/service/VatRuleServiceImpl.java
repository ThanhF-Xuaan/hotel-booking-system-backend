package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.dto.request.VatRuleCreationRequest;
import com.hotel.booking.modules.pricing.dto.request.VatRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.VatRuleResponse;
import com.hotel.booking.modules.pricing.entity.VatRule;
import com.hotel.booking.modules.pricing.mapper.VatRuleMapper;
import com.hotel.booking.modules.pricing.repository.VatRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class VatRuleServiceImpl implements VatRuleService {

    VatRuleRepository vatRuleRepository;
    VatRuleMapper vatRuleMapper;

    @Override
    @Transactional
    public VatRuleResponse createVatRule(VatRuleCreationRequest request) {
        log.info("Creating new vat rule with code: {}", request.getVatCode());

        if (vatRuleRepository.existsByVatCodeAndIsDeletedFalse(request.getVatCode())) {
            throw new AppException(ErrorCode.VAT_RULE_CODE_EXISTED);
        }

        if (vatRuleRepository.existsByVatNameAndIsDeletedFalse(request.getVatName())) {
            throw new AppException(ErrorCode.VAT_RULE_NAME_EXISTED);
        }

        validateDateRange(request.getStartDate(), request.getEndDate());

        VatRule vatRule = vatRuleMapper.toEntity(request);
        
        if (vatRule.getStatus() == null) {
            vatRule.setStatus(ActiveStatus.ACTIVE);
        }
        vatRule.setIsDeleted(false);

        VatRule savedRule = vatRuleRepository.save(vatRule);
        log.info("Vat rule created successfully with id: {}", savedRule.getId());
        return vatRuleMapper.toResponse(savedRule);
    }

    @Override
    public List<VatRuleResponse> getAllVatRules() {
        log.info("Fetching all active vat rules");
        return vatRuleRepository.findAllByIsDeletedFalse()
                .stream()
                .map(vatRuleMapper::toResponse)
                .toList();
    }

    @Override
    public VatRuleResponse getVatRuleById(Integer id) {
        log.info("Fetching vat rule with id: {}", id);
        VatRule vatRule = vatRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.VAT_RULE_NOT_FOUND));
        return vatRuleMapper.toResponse(vatRule);
    }

    @Override
    @Transactional
    public VatRuleResponse updateVatRule(Integer id, VatRuleUpdateRequest request) {
        log.info("Updating vat rule with id: {}", id);

        VatRule vatRule = vatRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.VAT_RULE_NOT_FOUND));

        if (vatRuleRepository.existsByVatNameAndIdNotAndIsDeletedFalse(request.getVatName(), id)) {
            throw new AppException(ErrorCode.VAT_RULE_NAME_EXISTED);
        }

        LocalDate finalStartDate = request.getStartDate() != null ? request.getStartDate() : vatRule.getStartDate();
        LocalDate finalEndDate = request.getEndDate() != null ? request.getEndDate() : vatRule.getEndDate();
        validateDateRange(finalStartDate, finalEndDate);

        vatRuleMapper.updateEntity(request, vatRule);
        VatRule savedRule = vatRuleRepository.save(vatRule);

        log.info("Vat rule updated successfully with id: {}", savedRule.getId());
        return vatRuleMapper.toResponse(savedRule);
    }

    @Override
    @Transactional
    public void deleteVatRule(Integer id) {
        log.info("Soft-deleting vat rule with id: {}", id);

        VatRule vatRule = vatRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.VAT_RULE_NOT_FOUND));

        vatRule.setIsDeleted(true);
        vatRuleRepository.save(vatRule);

        log.info("Vat rule soft-deleted successfully with id: {}", id);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.VAT_RULE_INVALID_DATE_RANGE);
        }
    }
}
