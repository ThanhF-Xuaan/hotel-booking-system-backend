package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import com.hotel.booking.modules.inventory.entity.RoomInstance;
import com.hotel.booking.modules.inventory.entity.RoomSlot;
import com.hotel.booking.modules.inventory.enums.RoomInstanceStatus;
import com.hotel.booking.modules.inventory.enums.RoomSlotStatus;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import com.hotel.booking.modules.inventory.repository.RoomInstanceRepository;
import com.hotel.booking.modules.inventory.repository.RoomSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomMaintenanceServiceImplTest {

    @Mock
    RoomInstanceRepository roomInstanceRepository;

    @Mock
    RoomSlotRepository roomSlotRepository;

    @Mock
    RoomAvailabilityRepository roomAvailabilityRepository;

    @InjectMocks
    RoomMaintenanceServiceImpl roomMaintenanceService;

    @Test
    void scheduleMaintenance_roomInstanceNotFound_throwsException() {
        when(roomInstanceRepository.findByIdAndIsDeletedFalse(99)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            roomMaintenanceService.scheduleMaintenance(99, LocalDate.now(), LocalDate.now().plusDays(2));
        });

        assertEquals(ErrorCode.ROOM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void scheduleMaintenance_hasBookingConflict_throwsException() {
        RoomInstance roomInstance = new RoomInstance();
        when(roomInstanceRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomInstance));
        when(roomSlotRepository.existsByRoomInstanceIdAndSlotDateBetweenAndStatusIn(
                eq(1), any(LocalDate.class), any(LocalDate.class), any()
        )).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            roomMaintenanceService.scheduleMaintenance(1, LocalDate.now(), LocalDate.now().plusDays(2));
        });

        assertEquals(ErrorCode.ROOM_HAS_BOOKING_CONFLICT, exception.getErrorCode());
    }

    @Test
    void scheduleMaintenance_success() {
        HotelRoomType roomType = HotelRoomType.builder().id(10).build();
        RoomInstance roomInstance = RoomInstance.builder()
                .id(1)
                .hotelRoomType(roomType)
                .currentStatus(RoomInstanceStatus.READY)
                .build();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(2);

        when(roomInstanceRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(roomInstance));
        when(roomSlotRepository.existsByRoomInstanceIdAndSlotDateBetweenAndStatusIn(
                eq(1), eq(startDate), eq(endDate), any()
        )).thenReturn(false);

        RoomAvailability availability = RoomAvailability.builder()
                .id(100L)
                .totalRooms(5)
                .build();

        when(roomAvailabilityRepository.findByHotelRoomTypeIdAndDate(eq(10), any(LocalDate.class)))
                .thenReturn(Optional.of(availability));

        roomMaintenanceService.scheduleMaintenance(1, startDate, endDate);

        verify(roomSlotRepository, times(3)).save(any(RoomSlot.class));
        verify(roomAvailabilityRepository, times(3)).save(any(RoomAvailability.class));
        verify(roomInstanceRepository, times(1)).save(roomInstance);
        assertEquals(RoomInstanceStatus.MAINTENANCE, roomInstance.getCurrentStatus());
    }
}
