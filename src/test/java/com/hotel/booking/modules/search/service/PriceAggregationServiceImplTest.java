package com.hotel.booking.modules.search.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.RoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.pricing.entity.DiscountRule;
import com.hotel.booking.modules.pricing.entity.DiscountRuleTypeConfig;
import com.hotel.booking.modules.pricing.entity.HotelAgePolicy;
import com.hotel.booking.modules.pricing.entity.PricingRule;
import com.hotel.booking.modules.pricing.entity.PricingRuleTypeConfig;
import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import com.hotel.booking.modules.pricing.entity.TaxCategory;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
import com.hotel.booking.modules.pricing.repository.DiscountRuleRepository;
import com.hotel.booking.modules.pricing.repository.PricingRuleRepository;
import com.hotel.booking.modules.pricing.repository.SurchargeRuleRepository;
import com.hotel.booking.modules.pricing.repository.TaxCategoryRepository;
import com.hotel.booking.modules.pricing.service.PricingEngineService;
import com.hotel.booking.modules.pricing.service.TaxCalculatorService;
import com.hotel.booking.modules.search.dto.request.PricingRequest;
import com.hotel.booking.modules.search.dto.request.RoomRequest;
import com.hotel.booking.modules.search.dto.request.RoomOccupancy;
import com.hotel.booking.modules.search.dto.response.PricingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceAggregationServiceImplTest {

    @Mock
    HotelRoomTypeRepository hotelRoomTypeRepository;
    @Mock
    PricingRuleRepository pricingRuleRepository;
    @Mock
    DiscountRuleRepository discountRuleRepository;
    @Mock
    SurchargeRuleRepository surchargeRuleRepository;
    @Mock
    TaxCategoryRepository taxCategoryRepository;
    @Mock
    PricingEngineService pricingEngineService;
    @Mock
    TaxCalculatorService taxCalculatorService;

    @InjectMocks
    PriceAggregationServiceImpl priceAggregationService;

    @Test
    void calculatePrice_success() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2); // 2 nights

        RoomOccupancy occupancy = RoomOccupancy.builder()
                .adults(3) // 1 extra adult over standard 2
                .children(0)
                .infants(0)
                .build();

        RoomRequest room = RoomRequest.builder()
                .hotelRoomTypeId(10)
                .occupancy(occupancy)
                .build();

        PricingRequest request = PricingRequest.builder()
                .hotelId((short) 1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .rooms(List.of(room))
                .build();

        Hotel hotel = Hotel.builder()
                .id((short) 1)
                .serviceFeePercent(BigDecimal.valueOf(10.0)) // 10% Service Fee
                .build();

        HotelRoomType roomType = HotelRoomType.builder()
                .id(10)
                .hotel(hotel)
                .roomType(RoomType.builder().name("Deluxe").build())
                .basePrice(BigDecimal.valueOf(1000.0))
                .standardAdults(2)
                .standardChildren(0)
                .maxAdults(4)
                .maxChildren(2)
                .maxInfants(1)
                .maxTotalGuests(4)
                .build();

        PricingRule pricingRule = PricingRule.builder()
                .ruleType(PricingRuleTypeConfig.builder().code("PEAK_SEASON").build())
                .adjustmentType(AdjustmentType.FIXED)
                .adjustmentValue(BigDecimal.valueOf(100.0))
                .status(ActiveStatus.ACTIVE)
                .startDate(checkIn)
                .endDate(checkOut)
                .build();

        DiscountRule discountRule = DiscountRule.builder()
                .ruleType(DiscountRuleTypeConfig.builder().code("EARLY_BIRD").build())
                .discountType(AdjustmentType.PERCENT)
                .discountValue(BigDecimal.valueOf(10.0))
                .status(ActiveStatus.ACTIVE)
                .startDate(checkIn)
                .endDate(checkOut)
                .build();

        HotelAgePolicy agePolicy = HotelAgePolicy.builder()
                .guestType("ADULT")
                .build();

        SurchargeRule surchargeRule = SurchargeRule.builder()
                .ruleType(SurchargeRuleType.EXTRA_PERSON)
                .agePolicy(agePolicy)
                .adjustmentType(AdjustmentType.FIXED)
                .adjustmentValue(BigDecimal.valueOf(200.0))
                .status(ActiveStatus.ACTIVE)
                .startDate(checkIn)
                .endDate(checkOut)
                .build();

        TaxCategory taxCategory = TaxCategory.builder()
                .id(5)
                .categoryCode("ROOM")
                .status(ActiveStatus.ACTIVE)
                .build();

        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(10)).thenReturn(Optional.of(roomType));
        when(pricingRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(List.of(pricingRule));
        when(discountRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(List.of(discountRule));
        when(surchargeRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(List.of(surchargeRule));
        when(taxCategoryRepository.findAllByIsDeletedFalse()).thenReturn(List.of(taxCategory));

        when(pricingEngineService.resolveWinningRule(any(), any())).thenReturn(Optional.of(pricingRule));
        when(taxCalculatorService.getTaxRate(eq(5), any())).thenReturn(BigDecimal.valueOf(10.0)); // 10% VAT

        PricingResponse response = priceAggregationService.calculatePrice(request);

        // Verification of math:
        // - Base per night = 1000.0
        // - Peak Season Adjustment = +100.0 -> Adjusted price = 1100.0
        // - Early Bird Discount = 10% of 1100 = 110.0 -> Discounted price = 990.0
        // - Surcharge (1 extra adult) = 200.0
        // - Service Fee Subtotal = 990.0 + 200.0 = 1190.0
        // - Service Fee = 1190.0 * 10% = 119.0
        // - Taxable Amount = 1190.0 + 119.0 = 1309.0
        // - VAT = 1309.0 * 10% = 130.9
        // - Final Price per night = 1309.0 + 130.9 = 1439.90
        //
        // Totals for 2 nights:
        // - Total Base = 2000.0
        // - Total Adjustment = 200.0
        // - Total Discount = 220.0
        // - Total Surcharge = 400.0
        // - Total Service Fee = 238.0
        // - Total Tax = 261.80
        // - Total Final = 2879.80

        PricingResponse.PriceDetail grand = response.getGrandTotal();
        assertEquals(BigDecimal.valueOf(2000.0), grand.getBasePrice());
        assertEquals(BigDecimal.valueOf(200.0), grand.getPriceAdjustment());
        assertEquals(BigDecimal.valueOf(220.00).setScale(2), grand.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(400.0), grand.getSurchargeAmount());
        assertEquals(BigDecimal.valueOf(238.00).setScale(2), grand.getServiceFeeAmount());
        assertEquals(BigDecimal.valueOf(261.80).setScale(2), grand.getTaxAmount());
        assertEquals(BigDecimal.valueOf(2879.80).setScale(2), grand.getFinalPrice());

        assertEquals(1, response.getRooms().size());
        assertEquals(10, response.getRooms().get(0).getHotelRoomTypeId());
    }

    @Test
    void calculatePrice_capacityExceeded_throwsException() {
        RoomOccupancy occupancy = RoomOccupancy.builder()
                .adults(5) // Max total guests is 4 in roomType setup
                .children(0)
                .infants(0)
                .build();

        RoomRequest room = RoomRequest.builder()
                .hotelRoomTypeId(10)
                .occupancy(occupancy)
                .build();

        PricingRequest request = PricingRequest.builder()
                .hotelId((short) 1)
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(2))
                .rooms(List.of(room))
                .build();

        HotelRoomType roomType = HotelRoomType.builder()
                .id(10)
                .hotel(Hotel.builder().id((short) 1).build())
                .maxTotalGuests(4)
                .build();

        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(10)).thenReturn(Optional.of(roomType));

        AppException exception = assertThrows(AppException.class, () -> {
            priceAggregationService.calculatePrice(request);
        });

        assertEquals(ErrorCode.ROOM_CAPACITY_EXCEEDED, exception.getErrorCode());
    }

    @Test
    void calculatePrice_noRulesApplied_success() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1); // 1 night

        RoomOccupancy occupancy = RoomOccupancy.builder().adults(2).children(0).infants(0).build();
        RoomRequest room = RoomRequest.builder()
                .hotelRoomTypeId(10)
                .occupancy(occupancy)
                .build();

        PricingRequest request = PricingRequest.builder()
                .hotelId((short) 1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .rooms(List.of(room))
                .build();

        HotelRoomType roomType = HotelRoomType.builder()
                .id(10)
                .hotel(Hotel.builder().id((short) 1).serviceFeePercent(BigDecimal.ZERO).build())
                .basePrice(BigDecimal.valueOf(500.0))
                .standardAdults(2)
                .maxTotalGuests(4)
                .build();

        TaxCategory taxCategory = TaxCategory.builder()
                .id(5)
                .categoryCode("ROOM")
                .status(ActiveStatus.ACTIVE)
                .build();

        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(10)).thenReturn(Optional.of(roomType));
        when(pricingRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(discountRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(surchargeRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(taxCategoryRepository.findAllByIsDeletedFalse()).thenReturn(List.of(taxCategory));
        when(taxCalculatorService.getTaxRate(eq(5), any())).thenReturn(BigDecimal.ZERO);

        PricingResponse response = priceAggregationService.calculatePrice(request);

        PricingResponse.PriceDetail grand = response.getGrandTotal();
        assertEquals(BigDecimal.valueOf(500.0), grand.getBasePrice());
        assertEquals(BigDecimal.ZERO, grand.getSurchargeAmount());
        assertEquals(BigDecimal.valueOf(500.00).setScale(2), grand.getFinalPrice());
    }

    @Test
    void calculatePrice_discountRulesApplied_success() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1); // 1 night

        RoomOccupancy occupancy = RoomOccupancy.builder().adults(2).children(0).infants(0).build();
        RoomRequest room = RoomRequest.builder()
                .hotelRoomTypeId(10)
                .occupancy(occupancy)
                .build();

        PricingRequest request = PricingRequest.builder()
                .hotelId((short) 1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .rooms(List.of(room))
                .build();

        HotelRoomType roomType = HotelRoomType.builder()
                .id(10)
                .hotel(Hotel.builder().id((short) 1).serviceFeePercent(BigDecimal.ZERO).build())
                .basePrice(BigDecimal.valueOf(1000.0))
                .standardAdults(2)
                .maxTotalGuests(4)
                .build();

        DiscountRule discountRule = DiscountRule.builder()
                .ruleType(DiscountRuleTypeConfig.builder().code("PROMO").build())
                .discountType(AdjustmentType.FIXED)
                .discountValue(BigDecimal.valueOf(150.0))
                .status(ActiveStatus.ACTIVE)
                .startDate(checkIn)
                .endDate(checkOut)
                .build();

        TaxCategory taxCategory = TaxCategory.builder()
                .id(5)
                .categoryCode("ROOM")
                .status(ActiveStatus.ACTIVE)
                .build();

        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(10)).thenReturn(Optional.of(roomType));
        when(pricingRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(discountRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(List.of(discountRule));
        when(surchargeRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(taxCategoryRepository.findAllByIsDeletedFalse()).thenReturn(List.of(taxCategory));
        when(taxCalculatorService.getTaxRate(eq(5), any())).thenReturn(BigDecimal.ZERO);

        PricingResponse response = priceAggregationService.calculatePrice(request);

        PricingResponse.PriceDetail grand = response.getGrandTotal();
        assertEquals(BigDecimal.valueOf(1000.0), grand.getBasePrice());
        assertEquals(BigDecimal.valueOf(150.0), grand.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(850.00).setScale(2), grand.getFinalPrice());
    }

    @Test
    void calculatePrice_pricingRulesApplied_success() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1); // 1 night

        RoomOccupancy occupancy = RoomOccupancy.builder().adults(2).children(0).infants(0).build();
        RoomRequest room = RoomRequest.builder()
                .hotelRoomTypeId(10)
                .occupancy(occupancy)
                .build();

        PricingRequest request = PricingRequest.builder()
                .hotelId((short) 1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .rooms(List.of(room))
                .build();

        HotelRoomType roomType = HotelRoomType.builder()
                .id(10)
                .hotel(Hotel.builder().id((short) 1).serviceFeePercent(BigDecimal.ZERO).build())
                .basePrice(BigDecimal.valueOf(1000.0))
                .standardAdults(2)
                .maxTotalGuests(4)
                .build();

        PricingRule pricingRule = PricingRule.builder()
                .ruleType(PricingRuleTypeConfig.builder().code("WEEKEND").build())
                .adjustmentType(AdjustmentType.PERCENT)
                .adjustmentValue(BigDecimal.valueOf(20.0))
                .status(ActiveStatus.ACTIVE)
                .startDate(checkIn)
                .endDate(checkOut)
                .build();

        TaxCategory taxCategory = TaxCategory.builder()
                .id(5)
                .categoryCode("ROOM")
                .status(ActiveStatus.ACTIVE)
                .build();

        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(10)).thenReturn(Optional.of(roomType));
        when(pricingRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(List.of(pricingRule));
        when(discountRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(surchargeRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(taxCategoryRepository.findAllByIsDeletedFalse()).thenReturn(List.of(taxCategory));
        when(pricingEngineService.resolveWinningRule(any(), any())).thenReturn(Optional.of(pricingRule));
        when(taxCalculatorService.getTaxRate(eq(5), any())).thenReturn(BigDecimal.ZERO);

        PricingResponse response = priceAggregationService.calculatePrice(request);

        PricingResponse.PriceDetail grand = response.getGrandTotal();
        assertEquals(BigDecimal.valueOf(1000.0), grand.getBasePrice());
        assertEquals(BigDecimal.valueOf(200.00).setScale(2), grand.getPriceAdjustment());
        assertEquals(BigDecimal.valueOf(1200.00).setScale(2), grand.getFinalPrice());
    }

    @Test
    void calculatePrice_surchargeRulesApplied_success() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1); // 1 night

        RoomOccupancy occupancy = RoomOccupancy.builder().adults(3).children(0).infants(0).build();
        RoomRequest room = RoomRequest.builder()
                .hotelRoomTypeId(10)
                .occupancy(occupancy)
                .build();

        PricingRequest request = PricingRequest.builder()
                .hotelId((short) 1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .rooms(List.of(room))
                .build();

        HotelRoomType roomType = HotelRoomType.builder()
                .id(10)
                .hotel(Hotel.builder().id((short) 1).serviceFeePercent(BigDecimal.ZERO).build())
                .basePrice(BigDecimal.valueOf(1000.0))
                .standardAdults(2)
                .maxTotalGuests(4)
                .build();

        TaxCategory taxCategory = TaxCategory.builder()
                .id(5)
                .categoryCode("ROOM")
                .status(ActiveStatus.ACTIVE)
                .build();

        HotelAgePolicy agePolicy = HotelAgePolicy.builder()
                .guestType("ADULT")
                .build();

        SurchargeRule surchargeRule = SurchargeRule.builder()
                .ruleType(SurchargeRuleType.EXTRA_PERSON)
                .agePolicy(agePolicy)
                .adjustmentType(AdjustmentType.FIXED)
                .adjustmentValue(BigDecimal.valueOf(250.0))
                .status(ActiveStatus.ACTIVE)
                .startDate(checkIn)
                .endDate(checkOut)
                .build();

        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(10)).thenReturn(Optional.of(roomType));
        when(pricingRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(discountRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(surchargeRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(List.of(surchargeRule));
        when(taxCategoryRepository.findAllByIsDeletedFalse()).thenReturn(List.of(taxCategory));
        when(taxCalculatorService.getTaxRate(eq(5), any())).thenReturn(BigDecimal.ZERO);

        PricingResponse response = priceAggregationService.calculatePrice(request);

        PricingResponse.PriceDetail grand = response.getGrandTotal();
        assertEquals(BigDecimal.valueOf(1000.0), grand.getBasePrice());
        assertEquals(BigDecimal.valueOf(250.0), grand.getSurchargeAmount());
        assertEquals(BigDecimal.valueOf(1250.00).setScale(2), grand.getFinalPrice());
    }

    @Test
    void calculatePrice_roomTypeNotFound_throwsSpecificException() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);

        RoomOccupancy room = RoomOccupancy.builder().adults(2).children(0).infants(0).build();
        RoomRequest roomReq = RoomRequest.builder()
                .hotelRoomTypeId(99)
                .occupancy(room)
                .build();

        PricingRequest request = PricingRequest.builder()
                .hotelId((short) 1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .rooms(List.of(roomReq))
                .build();

        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(99)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            priceAggregationService.calculatePrice(request);
        });

        assertEquals(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void calculatePrice_multiRoomOccupancy_returnsDistinctRoomPricingResultWithTotals() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1); // 1 night

        RoomOccupancy room1 = RoomOccupancy.builder().adults(3).children(0).infants(0).build(); // 1 extra (surcharge)
        RoomOccupancy room2 = RoomOccupancy.builder().adults(2).children(0).infants(0).build(); // no extra

        RoomRequest req1 = RoomRequest.builder().hotelRoomTypeId(10).occupancy(room1).build();
        RoomRequest req2 = RoomRequest.builder().hotelRoomTypeId(10).occupancy(room2).build();

        PricingRequest request = PricingRequest.builder()
                .hotelId((short) 1)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .rooms(List.of(req1, req2))
                .build();

        Hotel hotel = Hotel.builder()
                .id((short) 1)
                .serviceFeePercent(BigDecimal.ZERO)
                .build();

        HotelRoomType roomType = HotelRoomType.builder()
                .id(10)
                .hotel(hotel)
                .roomType(RoomType.builder().name("Deluxe").build())
                .basePrice(BigDecimal.valueOf(1000.0))
                .standardAdults(2)
                .standardChildren(0)
                .maxAdults(4)
                .maxChildren(2)
                .maxInfants(1)
                .maxTotalGuests(4)
                .build();

        TaxCategory taxCategory = TaxCategory.builder()
                .id(5)
                .categoryCode("ROOM")
                .status(ActiveStatus.ACTIVE)
                .build();

        HotelAgePolicy agePolicy = HotelAgePolicy.builder()
                .guestType("ADULT")
                .build();

        SurchargeRule rule = SurchargeRule.builder()
                .ruleType(SurchargeRuleType.EXTRA_PERSON)
                .agePolicy(agePolicy)
                .adjustmentType(AdjustmentType.FIXED)
                .adjustmentValue(BigDecimal.valueOf(200.0))
                .status(ActiveStatus.ACTIVE)
                .startDate(checkIn)
                .endDate(checkOut)
                .build();

        when(hotelRoomTypeRepository.findByIdAndIsDeletedFalse(10)).thenReturn(Optional.of(roomType));
        when(pricingRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(discountRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(Collections.emptyList());
        when(surchargeRuleRepository.findAllByHotelRoomTypeIdAndIsDeletedFalse(10)).thenReturn(List.of(rule));
        when(taxCategoryRepository.findAllByIsDeletedFalse()).thenReturn(List.of(taxCategory));
        when(pricingEngineService.resolveWinningRule(any(), any())).thenReturn(Optional.empty());
        when(taxCalculatorService.getTaxRate(eq(5), any())).thenReturn(BigDecimal.ZERO);

        PricingResponse response = priceAggregationService.calculatePrice(request);

        // Verify grandTotal
        PricingResponse.PriceDetail grand = response.getGrandTotal();
        assertEquals(BigDecimal.valueOf(2000.0), grand.getBasePrice());
        assertEquals(BigDecimal.valueOf(200.0), grand.getSurchargeAmount());
        assertEquals(BigDecimal.valueOf(2200.00).setScale(2), grand.getFinalPrice());

        // Verify rooms list size
        assertEquals(2, response.getRooms().size());

        // Room 1 (index 0) - has surcharge
        PricingResponse.RoomPricingResult result1 = response.getRooms().get(0);
        assertEquals(10, result1.getHotelRoomTypeId());
        assertEquals(BigDecimal.valueOf(1000.0), result1.getPriceDetail().getBasePrice());
        assertEquals(BigDecimal.valueOf(200.0), result1.getPriceDetail().getSurchargeAmount());
        assertEquals(BigDecimal.valueOf(1200.00).setScale(2), result1.getPriceDetail().getFinalPrice());

        // Room 2 (index 1) - no surcharge
        PricingResponse.RoomPricingResult result2 = response.getRooms().get(1);
        assertEquals(10, result2.getHotelRoomTypeId());
        assertEquals(BigDecimal.valueOf(1000.0), result2.getPriceDetail().getBasePrice());
        assertEquals(BigDecimal.ZERO, result2.getPriceDetail().getSurchargeAmount());
        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), result2.getPriceDetail().getFinalPrice());
    }
}
