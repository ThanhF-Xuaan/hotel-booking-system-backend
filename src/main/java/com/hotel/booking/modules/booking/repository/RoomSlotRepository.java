package com.hotel.booking.modules.booking.repository;

import com.hotel.booking.modules.booking.entity.RoomSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RoomSlotRepository extends JpaRepository<RoomSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RoomSlot> findByRoomInstanceIdAndSlotDate(Integer roomInstanceId, LocalDate slotDate);
}
