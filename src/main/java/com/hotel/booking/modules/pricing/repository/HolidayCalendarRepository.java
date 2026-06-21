package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.HolidayCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HolidayCalendarRepository extends JpaRepository<HolidayCalendar, Integer> {
    List<HolidayCalendar> findAllByIsDeletedFalse();
}
