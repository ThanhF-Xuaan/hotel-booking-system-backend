package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {
}
