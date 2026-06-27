package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleCreateRequest;
import com.hotel.booking.modules.pricing.entity.Campaign;
import com.hotel.booking.modules.pricing.entity.DiscountRuleTypeConfig;
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
import java.util.Optional;

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

    @InjectMocks
    DiscountRuleServiceImpl discountRuleService;

    @Test
    void createDiscountRule_longStayMinNightsNull_throwsException() {
        DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleTypeCode("LONG_STAY")
                .minNights(null)
                .discountValue(BigDecimal.TEN)
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));

        AppException exception = assertThrows(AppException.class, () -> {
            discountRuleService.createDiscountRule(request);
        });

        assertEquals(ErrorCode.DISCOUNT_RULE_MIN_NIGHTS_REQUIRED, exception.getErrorCode());
    }

    @Test
    void createDiscountRule_longStayMinNightsLessThanThree_throwsException() {
        DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleTypeCode("LONG_STAY")
                .minNights((short) 2) // Under 3 nights
                .discountValue(BigDecimal.TEN)
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));

        AppException exception = assertThrows(AppException.class, () -> {
            discountRuleService.createDiscountRule(request);
        });

        assertEquals(ErrorCode.DISCOUNT_RULE_MIN_NIGHTS_REQUIRED, exception.getErrorCode());
    }

    @Test
    void createDiscountRule_otherRuleTypeWithMinNights_throwsException() {
        DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleTypeCode("PROMOTION")
                .minNights((short) 4) // minNights is set for non LONG_STAY rule
                .discountValue(BigDecimal.TEN)
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("PROMOTION").build();
        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("PROMOTION")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));

        AppException exception = assertThrows(AppException.class, () -> {
            discountRuleService.createDiscountRule(request);
        });

        assertEquals(ErrorCode.DISCOUNT_RULE_MIN_NIGHTS_MUST_BE_NULL, exception.getErrorCode());
    }

    @Test
    void createDiscountRule_invalidValueZeroOrNegative_throwsException() {
        DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleTypeCode("LONG_STAY")
                .minNights((short) 3)
                .discountValue(BigDecimal.ZERO)
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));

        AppException exception = assertThrows(AppException.class, () -> {
            discountRuleService.createDiscountRule(request);
        });

        assertEquals(ErrorCode.DISCOUNT_RULE_VALUE_INVALID, exception.getErrorCode());
    }

    @Test
    void createDiscountRule_invalidDateRange_throwsDiscountRuleInvalidDateRange() {
        DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleTypeCode("LONG_STAY")
                .minNights((short) 3)
                .discountValue(BigDecimal.TEN)
                .startDate(LocalDate.of(2026, 7, 15))
                .endDate(LocalDate.of(2026, 7, 10)) // end date before start date
                .build();

        HotelRoomType roomType = new HotelRoomType();
        DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));

        AppException exception = assertThrows(AppException.class, () -> {
            discountRuleService.createDiscountRule(request);
        });

        assertEquals(ErrorCode.DISCOUNT_RULE_INVALID_DATE_RANGE, exception.getErrorCode());
    }

    @Test
    void createDiscountRule_dateOutsideCampaign_throwsDiscountRuleOutsideCampaignDates() {
        DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .campaignId(2)
                .ruleTypeCode("LONG_STAY")
                .minNights((short) 3)
                .discountValue(BigDecimal.TEN)
                .startDate(LocalDate.of(2026, 7, 1)) // outside campaign start (July 5)
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        Campaign campaign = Campaign.builder()
                .id(2)
                .startDate(LocalDate.of(2026, 7, 5))
                .endDate(LocalDate.of(2026, 7, 30))
                .build();

        DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("LONG_STAY").build();
        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("LONG_STAY")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));
        when(campaignRepository.findByIdAndIsDeletedFalse(2)).thenReturn(Optional.of(campaign));

        AppException exception = assertThrows(AppException.class, () -> {
            discountRuleService.createDiscountRule(request);
        });

        assertEquals(ErrorCode.DISCOUNT_RULE_OUTSIDE_CAMPAIGN_DATES, exception.getErrorCode());
    }

    @Test
    void createDiscountRule_overlappingDates_throwsDiscountRuleOverlapping() {
        DiscountRuleCreateRequest request = DiscountRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleTypeCode("PROMOTION")
                .discountValue(BigDecimal.TEN)
                .startDate(LocalDate.of(2026, 7, 10))
                .endDate(LocalDate.of(2026, 7, 15))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        DiscountRuleTypeConfig config = DiscountRuleTypeConfig.builder().code("PROMOTION").build();
        when(discountRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("PROMOTION")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));
        when(discountRuleRepository.existsOverlapping(eq(1), eq("PROMOTION"), eq(LocalDate.of(2026, 7, 10)), eq(LocalDate.of(2026, 7, 15)), any()))
                .thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            discountRuleService.createDiscountRule(request);
        });

        assertEquals(ErrorCode.DISCOUNT_RULE_OVERLAPPING, exception.getErrorCode());
    }
}
