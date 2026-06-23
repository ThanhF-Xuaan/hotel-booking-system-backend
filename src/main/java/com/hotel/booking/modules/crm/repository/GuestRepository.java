package com.hotel.booking.modules.crm.repository;

import com.hotel.booking.modules.crm.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByPhoneAndIsDeletedFalse(String phone);
    Optional<Guest> findByPublicIdAndIsDeletedFalse(UUID publicId);
    List<Guest> findAllByIsDeletedFalse();
    Optional<Guest> findByIdAndIsDeletedFalse(Long id);
}
