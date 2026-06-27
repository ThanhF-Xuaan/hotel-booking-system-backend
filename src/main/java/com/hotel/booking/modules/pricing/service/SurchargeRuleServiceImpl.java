package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.SurchargeRuleResponse;
import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import com.hotel.booking.modules.pricing.mapper.SurchargeRuleMapper;
import com.hotel.booking.modules.pricing.repository.SurchargeRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class SurchargeRuleServiceImpl implements SurchargeRuleService {

    SurchargeRuleRepository surchargeRuleRepository;
    HotelRoomTypeRepository hotelRoomTypeRepository;
    SurchargeRuleMapper surchargeRuleMapper;

    @Override
    @Transactional
    public SurchargeRuleResponse createSurchargeRule(SurchargeRuleCreateRequest request) {
        log.info("Creating new surcharge rule for room type ID: {}", request.getHotelRoomTypeId());

        HotelRoomType roomType = validateAndGetRoomType(request.getHotelRoomTypeId());
        validateSurchargeRuleCommon(request.getAdjustmentValue(), request.getStartDate(), request.getEndDate());

        if (surchargeRuleRepository.existsOverlapping(request.getHotelRoomTypeId(), request.getRuleType(), request.getGuestType(), request.getStartDate(), request.getEndDate(), null)) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_OVERLAPPING);
        }

        SurchargeRule surchargeRule = surchargeRuleMapper.toEntity(request);
        surchargeRule.setHotelRoomType(roomType);
        surchargeRule.setIsDeleted(false);

        if (surchargeRule.getStatus() == null) {
            surchargeRule.setStatus(ActiveStatus.ACTIVE);
        }

        SurchargeRule saved = surchargeRuleRepository.save(surchargeRule);
        log.info("Surcharge rule created successfully with ID: {}", saved.getId());
        return surchargeRuleMapper.toResponse(saved);
    }

    @Override
    public SurchargeRuleResponse getSurchargeRuleById(Integer id) {
        log.info("Fetching surcharge rule by ID: {}", id);
        SurchargeRule surchargeRule = surchargeRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SURCHARGE_RULE_NOT_FOUND));
        return surchargeRuleMapper.toResponse(surchargeRule);
    }

    @Override
    public List<SurchargeRuleResponse> getSurchargeRules(Integer hotelRoomTypeId) {
        if (hotelRoomTypeId != null) {
            log.info("Fetching all active surcharge rules for hotelRoomTypeId: {}", hotelRoomTypeId);
            validateAndGetRoomType(hotelRoomTypeId);
            return surchargeRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(hotelRoomTypeId).stream()
                    .map(surchargeRuleMapper::toResponse)
                    .toList();
        } else {
            log.info("Fetching all active surcharge rules");
            return surchargeRuleRepository.findAllByIsDeletedFalse().stream()
                    .map(surchargeRuleMapper::toResponse)
                    .toList();
        }
    }

    @Override
    @Transactional
    public SurchargeRuleResponse updateSurchargeRule(Integer id, SurchargeRuleUpdateRequest request) {
        log.info("Updating surcharge rule with ID: {}", id);

        SurchargeRule surchargeRule = surchargeRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SURCHARGE_RULE_NOT_FOUND));

        HotelRoomType roomType = validateAndGetRoomType(request.getHotelRoomTypeId());
        validateSurchargeRuleCommon(request.getAdjustmentValue(), request.getStartDate(), request.getEndDate());

        if (surchargeRuleRepository.existsOverlapping(request.getHotelRoomTypeId(), request.getRuleType(), request.getGuestType(), request.getStartDate(), request.getEndDate(), id)) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_OVERLAPPING);
        }

        surchargeRuleMapper.updateEntity(request, surchargeRule);
        surchargeRule.setHotelRoomType(roomType);

        SurchargeRule updated = surchargeRuleRepository.save(surchargeRule);
        log.info("Surcharge rule updated successfully with ID: {}", updated.getId());
        return surchargeRuleMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSurchargeRule(Integer id) {
        log.info("Soft-deleting surcharge rule with ID: {}", id);
        SurchargeRule surchargeRule = surchargeRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SURCHARGE_RULE_NOT_FOUND));

        surchargeRule.setIsDeleted(true);
        surchargeRuleRepository.save(surchargeRule);
        log.info("Surcharge rule soft-deleted successfully with ID: {}", id);
    }

    private HotelRoomType validateAndGetRoomType(Integer hotelRoomTypeId) {
        if (hotelRoomTypeId == null) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_HOTEL_ROOM_TYPE_ID_NOT_NULL);
        }
        return hotelRoomTypeRepository.findByIdAndIsDeletedFalse(hotelRoomTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));
    }

    private void validateSurchargeRuleCommon(BigDecimal adjustmentValue, LocalDate startDate, LocalDate endDate) {
        if (adjustmentValue == null) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_ADJUSTMENT_VALUE_NOT_NULL);
        }
        if (adjustmentValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_VALUE_INVALID);
        }
        if (startDate == null) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_START_DATE_NOT_NULL);
        }
        if (endDate == null) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_END_DATE_NOT_NULL);
        }
        if (startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_INVALID_DATE_RANGE);
        }
    }
}
