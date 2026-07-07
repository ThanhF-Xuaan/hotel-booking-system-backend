package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.pricing.dto.request.DiscountConditionRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountTierRequest;
import com.hotel.booking.modules.pricing.entity.Campaign;
import com.hotel.booking.modules.pricing.entity.DiscountRule;
import com.hotel.booking.modules.pricing.entity.DiscountRuleTypeConfig;
import com.hotel.booking.modules.pricing.entity.pojo.DiscountCondition;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import com.hotel.booking.modules.pricing.mapper.DiscountRuleMapper;
import com.hotel.booking.modules.pricing.repository.CampaignRepository;
import com.hotel.booking.modules.pricing.repository.DiscountRuleRepository;
import com.hotel.booking.modules.pricing.repository.DiscountRuleTypeConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountRuleServiceImplTest {

        @Mock
        DiscountRuleRepository discountRuleRepository;

        @Mock
        HotelRoomTypeRepository hotelRoomTypeRepository;

        @Mock
        CampaignRepository campaignRepository;

        @Mock
        DiscountRuleTypeConfigRepository discountRuleTypeConfigRepository;

        @Mock
        DiscountRuleMapper discountRuleMapper;

        @InjectMocks
        DiscountRuleServiceImpl discountRuleService;

        @Test
        void createDiscountRule_longStayMinNightsNull_throwsException() {
                DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                                .appliedRoomTypeIds(Set.of(1))
                                .ruleTypeCode("LONG_STAY")
                                .startDate(LocalDate.of(2026, 7, 1))
                                .endDate(LocalDate.of(2026, 7, 31))
                                .tiers(List.of(
                                                DiscountTierRequest.builder()
                                                                .conditions(DiscountConditionRequest.builder().minNights(null)
                                                                                .build())
                                                                .discountType(AdjustmentType.PERCENT)
                                                                .discountValue(BigDecimal.TEN)
                                                                .build()))
                                .build();

                HotelRoomType roomType = new HotelRoomType();
                DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
                when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY"))
                                .thenReturn(Optional.of(config));
                when(hotelRoomTypeRepository.findAllByIdInAndIsDeletedFalse(Set.of(1))).thenReturn(List.of(roomType));

                AppException exception = assertThrows(AppException.class, () -> {
                        discountRuleService.createDiscountRule(request);
                });

                assertEquals(ErrorCode.DISCOUNT_RULE_MIN_NIGHTS_REQUIRED, exception.getErrorCode());
        }

        @Test
        void createDiscountRule_longStayMinNightsLessThanThree_throwsException() {
                DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                                .appliedRoomTypeIds(Set.of(1))
                                .ruleTypeCode("LONG_STAY")
                                .startDate(LocalDate.of(2026, 7, 1))
                                .endDate(LocalDate.of(2026, 7, 31))
                                .tiers(List.of(
                                                DiscountTierRequest.builder()
                                                                .conditions(DiscountConditionRequest.builder().minNights(2)
                                                                                .build()) // Under 3 nights
                                                                .discountType(AdjustmentType.PERCENT)
                                                                .discountValue(BigDecimal.TEN)
                                                                .build()))
                                .build();

                HotelRoomType roomType = new HotelRoomType();
                DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
                when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY"))
                                .thenReturn(Optional.of(config));
                when(hotelRoomTypeRepository.findAllByIdInAndIsDeletedFalse(Set.of(1))).thenReturn(List.of(roomType));

                AppException exception = assertThrows(AppException.class, () -> {
                        discountRuleService.createDiscountRule(request);
                });

                assertEquals(ErrorCode.DISCOUNT_RULE_MIN_NIGHTS_REQUIRED, exception.getErrorCode());
        }

        @Test
        void createDiscountRule_otherRuleTypeWithMinNights_throwsException() {
                DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                                .appliedRoomTypeIds(Set.of(1))
                                .ruleTypeCode("PROMOTION")
                                .startDate(LocalDate.of(2026, 7, 1))
                                .endDate(LocalDate.of(2026, 7, 31))
                                .tiers(List.of(
                                                DiscountTierRequest.builder()
                                                                .conditions(DiscountConditionRequest.builder().minNights(4)
                                                                                .build()) // minNights is set for non
                                                                                          // LONG_STAY rule
                                                                .discountType(AdjustmentType.PERCENT)
                                                                .discountValue(BigDecimal.TEN)
                                                                .build()))
                                .build();

                HotelRoomType roomType = new HotelRoomType();
                DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("PROMOTION").build();
                when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("PROMOTION"))
                                .thenReturn(Optional.of(config));
                when(hotelRoomTypeRepository.findAllByIdInAndIsDeletedFalse(Set.of(1))).thenReturn(List.of(roomType));

                AppException exception = assertThrows(AppException.class, () -> {
                        discountRuleService.createDiscountRule(request);
                });

                assertEquals(ErrorCode.DISCOUNT_RULE_MIN_NIGHTS_MUST_BE_NULL, exception.getErrorCode());
        }

        @Test
        void createDiscountRule_invalidValueZeroOrNegative_throwsException() {
                DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                                .appliedRoomTypeIds(Set.of(1))
                                .ruleTypeCode("LONG_STAY")
                                .startDate(LocalDate.of(2026, 7, 1))
                                .endDate(LocalDate.of(2026, 7, 31))
                                .tiers(List.of(
                                                DiscountTierRequest.builder()
                                                                .conditions(DiscountConditionRequest.builder().minNights(3)
                                                                                .build())
                                                                .discountType(AdjustmentType.PERCENT)
                                                                .discountValue(BigDecimal.ZERO)
                                                                .build()))
                                .build();

                HotelRoomType roomType = new HotelRoomType();
                DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
                when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY"))
                                .thenReturn(Optional.of(config));
                when(hotelRoomTypeRepository.findAllByIdInAndIsDeletedFalse(Set.of(1))).thenReturn(List.of(roomType));

                AppException exception = assertThrows(AppException.class, () -> {
                        discountRuleService.createDiscountRule(request);
                });

                assertEquals(ErrorCode.DISCOUNT_RULE_VALUE_INVALID, exception.getErrorCode());
        }

        @Test
        void createDiscountRule_invalidDateRange_throwsDiscountRuleInvalidDateRange() {
                DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                                .appliedRoomTypeIds(Set.of(1))
                                .ruleTypeCode("LONG_STAY")
                                .startDate(LocalDate.of(2026, 7, 15))
                                .endDate(LocalDate.of(2026, 7, 10)) // end date before start date
                                .tiers(List.of(
                                                DiscountTierRequest.builder()
                                                                .conditions(DiscountConditionRequest.builder().minNights(3)
                                                                                .build())
                                                                .discountType(AdjustmentType.PERCENT)
                                                                .discountValue(BigDecimal.TEN)
                                                                .build()))
                                .build();

                DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
                when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY"))
                                .thenReturn(Optional.of(config));

                AppException exception = assertThrows(AppException.class, () -> {
                        discountRuleService.createDiscountRule(request);
                });

                assertEquals(ErrorCode.DISCOUNT_RULE_INVALID_DATE_RANGE, exception.getErrorCode());
        }

        @Test
        void createDiscountRule_dateOutsideCampaign_throwsDiscountRuleOutsideCampaignDates() {
                DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                                .appliedRoomTypeIds(Set.of(1))
                                .campaignId(2)
                                .ruleTypeCode("LONG_STAY")
                                .startDate(LocalDate.of(2026, 7, 1)) // outside campaign start (July 5)
                                .endDate(LocalDate.of(2026, 7, 31))
                                .tiers(List.of(
                                                DiscountTierRequest.builder()
                                                                .conditions(DiscountConditionRequest.builder().minNights(3)
                                                                                .build())
                                                                .discountType(AdjustmentType.PERCENT)
                                                                .discountValue(BigDecimal.TEN)
                                                                .build()))
                                .build();

                Campaign campaign = Campaign.builder()
                                .id(2)
                                .startDate(LocalDate.of(2026, 7, 5))
                                .endDate(LocalDate.of(2026, 7, 30))
                                .build();

                DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
                when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY"))
                                .thenReturn(Optional.of(config));
                when(campaignRepository.findByIdAndIsDeletedFalse(2)).thenReturn(Optional.of(campaign));

                AppException exception = assertThrows(AppException.class, () -> {
                        discountRuleService.createDiscountRule(request);
                });

                assertEquals(ErrorCode.DISCOUNT_RULE_OUTSIDE_CAMPAIGN_DATES, exception.getErrorCode());
        }

        @Test
        void createDiscountRule_duplicateConditions_throwsDiscountRuleDuplicateConditions() {
                DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                                .appliedRoomTypeIds(Set.of(1))
                                .ruleTypeCode("LONG_STAY")
                                .startDate(LocalDate.of(2026, 7, 1))
                                .endDate(LocalDate.of(2026, 7, 31))
                                .tiers(List.of(
                                                DiscountTierRequest.builder()
                                                                .conditions(DiscountConditionRequest.builder().minNights(3)
                                                                                .build())
                                                                .discountType(AdjustmentType.PERCENT)
                                                                .discountValue(BigDecimal.TEN)
                                                                .build(),
                                                DiscountTierRequest.builder()
                                                                .conditions(DiscountConditionRequest.builder().minNights(3)
                                                                                .build()) // Duplicate minNights
                                                                .discountType(AdjustmentType.PERCENT)
                                                                .discountValue(BigDecimal.valueOf(15))
                                                                .build()))
                                .build();

                AppException exception = assertThrows(AppException.class, () -> {
                        discountRuleService.createDiscountRule(request);
                });

                assertEquals(ErrorCode.DISCOUNT_RULE_DUPLICATE_CONDITIONS, exception.getErrorCode());
        }

        @Test
        void createDiscountRule_duplicateTierConditionInDb_throwsDiscountRuleDuplicateTierCondition() {
                DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                                .appliedRoomTypeIds(Set.of(1))
                                .ruleTypeCode("LONG_STAY")
                                .startDate(LocalDate.of(2026, 7, 1))
                                .endDate(LocalDate.of(2026, 7, 31))
                                .tiers(List.of(
                                                DiscountTierRequest.builder()
                                                                .conditions(DiscountConditionRequest.builder().minNights(3)
                                                                                .build())
                                                                .discountType(AdjustmentType.PERCENT)
                                                                .discountValue(BigDecimal.TEN)
                                                                .build()))
                                .build();

                HotelRoomType roomType = new HotelRoomType();
                roomType.setId(1);
                DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();

                DiscountCondition existingCondition = DiscountCondition.builder().minNights(3).build();

                DiscountRule existingRule = new DiscountRule();
                existingRule.setHotelRoomType(roomType);
                existingRule.setConditions(existingCondition);

                when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY"))
                                .thenReturn(Optional.of(config));
                when(hotelRoomTypeRepository.findAllByIdInAndIsDeletedFalse(Set.of(1))).thenReturn(List.of(roomType));
                when(discountRuleRepository.findOverlappingRules(Set.of(1), "LONG_STAY", request.getStartDate(), request.getEndDate()))
                                .thenReturn(List.of(existingRule));

                AppException exception = assertThrows(AppException.class, () -> {
                        discountRuleService.createDiscountRule(request);
                });

                assertEquals(ErrorCode.DISCOUNT_RULE_DUPLICATE_TIER_CONDITION, exception.getErrorCode());
        }
}
