package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.BookingRoom;
import com.hotel.booking.modules.booking.entity.BookingRoomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoom, BookingRoomId> {
    long countByBookingDetailId(Long bookingDetailId);
}
