package com.hotel.booking.modules.operation.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.operation.dto.response.TimelineResponse;
import com.hotel.booking.modules.operation.service.TimelineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/operation")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimelineController {

    TimelineService timelineService;

    @GetMapping("/timeline")
    public ApiResponse<TimelineResponse> getTimeline(
            @RequestParam Integer hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ApiResponse.<TimelineResponse>builder()
                .result(timelineService.getTimelineMatrix(hotelId, startDate, endDate))
                .build();
    }
}