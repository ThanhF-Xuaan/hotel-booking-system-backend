package com.hotel.booking.modules.report.repository;

import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OccupancyRepository extends JpaRepository<RoomAvailability, Long> {

    @Query("""
        SELECT ra.date, 
               SUM(ra.bookedRooms) * 1.0 / NULLIF(SUM(ra.totalRooms), 0) * 100 
        FROM RoomAvailability ra
        WHERE ra.hotelRoomType.hotel.id = :hotelId
          AND ra.date BETWEEN :startDate AND :endDate
        GROUP BY ra.date
        ORDER BY ra.date ASC
    """)
    List<Object[]> getDailyOccupancy(@Param("hotelId") Integer hotelId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT ra.date, 
               SUM(ra.bookedRooms) * 1.0 / NULLIF(SUM(ra.totalRooms), 0) * 100 
        FROM RoomAvailability ra
        WHERE ra.hotelRoomType.id = :roomTypeId
          AND ra.date BETWEEN :startDate AND :endDate
        GROUP BY ra.date
        ORDER BY ra.date ASC
    """)
    List<Object[]> getDailyOccupancyByRoomType(@Param("roomTypeId") Integer roomTypeId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}
