package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
import com.hotel.booking.modules.crm.enums.GuestType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SurchargeRuleRepository extends JpaRepository<SurchargeRule, Integer> {
    List<SurchargeRule> findAllByIsDeletedFalse();
    Optional<SurchargeRule> findByIdAndIsDeletedFalse(Integer id);
    List<SurchargeRule> findAllByHotelRoomTypeIdAndIsDeletedFalse(Integer hotelRoomTypeId);

    @Query("""
        SELECT COUNT(sr) > 0 FROM SurchargeRule sr
        WHERE sr.hotelRoomType.id = :hotelRoomTypeId
          AND sr.ruleType = :ruleType
          AND (:guestType IS NULL AND sr.guestType IS NULL OR sr.guestType = :guestType)
          AND sr.startDate <= :endDate
          AND sr.endDate >= :startDate
          AND sr.isDeleted = false
          AND (:id IS NULL OR sr.id <> :id)
    """)
    boolean existsOverlapping(
            @Param("hotelRoomTypeId") Integer hotelRoomTypeId,
            @Param("ruleType") SurchargeRuleType ruleType,
            @Param("guestType") GuestType guestType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("id") Integer id);
}
