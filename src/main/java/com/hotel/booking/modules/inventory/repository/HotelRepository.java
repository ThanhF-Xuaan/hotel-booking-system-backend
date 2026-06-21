package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Short> {
    List<Hotel> findAllByIsDeletedFalse();
}
