package com.hotel.booking.modules.report.service;

import com.hotel.booking.modules.report.dto.OccupancyDTO;

import java.time.LocalDate;
import java.util.List;

public interface OccupancyReportService {
    List<OccupancyDTO> getRealTimeOccupancy(Integer hotelId, Integer hotelRoomTypeId, LocalDate startDate, LocalDate endDate);
}
