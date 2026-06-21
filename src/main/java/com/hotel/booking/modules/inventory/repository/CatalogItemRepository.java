package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CatalogItemRepository extends JpaRepository<CatalogItem, Integer> {
    List<CatalogItem> findAllByIsDeletedFalse();
}
