package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.Payment;
import com.hotel.booking.modules.booking.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p WHERE p.booking.id = :bookingId AND p.status = 'COMPLETED'")
    BigDecimal sumPaidAmountByBookingId(@Param("bookingId") Long bookingId);

    List<Payment> findByBookingIdAndStatus(Long bookingId, PaymentStatus paymentStatus);
}
