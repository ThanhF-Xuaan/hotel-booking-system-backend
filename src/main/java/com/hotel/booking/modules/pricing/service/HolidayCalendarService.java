package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.HolidayCalendarCreationRequest;
import com.hotel.booking.modules.pricing.dto.request.HolidayCalendarUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.HolidayCalendarResponse;

import java.util.List;

public interface HolidayCalendarService {
    HolidayCalendarResponse createHoliday(HolidayCalendarCreationRequest request);
    List<HolidayCalendarResponse> getAllHolidays();
    HolidayCalendarResponse getHolidayById(Integer id);
    HolidayCalendarResponse updateHoliday(Integer id, HolidayCalendarUpdateRequest request);
    void deleteHoliday(Integer id);
}
