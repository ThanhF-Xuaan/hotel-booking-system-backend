package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Short> {
    boolean existsByCodeAndIsDeletedFalse(String code);
    boolean existsByNameAndIsDeletedFalse(String name);
    boolean existsByNameAndIdNotAndIsDeletedFalse(String name, Short id);
    List<RoomType> findAllByIsDeletedFalse();
    Optional<RoomType> findByIdAndIsDeletedFalse(Short id);
}
