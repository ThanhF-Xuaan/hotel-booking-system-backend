package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.HolidayCalendarCreationRequest;
import com.hotel.booking.modules.pricing.dto.request.HolidayCalendarUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.HolidayCalendarResponse;
import com.hotel.booking.modules.pricing.entity.HolidayCalendar;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HolidayCalendarMapper {

    HolidayCalendar toEntity(HolidayCalendarCreationRequest request);

    HolidayCalendarResponse toResponse(HolidayCalendar holidayCalendar);

    void updateEntity(HolidayCalendarUpdateRequest request, @MappingTarget HolidayCalendar holidayCalendar);
}
