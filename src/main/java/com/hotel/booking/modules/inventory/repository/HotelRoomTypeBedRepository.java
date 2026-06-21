package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.HotelRoomTypeBed;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeBedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRoomTypeBedRepository extends JpaRepository<HotelRoomTypeBed, HotelRoomTypeBedId> {
}
