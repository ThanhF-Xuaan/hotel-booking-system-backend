package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogItemRepository extends JpaRepository<CatalogItem, Integer> {
    List<CatalogItem> findAllByIdInAndIsDeletedFalse(Collection<Integer> ids);
    Optional<CatalogItem> findByIdAndIsDeletedFalse(Integer id);

    List<CatalogItem> findAllByIsDeletedFalse();
    List<CatalogItem> findAllByHotelIdAndIsDeletedFalse(Short hotelId);
}
