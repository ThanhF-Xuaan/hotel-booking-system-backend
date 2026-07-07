package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.pricing.dto.request.HolidayCalendarCreationRequest;
import com.hotel.booking.modules.pricing.dto.request.HolidayCalendarUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.HolidayCalendarResponse;
import com.hotel.booking.modules.pricing.entity.HolidayCalendar;
import com.hotel.booking.modules.pricing.mapper.HolidayCalendarMapper;
import com.hotel.booking.modules.pricing.repository.HolidayCalendarRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class HolidayCalendarServiceImpl implements HolidayCalendarService {

    HolidayCalendarRepository holidayCalendarRepository;
    HolidayCalendarMapper holidayCalendarMapper;

    @Override
    @Transactional
    public HolidayCalendarResponse createHoliday(HolidayCalendarCreationRequest request) {
        log.info("Creating new holiday config for date: {}", request.getDate());

        if (holidayCalendarRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new AppException(ErrorCode.HOLIDAY_NAME_EXISTED);
        }

        HolidayCalendar holidayCalendar = holidayCalendarMapper.toEntity(request);
        
        if (holidayCalendar.getStatus() == null) {
            holidayCalendar.setStatus(ActiveStatus.ACTIVE);
        }
        holidayCalendar.setIsDeleted(false);

        HolidayCalendar savedHoliday = holidayCalendarRepository.save(holidayCalendar);
        log.info("Holiday created successfully with id: {}", savedHoliday.getId());
        return holidayCalendarMapper.toResponse(savedHoliday);
    }

    @Override
    public List<HolidayCalendarResponse> getAllHolidays() {
        log.info("Fetching all active holiday calendar records");
        return holidayCalendarRepository.findAllByIsDeletedFalse()
                .stream()
                .map(holidayCalendarMapper::toResponse)
                .toList();
    }

    @Override
    public HolidayCalendarResponse getHolidayById(Integer id) {
        log.info("Fetching holiday config with id: {}", id);
        HolidayCalendar holidayCalendar = holidayCalendarRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOLIDAY_NOT_FOUND));
        return holidayCalendarMapper.toResponse(holidayCalendar);
    }

    @Override
    @Transactional
    public HolidayCalendarResponse updateHoliday(Integer id, HolidayCalendarUpdateRequest request) {
        log.info("Updating holiday config with id: {}", id);

        HolidayCalendar holidayCalendar = holidayCalendarRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOLIDAY_NOT_FOUND));

        if (holidayCalendarRepository.existsByNameAndIdNotAndIsDeletedFalse(request.getName(), id)) {
            throw new AppException(ErrorCode.HOLIDAY_NAME_EXISTED);
        }

        holidayCalendarMapper.updateEntity(request, holidayCalendar);
        HolidayCalendar savedHoliday = holidayCalendarRepository.save(holidayCalendar);

        log.info("Holiday updated successfully with id: {}", savedHoliday.getId());
        return holidayCalendarMapper.toResponse(savedHoliday);
    }

    @Override
    @Transactional
    public void deleteHoliday(Integer id) {
        log.info("Soft-deleting holiday config with id: {}", id);

        HolidayCalendar holidayCalendar = holidayCalendarRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOLIDAY_NOT_FOUND));

        holidayCalendar.setIsDeleted(true);
        holidayCalendarRepository.save(holidayCalendar);

        log.info("Holiday soft-deleted successfully with id: {}", id);
    }
}
