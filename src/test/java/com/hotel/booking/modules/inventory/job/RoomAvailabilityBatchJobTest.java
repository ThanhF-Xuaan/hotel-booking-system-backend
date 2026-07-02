package com.hotel.booking.modules.inventory.job;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomAvailabilityBatchJobTest {

    @Mock
    HotelRoomTypeRepository hotelRoomTypeRepository;

    @Mock
    RoomAvailabilityRepository roomAvailabilityRepository;

    @InjectMocks
    RoomAvailabilityBatchJob roomAvailabilityBatchJob;

    @Test
    void preGenerateRoomAvailability_success() {
        HotelRoomType roomType = HotelRoomType.builder()
                .id(1)
                .totalQuantity(10)
                .status(ActiveStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(hotelRoomTypeRepository.findAllByIsDeletedFalseAndStatus(ActiveStatus.ACTIVE))
                .thenReturn(List.of(roomType));

        roomAvailabilityBatchJob.preGenerateRoomAvailability();

        verify(hotelRoomTypeRepository, times(1)).findAllByIsDeletedFalseAndStatus(ActiveStatus.ACTIVE);
        verify(roomAvailabilityRepository, times(181)).insertIgnoreConflict(eq(1), any(LocalDate.class), eq(10));
    }
}
