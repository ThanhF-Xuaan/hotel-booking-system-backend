package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.pricing.dto.request.CampaignCreateRequest;
import com.hotel.booking.modules.pricing.repository.CampaignRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceImplTest {

    @Mock
    CampaignRepository campaignRepository;

    @Mock
    HotelRepository hotelRepository;

    @InjectMocks
    CampaignServiceImpl campaignService;

    @Test
    void createCampaign_invalidDateRange_throwsCampaignInvalidDateRange() {
        CampaignCreateRequest request = CampaignCreateRequest.builder()
                .hotelId((short) 1)
                .name("Summer Sale")
                .startDate(LocalDate.of(2026, 7, 15))
                .endDate(LocalDate.of(2026, 7, 10)) // end date before start date
                .build();

        Hotel hotel = new Hotel();
        when(hotelRepository.findByIdAndIsDeletedFalse((short) 1)).thenReturn(Optional.of(hotel));

        AppException exception = assertThrows(AppException.class, () -> {
            campaignService.createCampaign(request);
        });

        assertEquals(ErrorCode.CAMPAIGN_INVALID_DATE_RANGE, exception.getErrorCode());
    }

    @Test
    void createCampaign_overlappingName_throwsCampaignNameOverlapping() {
        CampaignCreateRequest request = CampaignCreateRequest.builder()
                .hotelId((short) 1)
                .name("Summer Sale")
                .startDate(LocalDate.of(2026, 7, 10))
                .endDate(LocalDate.of(2026, 7, 15))
                .build();

        Hotel hotel = new Hotel();
        when(hotelRepository.findByIdAndIsDeletedFalse((short) 1)).thenReturn(Optional.of(hotel));
        when(campaignRepository.existsOverlappingName(eq((short) 1), eq("Summer Sale"), eq(LocalDate.of(2026, 7, 10)), eq(LocalDate.of(2026, 7, 15)), any()))
                .thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            campaignService.createCampaign(request);
        });

        assertEquals(ErrorCode.CAMPAIGN_NAME_OVERLAPPING, exception.getErrorCode());
    }
}
