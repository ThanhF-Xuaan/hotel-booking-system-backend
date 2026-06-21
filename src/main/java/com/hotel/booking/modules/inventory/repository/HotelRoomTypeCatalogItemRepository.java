package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.HotelRoomTypeCatalogItem;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeCatalogItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRoomTypeCatalogItemRepository extends JpaRepository<HotelRoomTypeCatalogItem, HotelRoomTypeCatalogItemId> {
}
