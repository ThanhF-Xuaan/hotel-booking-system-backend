package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.HotelAgePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelAgePolicyRepository extends JpaRepository<HotelAgePolicy, Short> {
    List<HotelAgePolicy> findAllByIsDeletedFalse();
    Optional<HotelAgePolicy> findByIdAndIsDeletedFalse(Short id);
    List<HotelAgePolicy> findAllByHotelIdAndIsDeletedFalse(Short hotelId);
    Optional<HotelAgePolicy> findByIdAndHotelIdAndIsDeletedFalse(Short id, Short hotelId);
    boolean existsByHotelIdAndGuestTypeAndIsDeletedFalse(Short hotelId, String guestType);
    boolean existsByHotelIdAndGuestTypeAndIdNotAndIsDeletedFalse(Short hotelId, String guestType, Short id);
}
