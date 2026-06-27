package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Integer> {
    List<PricingRule> findAllByIsDeletedFalse();
    Optional<PricingRule> findByIdAndIsDeletedFalse(Integer id);
    List<PricingRule> findAllByHotelRoomTypeIdAndIsDeletedFalse(Integer hotelRoomTypeId);

    @Query("""
        SELECT COUNT(pr) > 0 FROM PricingRule pr
        WHERE pr.hotelRoomType.id = :hotelRoomTypeId
          AND pr.ruleType.code = :ruleTypeCode
          AND pr.startDate <= :endDate
          AND pr.endDate >= :startDate
          AND pr.isDeleted = false
          AND (:id IS NULL OR pr.id <> :id)
    """)
    boolean existsOverlapping(
            @Param("hotelRoomTypeId") Integer hotelRoomTypeId,
            @Param("ruleTypeCode") String ruleTypeCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("id") Integer id);
}
