package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    @Modifying
    @Query(value = "INSERT INTO room_availability (hotel_room_type_id, date, total_rooms, booked_rooms, locked_rooms, version) " +
                   "VALUES (:hotelRoomTypeId, :date, :totalRooms, 0, 0, 0) " +
                   "ON CONFLICT (hotel_room_type_id, date) DO NOTHING", 
           nativeQuery = true)
    void insertIgnoreConflict(
            @Param("hotelRoomTypeId") Integer hotelRoomTypeId,
            @Param("date") LocalDate date,
            @Param("totalRooms") Integer totalRooms
    );

    Optional<RoomAvailability> findByHotelRoomTypeIdAndDate(Integer hotelRoomTypeId, LocalDate date);

    @Query("""
        SELECT ra.hotelRoomType, MIN(ra.availableCount)
        FROM RoomAvailability ra
        WHERE ra.hotelRoomType.hotel.id = :hotelId
          AND ra.hotelRoomType.isDeleted = false
          AND ra.hotelRoomType.status = :status
          AND ra.date >= :checkIn
          AND ra.date < :checkOut
          AND ra.availableCount >= :roomCount
        GROUP BY ra.hotelRoomType
        HAVING COUNT(ra.date) = :numberOfNights
    """)
    List<Object[]> findAvailableRoomTypes(
            @Param("hotelId") Integer hotelId,
            @Param("status") ActiveStatus status,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("roomCount") Integer roomCount,
            @Param("numberOfNights") Long numberOfNights
    );
}
