package com.hotel.booking.modules.report.service;

import com.hotel.booking.modules.report.dto.OccupancyDTO;
import com.hotel.booking.modules.report.repository.OccupancyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OccupancyReportServiceImplTest {

    @Mock
    OccupancyRepository occupancyRepository;

    @InjectMocks
    OccupancyReportServiceImpl occupancyReportService;

    @Test
    void getRealTimeOccupancy_withoutRoomTypeId_callsGetDailyOccupancy() {
        Integer hotelId = 1;
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 2);

        Object[] row1 = new Object[]{LocalDate.of(2026, 7, 1), 75.0};
        Object[] row2 = new Object[]{LocalDate.of(2026, 7, 2), 80.0};
        List<Object[]> mockResult = List.of(row1, row2);

        when(occupancyRepository.getDailyOccupancy(hotelId, startDate, endDate)).thenReturn(mockResult);

        List<OccupancyDTO> results = occupancyReportService.getRealTimeOccupancy(hotelId, null, startDate, endDate);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(LocalDate.of(2026, 7, 1), results.get(0).getDate());
        assertEquals(75.0, results.get(0).getOccupancyRate());
        assertEquals(LocalDate.of(2026, 7, 2), results.get(1).getDate());
        assertEquals(80.0, results.get(1).getOccupancyRate());

        verify(occupancyRepository, times(1)).getDailyOccupancy(hotelId, startDate, endDate);
        verify(occupancyRepository, never()).getDailyOccupancyByRoomType(anyInt(), any(), any());
    }

    @Test
    void getRealTimeOccupancy_withRoomTypeId_callsGetDailyOccupancyByRoomType() {
        Integer hotelId = 1;
        Integer roomTypeId = 10;
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 2);

        Object[] row1 = new Object[]{LocalDate.of(2026, 7, 1), 60.0};
        Object[] row2 = new Object[]{LocalDate.of(2026, 7, 2), 65.0};
        List<Object[]> mockResult = List.of(row1, row2);

        when(occupancyRepository.getDailyOccupancyByRoomType(roomTypeId, startDate, endDate)).thenReturn(mockResult);

        List<OccupancyDTO> results = occupancyReportService.getRealTimeOccupancy(hotelId, roomTypeId, startDate, endDate);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(LocalDate.of(2026, 7, 1), results.get(0).getDate());
        assertEquals(60.0, results.get(0).getOccupancyRate());
        assertEquals(LocalDate.of(2026, 7, 2), results.get(1).getDate());
        assertEquals(65.0, results.get(1).getOccupancyRate());

        verify(occupancyRepository, times(1)).getDailyOccupancyByRoomType(roomTypeId, startDate, endDate);
        verify(occupancyRepository, never()).getDailyOccupancy(anyInt(), any(), any());
    }
}
