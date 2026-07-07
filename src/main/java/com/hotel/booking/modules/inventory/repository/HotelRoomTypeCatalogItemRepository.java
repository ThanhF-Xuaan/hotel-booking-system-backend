package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.HotelRoomTypeCatalogItem;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeCatalogItemId;
import com.hotel.booking.modules.inventory.enums.ItemUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRoomTypeCatalogItemRepository
        extends JpaRepository<HotelRoomTypeCatalogItem, HotelRoomTypeCatalogItemId> {
    List<HotelRoomTypeCatalogItem> findAllByHotelRoomTypeId(Integer hotelRoomTypeId);
    void deleteAllByIdInBatch(Iterable<HotelRoomTypeCatalogItemId> ids);

    boolean existsByHotelRoomTypeIdAndCatalogItemIdAndItemUsage(Integer hotelRoomTypeId,
                                                                Integer catalogItemId,
                                                                ItemUsage type);

    Optional<HotelRoomTypeCatalogItem> findByHotelRoomTypeIdAndCatalogItemId(Integer hotelRoomTypeId,
                                                                             Integer catalogItemId);
}
