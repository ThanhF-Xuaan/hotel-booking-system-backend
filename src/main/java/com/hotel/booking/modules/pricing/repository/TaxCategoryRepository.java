package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.TaxCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxCategoryRepository extends JpaRepository<TaxCategory, Integer> {
    List<TaxCategory> findAllByIsDeletedFalse();
    Optional<TaxCategory> findByIdAndIsDeletedFalse(Integer id);
    boolean existsByCategoryCodeAndIsDeletedFalse(String categoryCode);
    boolean existsByCategoryCodeAndIdNotAndIsDeletedFalse(String categoryCode, Integer id);
}
