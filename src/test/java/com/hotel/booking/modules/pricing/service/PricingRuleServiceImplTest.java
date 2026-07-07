package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleCreateRequest;
import com.hotel.booking.modules.pricing.entity.HolidayCalendar;
import com.hotel.booking.modules.pricing.entity.PricingRuleTypeConfig;
import com.hotel.booking.modules.pricing.repository.HolidayCalendarRepository;
import com.hotel.booking.modules.pricing.repository.PricingRuleRepository;
import com.hotel.booking.modules.pricing.repository.PricingRuleTypeConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingRuleServiceImplTest {

    @Mock
    PricingRuleRepository pricingRuleRepository;

    @Mock
    HotelRoomTypeRepository hotelRoomTypeRepository;

    @Mock
    HolidayCalendarRepository holidayCalendarRepository;

    @Mock
    PricingRuleTypeConfigRepository pricingRuleTypeConfigRepository;

    @InjectMocks
    PricingRuleServiceImpl pricingRuleService;

    @Test
    void createPricingRule_holidayDateBeforeRange_throwsHolidayDateOutOfRange() {
        PricingRuleCreateRequest request = PricingRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .holidayCalendarId(2)
                .ruleTypeCode("HOLIDAY")
                .adjustmentValue(BigDecimal.TEN)
                .startDate(LocalDate.of(2026, 7, 10))
                .endDate(LocalDate.of(2026, 7, 15))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        HolidayCalendar holiday = HolidayCalendar.builder()
                .id(2)
                .date(LocalDate.of(2026, 7, 9)) // before start date (July 10)
                .build();

        PricingRuleTypeConfig config = PricingRuleTypeConfig.builder().code("HOLIDAY").build();
        when(pricingRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("HOLIDAY")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));
        when(holidayCalendarRepository.findByIdAndIsDeletedFalse(2)).thenReturn(Optional.of(holiday));

        AppException exception = assertThrows(AppException.class, () -> {
            pricingRuleService.createPricingRule(request);
        });

        assertEquals(ErrorCode.PRICING_RULE_HOLIDAY_DATE_OUT_OF_RANGE, exception.getErrorCode());
    }

    @Test
    void createPricingRule_holidayDateAfterRange_throwsHolidayDateOutOfRange() {
        PricingRuleCreateRequest request = PricingRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .holidayCalendarId(2)
                .ruleTypeCode("HOLIDAY")
                .adjustmentValue(BigDecimal.TEN)
                .startDate(LocalDate.of(2026, 7, 10))
                .endDate(LocalDate.of(2026, 7, 15))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        HolidayCalendar holiday = HolidayCalendar.builder()
                .id(2)
                .date(LocalDate.of(2026, 7, 16)) // after end date (July 15)
                .build();

        PricingRuleTypeConfig config = PricingRuleTypeConfig.builder().code("HOLIDAY").build();
        when(pricingRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("HOLIDAY")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));
        when(holidayCalendarRepository.findByIdAndIsDeletedFalse(2)).thenReturn(Optional.of(holiday));

        AppException exception = assertThrows(AppException.class, () -> {
            pricingRuleService.createPricingRule(request);
        });

        assertEquals(ErrorCode.PRICING_RULE_HOLIDAY_DATE_OUT_OF_RANGE, exception.getErrorCode());
    }

    @Test
    void createPricingRule_overlappingDates_throwsPricingRuleOverlapping() {
        PricingRuleCreateRequest request = PricingRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleTypeCode("CUSTOMIZATION")
                .adjustmentValue(BigDecimal.TEN)
                .startDate(LocalDate.of(2026, 7, 10))
                .endDate(LocalDate.of(2026, 7, 15))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        PricingRuleTypeConfig config = PricingRuleTypeConfig.builder().code("CUSTOMIZATION").build();
        when(pricingRuleTypeConfigRepository.findByCodeAndIsDeletedFalse("CUSTOMIZATION")).thenReturn(Optional.of(config));
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));
        when(pricingRuleRepository.existsOverlapping(eq(1), eq("CUSTOMIZATION"), eq(LocalDate.of(2026, 7, 10)), eq(LocalDate.of(2026, 7, 15)), any()))
                .thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            pricingRuleService.createPricingRule(request);
        });

        assertEquals(ErrorCode.PRICING_RULE_OVERLAPPING, exception.getErrorCode());
    }
}
