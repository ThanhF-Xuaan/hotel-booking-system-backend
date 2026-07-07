package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.HotelRoomTypeFeature;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRoomTypeFeatureRepository extends JpaRepository<HotelRoomTypeFeature, HotelRoomTypeFeatureId> {
    List<HotelRoomTypeFeature> findAllByHotelRoomTypeId(Integer hotelRoomTypeId);
    void deleteAllByIdInBatch(Iterable<HotelRoomTypeFeatureId> ids);
}
