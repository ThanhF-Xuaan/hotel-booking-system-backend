package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.Booking;
import com.hotel.booking.modules.booking.enums.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    @EntityGraph(attributePaths = {"guest", "hotel", "bookingDetails"})
    Optional<Booking> findById(Long id);

    @EntityGraph(attributePaths = {"guest", "hotel", "bookingDetails"})
    Optional<Booking> findByBookingNumber(String bookingNumber);

    @Query("SELECT DISTINCT b FROM Booking b JOIN b.bookingDetails bd " +
            "WHERE b.status = :status AND bd.checkOutDate <= :targetDate")
    List<Booking> findBookingsForNoShow(
            @Param("status") BookingStatus status,
            @Param("targetDate") LocalDate targetDate
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);
}
