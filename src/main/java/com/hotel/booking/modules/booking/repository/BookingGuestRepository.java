package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.BookingGuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingGuestRepository extends JpaRepository<BookingGuest, Long> {
}
