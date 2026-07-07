package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRuleRepository extends JpaRepository<DiscountRule, Integer> {
    List<DiscountRule> findAllByIsDeletedFalse();
    Optional<DiscountRule> findByIdAndIsDeletedFalse(Integer id);
    List<DiscountRule> findAllByHotelRoomTypeIdAndIsDeletedFalse(Integer hotelRoomTypeId);

    @Query("""
        SELECT dr FROM DiscountRule dr
        WHERE dr.hotelRoomType.id IN :roomTypeIds
          AND dr.ruleType.code = :ruleTypeCode
          AND dr.startDate <= :endDate
          AND dr.endDate >= :startDate
          AND dr.isDeleted = false
    """)
    List<DiscountRule> findOverlappingRules(
            @Param("roomTypeIds") Collection<Integer> roomTypeIds,
            @Param("ruleTypeCode") String ruleTypeCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT COUNT(dr) > 0 FROM DiscountRule dr
        WHERE dr.hotelRoomType.id = :hotelRoomTypeId
          AND dr.ruleType.code = :ruleTypeCode
          AND dr.startDate <= :endDate
          AND dr.endDate >= :startDate
          AND dr.isDeleted = false
          AND (:id IS NULL OR dr.id <> :id)
    """)
    boolean existsOverlapping(
            @Param("hotelRoomTypeId") Integer hotelRoomTypeId,
            @Param("ruleTypeCode") String ruleTypeCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("id") Integer id);
}
