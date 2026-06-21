package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.RoomBed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomBedRepository extends JpaRepository<RoomBed, Short> {
    List<RoomBed> findAllByIsDeletedFalse();
}
