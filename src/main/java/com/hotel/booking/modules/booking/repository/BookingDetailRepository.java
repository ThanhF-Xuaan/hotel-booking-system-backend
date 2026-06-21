package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.BookingDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {

    @Override
    @EntityGraph(attributePaths = {"booking", "hotelRoomType"})
    Optional<BookingDetail> findById(Long id);
}
