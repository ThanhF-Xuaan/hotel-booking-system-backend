package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.PricingRuleResponse;
import com.hotel.booking.modules.pricing.entity.HolidayCalendar;
import com.hotel.booking.modules.pricing.entity.PricingRule;
import com.hotel.booking.modules.pricing.entity.PricingRuleTypeConfig;
import com.hotel.booking.modules.pricing.mapper.PricingRuleMapper;
import com.hotel.booking.modules.pricing.repository.HolidayCalendarRepository;
import com.hotel.booking.modules.pricing.repository.PricingRuleRepository;
import com.hotel.booking.modules.pricing.repository.PricingRuleTypeConfigRepository;
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
public class PricingRuleServiceImpl implements PricingRuleService {

    PricingRuleRepository pricingRuleRepository;
    HotelRoomTypeRepository hotelRoomTypeRepository;
    HolidayCalendarRepository holidayCalendarRepository;
    PricingRuleTypeConfigRepository pricingRuleTypeConfigRepository;
    PricingRuleMapper pricingRuleMapper;

    @Override
    @Transactional
    public PricingRuleResponse createPricingRule(PricingRuleCreateRequest request) {
        log.info("Creating new pricing rule for room type ID: {}", request.getHotelRoomTypeId());

        PricingRuleTypeConfig ruleTypeConfig = pricingRuleTypeConfigRepository.findByCodeAndIsDeletedFalse(request.getRuleTypeCode())
                .orElseThrow(() -> new AppException(ErrorCode.PRICING_RULE_TYPE_NOT_FOUND));

        HotelRoomType hotelRoomType = validateAndGetRoomType(request.getHotelRoomTypeId());
        HolidayCalendar holidayCalendar = validateAndGetHolidayCalendar(ruleTypeConfig.getCode(), request.getHolidayCalendarId());
        validatePricingRuleCommon(request.getAdjustmentValue(), request.getStartDate(), request.getEndDate());
        validateHolidayCalendarDateRange(holidayCalendar, request.getStartDate(), request.getEndDate());

        if (pricingRuleRepository.existsOverlapping(request.getHotelRoomTypeId(), ruleTypeConfig.getCode(), request.getStartDate(), request.getEndDate(), null)) {
            throw new AppException(ErrorCode.PRICING_RULE_OVERLAPPING);
        }

        PricingRule pricingRule = pricingRuleMapper.toEntity(request);
        pricingRule.setHotelRoomType(hotelRoomType);
        pricingRule.setHolidayCalendar(holidayCalendar);
        pricingRule.setRuleType(ruleTypeConfig);
        pricingRule.setIsDeleted(false);

        if (pricingRule.getStatus() == null) {
            pricingRule.setStatus(ActiveStatus.ACTIVE);
        }

        PricingRule saved = pricingRuleRepository.save(pricingRule);
        log.info("Pricing rule created successfully with ID: {}", saved.getId());
        return pricingRuleMapper.toResponse(saved);
    }

