package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyUpdateRequest;
import com.hotel.booking.modules.pricing.entity.HotelAgePolicy;
import com.hotel.booking.modules.pricing.mapper.HotelAgePolicyMapper;
import com.hotel.booking.modules.pricing.repository.HotelAgePolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelAgePolicyServiceImplTest {

    @Mock
    HotelAgePolicyRepository hotelAgePolicyRepository;

    @Mock
    HotelRepository hotelRepository;

    @Mock
    HotelAgePolicyMapper hotelAgePolicyMapper;

    @InjectMocks
    HotelAgePolicyServiceImpl hotelAgePolicyService;

    @Test
    void createPolicy_invalidAgeRange_throwsException() {
        HotelAgePolicyCreateRequest request = HotelAgePolicyCreateRequest.builder()
                .hotelId((short) 1)
                .guestType("CHILD")
                .minAge((short) 12)
                .maxAge((short) 6) // minAge > maxAge
                .build();

        Hotel hotel = new Hotel();
        when(hotelRepository.findByIdAndIsDeletedFalse((short) 1)).thenReturn(Optional.of(hotel));

        AppException exception = assertThrows(AppException.class, () -> {
            hotelAgePolicyService.createPolicy(request);
        });

        assertEquals(ErrorCode.AGE_POLICY_INVALID_AGE_RANGE, exception.getErrorCode());
    }

    @Test
    void createPolicy_hotelNotFound_throwsException() {
        HotelAgePolicyCreateRequest request = HotelAgePolicyCreateRequest.builder()
                .hotelId((short) 99)
                .guestType("CHILD")
                .minAge((short) 6)
                .maxAge((short) 11)
                .build();

        when(hotelRepository.findByIdAndIsDeletedFalse((short) 99)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            hotelAgePolicyService.createPolicy(request);
        });

        assertEquals(ErrorCode.HOTEL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createPolicy_duplicateGuestType_throwsException() {
        HotelAgePolicyCreateRequest request = HotelAgePolicyCreateRequest.builder()
                .hotelId((short) 1)
                .guestType("CHILD")
                .minAge((short) 6)
                .maxAge((short) 11)
                .build();

        Hotel hotel = new Hotel();
        when(hotelRepository.findByIdAndIsDeletedFalse((short) 1)).thenReturn(Optional.of(hotel));
        when(hotelAgePolicyRepository.existsByHotelIdAndGuestTypeAndIsDeletedFalse((short) 1, "CHILD")).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            hotelAgePolicyService.createPolicy(request);
        });

        assertEquals(ErrorCode.AGE_POLICY_DUPLICATE_GUEST_TYPE, exception.getErrorCode());
    }

    @Test
    void updatePolicy_notFound_throwsException() {
        HotelAgePolicyUpdateRequest request = HotelAgePolicyUpdateRequest.builder()
                .hotelId((short) 1)
                .guestType("CHILD")
                .minAge((short) 6)
                .maxAge((short) 11)
                .build();

        when(hotelAgePolicyRepository.findByIdAndIsDeletedFalse((short) 99)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            hotelAgePolicyService.updatePolicy((short) 99, request);
        });

        assertEquals(ErrorCode.AGE_POLICY_NOT_FOUND, exception.getErrorCode());
    }
}
