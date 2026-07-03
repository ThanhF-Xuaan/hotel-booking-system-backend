package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    @EntityGraph(attributePaths = {"guest", "hotel", "bookingDetails"})
    Optional<Booking> findById(Long id);

    @EntityGraph(attributePaths = {"guest", "hotel", "bookingDetails"})
    Optional<Booking> findByBookingNumber(String bookingNumber);
}
