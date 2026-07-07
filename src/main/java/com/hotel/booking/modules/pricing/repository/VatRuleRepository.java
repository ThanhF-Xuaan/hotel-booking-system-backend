package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.VatRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VatRuleRepository extends JpaRepository<VatRule, Integer> {
    List<VatRule> findAllByIsDeletedFalse();
    Optional<VatRule> findByIdAndIsDeletedFalse(Integer id);
    boolean existsByVatCodeAndIsDeletedFalse(String vatCode);
    boolean existsByVatNameAndIsDeletedFalse(String vatName);
    boolean existsByVatCodeAndIdNotAndIsDeletedFalse(String vatCode, Integer id);
    boolean existsByVatNameAndIdNotAndIsDeletedFalse(String vatName, Integer id);

    @Query("""
        SELECT vr FROM VatRule vr
        WHERE vr.taxCategory.id = :taxCategoryId
          AND vr.startDate <= :date
          AND (vr.endDate IS NULL OR vr.endDate >= :date)
          AND vr.status = 'ACTIVE'
          AND vr.isDeleted = false
        ORDER BY vr.startDate DESC
    """)
    List<VatRule> findActiveVatRules(
            @Param("taxCategoryId") Integer taxCategoryId,
            @Param("date") LocalDate date);

    @Query("""
    SELECT v FROM VatRule v 
    WHERE v.taxCategory.id = :taxCategoryId 
      AND v.status = 'ACTIVE' 
      AND v.startDate <= :date 
      AND (v.endDate IS NULL OR v.endDate >= :date)
    """)
    Optional<VatRule> findActiveRuleByTaxCategory(
            @Param("taxCategoryId") Integer taxCategoryId,
            @Param("date") LocalDate date
    );
}

