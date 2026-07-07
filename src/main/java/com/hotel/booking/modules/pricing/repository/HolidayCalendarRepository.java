package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.HolidayCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayCalendarRepository extends JpaRepository<HolidayCalendar, Integer> {
    List<HolidayCalendar> findAllByIsDeletedFalse();
    Optional<HolidayCalendar> findByIdAndIsDeletedFalse(Integer id);
    boolean existsByNameAndIsDeletedFalse(String name);
    boolean existsByNameAndIdNotAndIsDeletedFalse(String name, Integer id);
}
