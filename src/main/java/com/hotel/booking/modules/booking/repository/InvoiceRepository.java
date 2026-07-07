package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.Invoice;
import com.hotel.booking.modules.booking.enums.InvoiceStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByBookingIdAndStatus(Long bookingId, InvoiceStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Invoice i WHERE i.booking.id = :bookingId AND i.status = :status")
    Optional<Invoice> findByBookingIdAndStatusForUpdate(@Param("bookingId") Long bookingId,
                                                        @Param("status") InvoiceStatus status);

    Optional<Invoice> findTopByBookingIdOrderByCreatedAtDesc(Long bookingId);
}
