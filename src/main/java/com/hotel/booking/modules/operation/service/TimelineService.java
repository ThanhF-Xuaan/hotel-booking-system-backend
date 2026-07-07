package com.hotel.booking.modules.operation.service;

import com.hotel.booking.modules.operation.dto.response.TimelineResponse;

import java.time.LocalDate;

public interface TimelineService {
    TimelineResponse getTimelineMatrix(Integer hotelId, LocalDate startDate, LocalDate endDate);
}
