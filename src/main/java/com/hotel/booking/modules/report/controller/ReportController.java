package com.hotel.booking.modules.report.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.report.dto.OccupancyDTO;
import com.hotel.booking.modules.report.service.OccupancyReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
    OccupancyReportService occupancyReportService;

    @GetMapping("/occupancy")
    public ApiResponse<List<OccupancyDTO>> getOccupancy(
            @RequestParam Integer hotelId,
            @RequestParam(required = false) Integer hotelRoomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ApiResponse.<List<OccupancyDTO>>builder()
                .result(occupancyReportService.getRealTimeOccupancy(hotelId, hotelRoomTypeId, startDate, endDate))
                .build();
    }
}
