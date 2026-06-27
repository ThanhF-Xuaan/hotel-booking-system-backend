package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleUpdateRequest;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
import com.hotel.booking.modules.pricing.repository.SurchargeRuleRepository;
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
class SurchargeRuleServiceImplTest {

    @Mock
    SurchargeRuleRepository surchargeRuleRepository;

    @Mock
    HotelRoomTypeRepository hotelRoomTypeRepository;

    @InjectMocks
    SurchargeRuleServiceImpl surchargeRuleService;

    @Test
    void createSurchargeRule_valueInvalid_throwsException() {
        SurchargeRuleCreateRequest request = SurchargeRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleType(SurchargeRuleType.EXTRA_BED)
                .adjustmentType(AdjustmentType.FIXED)
                .adjustmentValue(new BigDecimal("-100")) // Negative value
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));

        AppException exception = assertThrows(AppException.class, () -> {
            surchargeRuleService.createSurchargeRule(request);
        });

        assertEquals(ErrorCode.SURCHARGE_RULE_VALUE_INVALID, exception.getErrorCode());
    }

    @Test
    void createSurchargeRule_invalidDateRange_throwsException() {
        SurchargeRuleCreateRequest request = SurchargeRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleType(SurchargeRuleType.EXTRA_BED)
                .adjustmentType(AdjustmentType.FIXED)
                .adjustmentValue(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, 7, 15))
                .endDate(LocalDate.of(2026, 7, 10)) // end date before start date
                .build();

        HotelRoomType roomType = new HotelRoomType();
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));

        AppException exception = assertThrows(AppException.class, () -> {
            surchargeRuleService.createSurchargeRule(request);
        });

        assertEquals(ErrorCode.SURCHARGE_RULE_INVALID_DATE_RANGE, exception.getErrorCode());
    }

    @Test
    void createSurchargeRule_roomTypeNotFound_throwsException() {
        SurchargeRuleCreateRequest request = SurchargeRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleType(SurchargeRuleType.EXTRA_BED)
                .adjustmentType(AdjustmentType.FIXED)
                .adjustmentValue(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            surchargeRuleService.createSurchargeRule(request);
        });

        assertEquals(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateSurchargeRule_surchargeRuleNotFound_throwsException() {
        SurchargeRuleUpdateRequest request = SurchargeRuleUpdateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleType(SurchargeRuleType.EXTRA_BED)
                .adjustmentType(AdjustmentType.FIXED)
                .adjustmentValue(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        when(surchargeRuleRepository.findByIdAndIsDeletedFalse(99)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            surchargeRuleService.updateSurchargeRule(99, request);
        });

        assertEquals(ErrorCode.SURCHARGE_RULE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createSurchargeRule_overlappingDates_throwsSurchargeRuleOverlapping() {
        SurchargeRuleCreateRequest request = SurchargeRuleCreateRequest.builder()
                .hotelRoomTypeId(1)
                .ruleType(SurchargeRuleType.EXTRA_BED)
                .adjustmentType(AdjustmentType.FIXED)
                .adjustmentValue(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, 7, 10))
                .endDate(LocalDate.of(2026, 7, 15))
                .build();

        HotelRoomType roomType = new HotelRoomType();
        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomType));
        when(surchargeRuleRepository.existsOverlapping(eq(1), eq(SurchargeRuleType.EXTRA_BED), any(), eq(LocalDate.of(2026, 7, 10)), eq(LocalDate.of(2026, 7, 15)), any()))
                .thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            surchargeRuleService.createSurchargeRule(request);
        });

        assertEquals(ErrorCode.SURCHARGE_RULE_OVERLAPPING, exception.getErrorCode());
    }
}
