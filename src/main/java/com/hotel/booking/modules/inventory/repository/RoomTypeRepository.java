package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Short> {
    List<RoomType> findAllByIsDeletedFalse();
}
