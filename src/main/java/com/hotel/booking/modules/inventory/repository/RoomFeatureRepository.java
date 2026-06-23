package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.RoomFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomFeatureRepository extends JpaRepository<RoomFeature, Short> {
    List<RoomFeature> findAllByIsDeletedFalse();
    Optional<RoomFeature> findByIdAndIsDeletedFalse(Short id);
    boolean existsByCodeAndIsDeletedFalse(String code);
    boolean existsByCodeAndIdNotAndIsDeletedFalse(String code, Short id);
    boolean existsByNameAndIsDeletedFalse(String name);
    boolean existsByNameAndIdNotAndIsDeletedFalse(String name, Short id);
}