    @Override
    public PricingRuleResponse getPricingRuleById(Integer id) {
        log.info("Fetching pricing rule by ID: {}", id);
        PricingRule pricingRule = pricingRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRICING_RULE_NOT_FOUND));
        return pricingRuleMapper.toResponse(pricingRule);
    }

    @Override
    public List<PricingRuleResponse> getPricingRules(Integer hotelRoomTypeId) {
        if (hotelRoomTypeId != null) {
            log.info("Fetching all active pricing rules for hotelRoomTypeId: {}", hotelRoomTypeId);
            validateAndGetRoomType(hotelRoomTypeId);
            return pricingRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(hotelRoomTypeId).stream()
                    .map(pricingRuleMapper::toResponse)
                    .toList();
        } else {
            log.info("Fetching all active pricing rules");
            return pricingRuleRepository.findAllByIsDeletedFalse().stream()
                    .map(pricingRuleMapper::toResponse)
                    .toList();
        }
    }

    @Override
    @Transactional
    public PricingRuleResponse updatePricingRule(Integer id, PricingRuleUpdateRequest request) {
        log.info("Updating pricing rule with ID: {}", id);

        PricingRule pricingRule = pricingRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRICING_RULE_NOT_FOUND));

        PricingRuleTypeConfig ruleTypeConfig = pricingRuleTypeConfigRepository.findByCodeAndIsDeletedFalse(request.getRuleTypeCode())
                .orElseThrow(() -> new AppException(ErrorCode.PRICING_RULE_TYPE_NOT_FOUND));

        HotelRoomType hotelRoomType = validateAndGetRoomType(request.getHotelRoomTypeId());
        HolidayCalendar holidayCalendar = validateAndGetHolidayCalendar(ruleTypeConfig.getCode(), request.getHolidayCalendarId());
        validatePricingRuleCommon(request.getAdjustmentValue(), request.getStartDate(), request.getEndDate());
        validateHolidayCalendarDateRange(holidayCalendar, request.getStartDate(), request.getEndDate());

        if (pricingRuleRepository.existsOverlapping(request.getHotelRoomTypeId(), ruleTypeConfig.getCode(), request.getStartDate(), request.getEndDate(), id)) {
            throw new AppException(ErrorCode.PRICING_RULE_OVERLAPPING);
        }

        pricingRuleMapper.updateEntity(request, pricingRule);
        pricingRule.setHotelRoomType(hotelRoomType);
        pricingRule.setHolidayCalendar(holidayCalendar);
        pricingRule.setRuleType(ruleTypeConfig);

        PricingRule updated = pricingRuleRepository.save(pricingRule);
        log.info("Pricing rule updated successfully with ID: {}", updated.getId());
        return pricingRuleMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deletePricingRule(Integer id) {
        log.info("Soft-deleting pricing rule with ID: {}", id);

        PricingRule pricingRule = pricingRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRICING_RULE_NOT_FOUND));

        pricingRule.setIsDeleted(true);
        pricingRuleRepository.save(pricingRule);
        log.info("Pricing rule soft-deleted successfully with ID: {}", id);
    }

    private HotelRoomType validateAndGetRoomType(Integer hotelRoomTypeId) {
        if (hotelRoomTypeId == null) {
            throw new AppException(ErrorCode.PRICING_RULE_HOTEL_ROOM_TYPE_ID_NOT_NULL);
        }
        return hotelRoomTypeRepository.findByIdAndIsDeletedFalse(hotelRoomTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));
    }

    private HolidayCalendar validateAndGetHolidayCalendar(String ruleTypeCode, Integer holidayCalendarId) {
        if ("HOLIDAY".equalsIgnoreCase(ruleTypeCode)) {
            if (holidayCalendarId == null) {
                throw new AppException(ErrorCode.PRICING_RULE_HOLIDAY_CALENDAR_REQUIRED);
            }
            return holidayCalendarRepository.findByIdAndIsDeletedFalse(holidayCalendarId)
                    .orElseThrow(() -> new AppException(ErrorCode.HOLIDAY_NOT_FOUND));
        } else {
            if (holidayCalendarId != null) {
                throw new AppException(ErrorCode.PRICING_RULE_HOLIDAY_CALENDAR_MUST_BE_NULL);
            }
            return null;
        }
    }

    private void validatePricingRuleCommon(BigDecimal adjustmentValue, LocalDate startDate, LocalDate endDate) {
        if (adjustmentValue == null) {
            throw new AppException(ErrorCode.PRICING_RULE_ADJUSTMENT_VALUE_NOT_NULL);
        }
        if (adjustmentValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.PRICING_RULE_ADJUSTMENT_VALUE_INVALID);
        }
        if (startDate == null) {
            throw new AppException(ErrorCode.PRICING_RULE_START_DATE_NOT_NULL);
        }
        if (endDate == null) {
            throw new AppException(ErrorCode.PRICING_RULE_END_DATE_NOT_NULL);
        }
        if (startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.PRICING_RULE_INVALID_DATE_RANGE);
        }
    }

    private void validateHolidayCalendarDateRange(HolidayCalendar holidayCalendar, LocalDate startDate, LocalDate endDate) {
        if (holidayCalendar != null) {
            LocalDate holidayDate = holidayCalendar.getDate();
            if (holidayDate.isBefore(startDate) || holidayDate.isAfter(endDate)) {
                throw new AppException(ErrorCode.PRICING_RULE_HOLIDAY_DATE_OUT_OF_RANGE);
            }
        }
    }
}
