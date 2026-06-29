package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRoomTypeRepository extends JpaRepository<HotelRoomType, Integer> {
    Optional<HotelRoomType> findByIdAndIsDeletedFalse(Integer id);
    List<HotelRoomType> findAllByIsDeletedFalse();
    List<HotelRoomType> findAllByHotelIdAndIsDeletedFalse(Short hotelId);
    boolean existsByHotelIdAndRoomTypeIdAndIsDeletedFalse(Short hotelId, Short roomTypeId);
    boolean existsByHotelIdAndRoomTypeIdAndIdNotAndIsDeletedFalse(Short hotelId, Short roomTypeId, Integer id);
    List<HotelRoomType> findAllByIdInAndIsDeletedFalse(Collection<Integer> ids);
}
