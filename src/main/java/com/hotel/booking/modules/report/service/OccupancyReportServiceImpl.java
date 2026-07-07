package com.hotel.booking.modules.report.service;

import com.hotel.booking.modules.report.dto.OccupancyDTO;
import com.hotel.booking.modules.report.repository.OccupancyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OccupancyReportServiceImpl implements OccupancyReportService{
    OccupancyRepository occupancyRepository;

    public List<OccupancyDTO> getRealTimeOccupancy(Integer hotelId, Integer hotelRoomTypeId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = hotelRoomTypeId != null
                ? occupancyRepository.getDailyOccupancyByRoomType(hotelRoomTypeId, startDate, endDate)
                : occupancyRepository.getDailyOccupancy(hotelId, startDate, endDate);

        return results.stream().map(obj -> OccupancyDTO.builder()
                        .date((LocalDate) obj[0])
                        // FIX Ở ĐÂY: Dùng Number để tránh lỗi ClassCastException
                        .occupancyRate(obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }
}
