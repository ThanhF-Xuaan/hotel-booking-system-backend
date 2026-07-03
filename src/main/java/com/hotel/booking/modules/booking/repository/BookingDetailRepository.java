package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.BookingDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {

    @Override
    @EntityGraph(attributePaths = {"booking", "hotelRoomType"})
    Optional<BookingDetail> findById(Long id);

    @Query("""
        SELECT bd FROM BookingDetail bd
        JOIN bd.booking b
        WHERE b.status = 'CONFIRMED' 
          AND bd.selectionDeadline <= :now 
          AND bd.quantity > (
              SELECT COUNT(br) FROM BookingRoom br WHERE br.bookingDetail.id = bd.id
          )
    """)
    List<BookingDetail> findDetailsNeedingAutoAssignment(@Param("now") OffsetDateTime now);
}
