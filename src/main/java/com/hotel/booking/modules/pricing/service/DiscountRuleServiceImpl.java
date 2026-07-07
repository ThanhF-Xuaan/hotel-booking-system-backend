package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.pricing.dto.request.DiscountConditionRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountTierRequest;
import com.hotel.booking.modules.pricing.dto.response.DiscountRuleResponse;
import com.hotel.booking.modules.pricing.entity.Campaign;
import com.hotel.booking.modules.pricing.entity.DiscountRule;
import com.hotel.booking.modules.pricing.entity.DiscountRuleTypeConfig;
import com.hotel.booking.modules.pricing.entity.pojo.DiscountCondition;
import com.hotel.booking.modules.pricing.mapper.DiscountRuleMapper;
import com.hotel.booking.modules.pricing.repository.CampaignRepository;
import com.hotel.booking.modules.pricing.repository.DiscountRuleRepository;
import com.hotel.booking.modules.pricing.repository.DiscountRuleTypeConfigRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class DiscountRuleServiceImpl implements DiscountRuleService {

    DiscountRuleRepository discountRuleRepository;
    HotelRoomTypeRepository hotelRoomTypeRepository;
    CampaignRepository campaignRepository;
    DiscountRuleTypeConfigRepository discountRuleTypeConfigRepository;
    DiscountRuleMapper discountRuleMapper;

    @Override
    @Transactional
    public List<DiscountRuleResponse> createDiscountRule(DiscountRuleCreateRequest request) {
        log.info("Creating tiered discount rules for room type IDs: {}", request.getAppliedRoomTypeIds());

        // 1. In-memory duplicate tier checks within the request payload itself
        validateDuplicateTiers(request.getTiers(), request.getRuleTypeCode());

        // 2. Resolve Common Entities & Validate Dates
        DiscountRuleTypeConfig ruleTypeConfig = discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse(request.getRuleTypeCode())
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_RULE_TYPE_NOT_FOUND));

        Campaign campaign = validateAndGetCampaign(request.getCampaignId());
        validateDiscountRuleDates(request.getStartDate(), request.getEndDate());
        validateCampaignContainment(campaign, request.getStartDate(), request.getEndDate());

        // 3. Fetch applied room types in bulk to prevent N+1 issues
        if (request.getAppliedRoomTypeIds() == null || request.getAppliedRoomTypeIds().isEmpty()) {
            throw new AppException(ErrorCode.DISCOUNT_RULE_ROOM_TYPES_REQUIRED);
        }
        List<HotelRoomType> roomTypes = hotelRoomTypeRepository.findAllByIdInAndIsDeletedFalse(request.getAppliedRoomTypeIds());
        if (roomTypes.size() != request.getAppliedRoomTypeIds().size()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND);
        }

        // 4. Bulk Fetch Existing Rules that overlap
        List<DiscountRule> existingRules = discountRuleRepository.findOverlappingRules(
                request.getAppliedRoomTypeIds(),
                ruleTypeConfig.getCode(),
                request.getStartDate(),
                request.getEndDate()
        );

        // 5. Nested loops to construct entities (outer room types, inner tiers)
        List<DiscountRule> rulesToSave = new ArrayList<>();
        for (HotelRoomType roomType : roomTypes) {
            for (DiscountTierRequest tier : request.getTiers()) {
                validateDiscountRuleCommon(ruleTypeConfig.getCode(), tier.getConditions(), tier.getDiscountValue());

                // In-memory exact tier check against database rules
                DiscountCondition mappedCondition = mapToPojo(tier.getConditions());
                for (DiscountRule existingRule : existingRules) {
                    if (existingRule.getHotelRoomType().getId().equals(roomType.getId())) {
                        if (Objects.equals(existingRule.getConditions(), mappedCondition)) {
                            throw new AppException(ErrorCode.DISCOUNT_RULE_DUPLICATE_TIER_CONDITION);
                        }
                    }
                }

                DiscountRule discountRule = discountRuleMapper.toEntity(request, tier, roomType, campaign, ruleTypeConfig);
                rulesToSave.add(discountRule);
            }
        }

        // 6. Bulk database insert
        List<DiscountRule> savedRules = discountRuleRepository.saveAll(rulesToSave);
        log.info("Successfully created and saved {} discount rules", savedRules.size());

        // 7. Response generation
        return discountRuleMapper.toResponseList(savedRules);
    }

    @Override
    public DiscountRuleResponse getDiscountRuleById(Integer id) {
        log.info("Fetching discount rule by ID: {}", id);
        DiscountRule discountRule = discountRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_RULE_NOT_FOUND));
        return discountRuleMapper.toResponse(discountRule);
    }

    @Override
    public List<DiscountRuleResponse> getDiscountRules(Integer hotelRoomTypeId) {
        if (hotelRoomTypeId != null) {
            log.info("Fetching all active discount rules for hotelRoomTypeId: {}", hotelRoomTypeId);
            validateAndGetRoomType(hotelRoomTypeId);
            return discountRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(hotelRoomTypeId).stream()
                    .map(discountRuleMapper::toResponse)
                    .toList();
        } else {
            log.info("Fetching all active discount rules");
            return discountRuleRepository.findAllByIsDeletedFalse().stream()
                    .map(discountRuleMapper::toResponse)
                    .toList();
        }
    }

    @Override
    @Transactional
    public DiscountRuleResponse updateDiscountRule(Integer id, DiscountRuleUpdateRequest request) {
        log.info("Updating discount rule with ID: {}", id);

        DiscountRule discountRule = discountRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_RULE_NOT_FOUND));

        DiscountRuleTypeConfig ruleTypeConfig = discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse(request.getRuleTypeCode())
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_RULE_TYPE_NOT_FOUND));

        HotelRoomType roomType = validateAndGetRoomType(request.getHotelRoomTypeId());
        Campaign campaign = validateAndGetCampaign(request.getCampaignId());
        validateDiscountRuleDates(request.getStartDate(), request.getEndDate());
        validateCampaignContainment(campaign, request.getStartDate(), request.getEndDate());
        validateDiscountRuleCommon(ruleTypeConfig.getCode(), request.getConditions(), request.getDiscountValue());

        // No database-level overlapping checks executed here

        discountRuleMapper.updateEntity(request, discountRule);
        discountRule.setHotelRoomType(roomType);
        discountRule.setCampaign(campaign);
        discountRule.setRuleType(ruleTypeConfig);

        DiscountRule updated = discountRuleRepository.save(discountRule);
        log.info("Discount rule updated successfully with ID: {}", updated.getId());
        return discountRuleMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDiscountRule(Integer id) {
        log.info("Soft-deleting discount rule with ID: {}", id);
        DiscountRule discountRule = discountRuleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_RULE_NOT_FOUND));

        discountRule.setIsDeleted(true);
        discountRuleRepository.save(discountRule);
        log.info("Discount rule soft-deleted successfully with ID: {}", id);
    }

    private HotelRoomType validateAndGetRoomType(Integer hotelRoomTypeId) {
        if (hotelRoomTypeId == null) {
            throw new AppException(ErrorCode.DISCOUNT_RULE_HOTEL_ROOM_TYPE_ID_NOT_NULL);
        }
        return hotelRoomTypeRepository.findByIdAndIsDeletedFalse(hotelRoomTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));
    }

    private Campaign validateAndGetCampaign(Integer campaignId) {
        if (campaignId == null) {
            return null;
        }
        return campaignRepository.findByIdAndIsDeletedFalse(campaignId)
                .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_FOUND));
    }

    private void validateDiscountRuleDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new AppException(ErrorCode.DISCOUNT_RULE_START_DATE_NOT_NULL);
        }
        if (endDate == null) {
            throw new AppException(ErrorCode.DISCOUNT_RULE_END_DATE_NOT_NULL);
        }
        if (startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.DISCOUNT_RULE_INVALID_DATE_RANGE);
        }
    }

    private void validateCampaignContainment(Campaign campaign, LocalDate startDate, LocalDate endDate) {
        if (campaign != null) {
            if (startDate.isBefore(campaign.getStartDate()) || endDate.isAfter(campaign.getEndDate())) {
                throw new AppException(ErrorCode.DISCOUNT_RULE_OUTSIDE_CAMPAIGN_DATES);
            }
        }
    }

    private void validateDiscountRuleCommon(String ruleTypeCode, DiscountConditionRequest conditions, BigDecimal discountValue) {
        if (discountValue == null) {
            throw new AppException(ErrorCode.DISCOUNT_RULE_DISCOUNT_VALUE_NOT_NULL);
        }
        if (discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.DISCOUNT_RULE_VALUE_INVALID);
        }

        if ("LONG_STAY".equalsIgnoreCase(ruleTypeCode)) {
            if (conditions == null || conditions.getMinNights() == null || conditions.getMinNights() < 3) {
                throw new AppException(ErrorCode.DISCOUNT_RULE_MIN_NIGHTS_REQUIRED);
            }
        } else {
            if (conditions != null && conditions.getMinNights() != null) {
                throw new AppException(ErrorCode.DISCOUNT_RULE_MIN_NIGHTS_MUST_BE_NULL);
            }
        }
    }

    private void validateDuplicateTiers(List<DiscountTierRequest> tiers, String ruleTypeCode) {
        if (tiers == null || tiers.isEmpty()) {
            throw new AppException(ErrorCode.DISCOUNT_RULE_TIERS_REQUIRED);
        }

        // Generic equals check
        for (int i = 0; i < tiers.size(); i++) {
            DiscountTierRequest tierI = tiers.get(i);
            if (tierI.getConditions() == null) {
                throw new AppException(ErrorCode.DISCOUNT_RULE_TIER_CONDITIONS_NOT_NULL);
            }
            for (int j = i + 1; j < tiers.size(); j++) {
                DiscountTierRequest tierJ = tiers.get(j);
                if (Objects.equals(tierI.getConditions(), tierJ.getConditions())) {
                    throw new AppException(ErrorCode.DISCOUNT_RULE_DUPLICATE_CONDITIONS);
                }
            }
        }

        // Specific minNights duplicate check for LONG_STAY
        if ("LONG_STAY".equalsIgnoreCase(ruleTypeCode)) {
            Set<Integer> minNightsSet = new HashSet<>();
            for (DiscountTierRequest tier : tiers) {
                if (tier.getConditions() != null && tier.getConditions().getMinNights() != null) {
                    if (!minNightsSet.add(tier.getConditions().getMinNights())) {
                        throw new AppException(ErrorCode.DISCOUNT_RULE_DUPLICATE_CONDITIONS);
                    }
                }
            }
        }
    }

    private DiscountCondition mapToPojo(DiscountConditionRequest request) {
        if (request == null) {
            return null;
        }
        return DiscountCondition.builder()
                .minNights(request.getMinNights())
                .maxNights(request.getMaxNights())
                .minAdvanceBookingDays(request.getMinAdvanceBookingDays())
                .promoCode(request.getPromoCode())
                .build();
    }
}
