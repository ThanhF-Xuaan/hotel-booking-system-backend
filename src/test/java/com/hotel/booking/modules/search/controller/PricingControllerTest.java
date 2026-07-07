package com.hotel.booking.modules.search.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.search.dto.request.PricingRequest;
import com.hotel.booking.modules.search.dto.response.PricingResponse;
import com.hotel.booking.modules.search.service.PriceAggregationService;
import com.hotel.booking.modules.search.dto.request.RoomRequest;
import com.hotel.booking.modules.search.dto.request.RoomOccupancy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingControllerTest {

    @Mock
    PriceAggregationService priceAggregationService;

    @InjectMocks
    PricingController pricingController;

    @Test
    void calculatePrice_invalidDateRange_throwsException() {
        RoomOccupancy occupancy = RoomOccupancy.builder()
                .adults(2)
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
                .checkOut(LocalDate.now().minusDays(1))
                .rooms(java.util.List.of(room))
                .build();

        when(priceAggregationService.calculatePrice(request))
                .thenThrow(new AppException(ErrorCode.INVALID_DATE_RANGE));

        AppException exception = assertThrows(AppException.class, () -> {
            pricingController.calculatePrice(request);
        });

        assertEquals(ErrorCode.INVALID_DATE_RANGE, exception.getErrorCode());
        verify(priceAggregationService, times(1)).calculatePrice(request);
    }

    @Test
    void calculatePrice_success() {
        RoomOccupancy occupancy = RoomOccupancy.builder()
                .adults(2)
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
                .rooms(java.util.List.of(room))
                .build();

        PricingResponse.PriceDetail mockPriceDetail = PricingResponse.PriceDetail.builder()
                .basePrice(BigDecimal.valueOf(1000.0))
                .priceAdjustment(BigDecimal.ZERO)
                .surchargeAmount(BigDecimal.valueOf(100.0))
                .discountAmount(BigDecimal.ZERO)
                .serviceFeeAmount(BigDecimal.valueOf(110.0))
                .taxAmount(BigDecimal.valueOf(121.0))
                .finalPrice(BigDecimal.valueOf(1331.0))
                .build();

        PricingResponse.RoomPricingResult mockRoomResult = PricingResponse.RoomPricingResult.builder()
                .hotelRoomTypeId(10)
                .occupancy(occupancy)
                .priceDetail(mockPriceDetail)
                .build();

        PricingResponse mockResponse = PricingResponse.builder()
                .rooms(java.util.List.of(mockRoomResult))
                .grandTotal(mockPriceDetail)
                .build();

        when(priceAggregationService.calculatePrice(request)).thenReturn(mockResponse);

        ApiResponse<PricingResponse> apiResponse = pricingController.calculatePrice(request);

        PricingResponse response = apiResponse.getResult();
        assertEquals(1, response.getRooms().size());
        assertEquals(10, response.getRooms().get(0).getHotelRoomTypeId());
        assertEquals(BigDecimal.valueOf(1000.0), response.getGrandTotal().getBasePrice());
        assertEquals(BigDecimal.ZERO, response.getGrandTotal().getPriceAdjustment());
        assertEquals(BigDecimal.valueOf(100.0), response.getGrandTotal().getSurchargeAmount());
        assertEquals(BigDecimal.valueOf(110.0), response.getGrandTotal().getServiceFeeAmount());
        assertEquals(BigDecimal.valueOf(121.0), response.getGrandTotal().getTaxAmount());
        assertEquals(BigDecimal.valueOf(1331.0), response.getGrandTotal().getFinalPrice());
        verify(priceAggregationService, times(1)).calculatePrice(request);
    }
}
