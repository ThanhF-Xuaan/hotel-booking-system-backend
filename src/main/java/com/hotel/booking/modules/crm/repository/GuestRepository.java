package com.hotel.booking.modules.crm.repository;

import com.hotel.booking.modules.crm.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findAllByIsDeletedFalse();
}
