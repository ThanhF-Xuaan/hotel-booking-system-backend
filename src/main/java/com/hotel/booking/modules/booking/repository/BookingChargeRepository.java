package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.BookingCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingChargeRepository extends JpaRepository<BookingCharge, Long> {
}
