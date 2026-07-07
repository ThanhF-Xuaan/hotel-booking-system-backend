package com.hotel.booking.modules.search.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.RoomType;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import com.hotel.booking.modules.search.dto.request.AvailabilitySearchRequest;
import com.hotel.booking.modules.search.dto.response.AvailableRoomTypeResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventorySearchServiceImplTest {

    @Mock
    RoomAvailabilityRepository roomAvailabilityRepository;

    @InjectMocks
    InventorySearchServiceImpl inventorySearchService;

    @Test
    void searchAvailableRooms_invalidDateRange_throwsException() {
        AvailabilitySearchRequest request = AvailabilitySearchRequest.builder()
                .hotelId(1)
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().minusDays(1))
                .roomCount(1)
                .build();

        AppException exception = assertThrows(AppException.class, () -> {
            inventorySearchService.searchAvailableRooms(request);
        });

        assertEquals(ErrorCode.SURCHARGE_RULE_INVALID_DATE_RANGE, exception.getErrorCode());
    }

    @Test
    void searchAvailableRooms_success() {
        AvailabilitySearchRequest request = AvailabilitySearchRequest.builder()
                .hotelId(1)
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(3))
                .roomCount(2)
                .build();

        RoomType roomTypeEntity = RoomType.builder().name("Deluxe").build();
        HotelRoomType hotelRoomType = HotelRoomType.builder()
                .id(10)
                .roomType(roomTypeEntity)
                .maxAdults(2)
                .maxChildren(1)
                .build();

        Object[] queryResultRow = new Object[]{hotelRoomType, 4};
        List<Object[]> queryResults = List.of(new Object[][]{queryResultRow});

        when(roomAvailabilityRepository.findAvailableRoomTypes(
                eq(1), eq(ActiveStatus.ACTIVE), eq(request.getCheckIn()), eq(request.getCheckOut()), eq(2), eq(3L)
        )).thenReturn(queryResults);

        List<AvailableRoomTypeResponse> responses = inventorySearchService.searchAvailableRooms(request);

        assertEquals(1, responses.size());
        AvailableRoomTypeResponse response = responses.get(0);
        assertEquals(10, response.getHotelRoomTypeId());
        assertEquals("Deluxe", response.getRoomTypeName());
        assertEquals(2, response.getMaxAdults());
        assertEquals(1, response.getMaxChildren());
        assertEquals(4, response.getMinAvailableCount());
    }
}
