package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Integer> {
    Optional<Campaign> findByIdAndIsDeletedFalse(Integer id);
    List<Campaign> findAllByIsDeletedFalse();
    List<Campaign> findAllByHotelIdAndIsDeletedFalse(Short hotelId);

    @Query("""
        SELECT COUNT(c) > 0 FROM Campaign c
        WHERE c.hotel.id = :hotelId
          AND c.name = :name
          AND c.startDate <= :endDate
          AND c.endDate >= :startDate
          AND c.isDeleted = false
          AND (:id IS NULL OR c.id <> :id)
    """)
    boolean existsOverlappingName(
            @Param("hotelId") Short hotelId,
            @Param("name") String name,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("id") Integer id);
}
