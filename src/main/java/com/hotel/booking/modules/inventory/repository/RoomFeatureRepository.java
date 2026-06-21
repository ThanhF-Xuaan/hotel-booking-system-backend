package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.RoomFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomFeatureRepository extends JpaRepository<RoomFeature, Short> {
    List<RoomFeature> findAllByIsDeletedFalse();
}
