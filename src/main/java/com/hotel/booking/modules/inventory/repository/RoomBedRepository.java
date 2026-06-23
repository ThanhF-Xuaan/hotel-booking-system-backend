package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.RoomBed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomBedRepository extends JpaRepository<RoomBed, Short> {
    List<RoomBed> findAllByIsDeletedFalse();
    Optional<RoomBed> findByIdAndIsDeletedFalse(Short id);
    boolean existsByNameAndIsDeletedFalse(String name);
    boolean existsByNameAndIdNotAndIsDeletedFalse(String name, Short id);
}
