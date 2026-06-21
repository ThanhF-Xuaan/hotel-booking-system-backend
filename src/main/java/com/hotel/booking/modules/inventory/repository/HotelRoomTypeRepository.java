package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRoomTypeRepository extends JpaRepository<HotelRoomType, Integer> {
}
