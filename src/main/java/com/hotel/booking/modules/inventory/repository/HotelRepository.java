package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Short> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Short id);
    List<Hotel> findAllByIsDeletedFalse();
    Optional<Hotel> findByIdAndIsDeletedFalse(Short id);
}
