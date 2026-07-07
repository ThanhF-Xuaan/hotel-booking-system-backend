package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.BookingRoom;
import com.hotel.booking.modules.booking.entity.BookingRoomId;
import com.hotel.booking.modules.inventory.entity.RoomInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoom, BookingRoomId> {
    long countByBookingDetailId(Long bookingDetailId);

    @Query("SELECT br.roomInstance FROM BookingRoom br WHERE br.bookingDetail.id = :bookingDetailId")
    List<RoomInstance> findRoomInstancesByBookingDetailId(Long bookingDetailId);

    void deleteByBookingDetailId(Long bookingDetailId);
}
