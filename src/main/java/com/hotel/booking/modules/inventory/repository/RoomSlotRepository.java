package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.RoomSlot;
import com.hotel.booking.modules.inventory.enums.RoomSlotStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomSlotRepository extends JpaRepository<RoomSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RoomSlot> findByRoomInstanceIdAndSlotDate(Integer roomInstanceId, LocalDate slotDate);

    boolean existsByRoomInstanceIdAndSlotDateBetweenAndStatusIn(
            Integer roomInstanceId,
            LocalDate startDate,
            LocalDate endDate,
            Collection<RoomSlotStatus> statuses
    );

    @Query("""
        SELECT COUNT(rs) > 0 FROM RoomSlot rs 
        WHERE rs.roomInstance.id = :roomInstanceId 
          AND rs.slotDate >= :checkIn 
          AND rs.slotDate < :checkOut
          AND rs.status != com.hotel.booking.modules.inventory.enums.RoomSlotStatus.READY
    """)
    boolean existsConflictingSlots(
            @Param("roomInstanceId") Integer roomInstanceId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    // Xóa các slot BLOCKED của một phòng cụ thể thuộc về một booking_detail_id (Dùng cho BƯỚC 15)
    @Modifying
    @Query("""
        DELETE FROM RoomSlot rs 
        WHERE rs.roomInstance.id = :roomInstanceId 
          AND rs.bookingDetail.id = :bookingDetailId
          AND rs.status = 'BLOCKED'
    """)
    void releaseBlockedSlots(
            @Param("roomInstanceId") Integer roomInstanceId,
            @Param("bookingDetailId") Long bookingDetailId
    );

    // Lấy danh sách các slot hiện có trong khoảng thời gian để chuẩn bị Upsert
    @Query("""
        SELECT rs FROM RoomSlot rs 
        WHERE rs.roomInstance.id = :roomInstanceId 
          AND rs.slotDate >= :checkIn 
          AND rs.slotDate < :checkOut
    """)
    List<RoomSlot> findByRoomInstanceIdAndDateRange(
            @Param("roomInstanceId") Integer roomInstanceId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    @Query("""
        SELECT rs FROM RoomSlot rs 
        WHERE rs.roomInstance.id = :roomInstanceId 
          AND rs.bookingDetail.id = :bookingDetailId
    """)
    List<RoomSlot> findAndLockSlotsForConfirmation(
            @Param("roomInstanceId") Integer roomInstanceId,
            @Param("bookingDetailId") Long bookingDetailId
    );

    @Query("""
        SELECT COUNT(DISTINCT rs.roomInstance.id) FROM RoomSlot rs 
        WHERE rs.bookingDetail.id = :bookingDetailId 
          AND rs.status IN (com.hotel.booking.modules.inventory.enums.RoomSlotStatus.BLOCKED, 
                            com.hotel.booking.modules.inventory.enums.RoomSlotStatus.RESERVED)
    """)
    long countDistinctRoomsByBookingDetail(@Param("bookingDetailId") Long bookingDetailId);

    @Query("""
        SELECT COUNT(rs) > 0 FROM RoomSlot rs 
        WHERE rs.roomInstance.id = :roomInstanceId 
          AND rs.bookingDetail.id = :bookingDetailId 
          AND rs.status = com.hotel.booking.modules.inventory.enums.RoomSlotStatus.RESERVED
    """)
    boolean hasReservedSlots(
            @Param("roomInstanceId") Integer roomInstanceId,
            @Param("bookingDetailId") Long bookingDetailId
    );

    @Query("SELECT rs FROM RoomSlot rs WHERE rs.bookingDetail.id = :bookingDetailId AND rs.status = :status")
    List<RoomSlot> findByBookingDetailIdAndStatus(
            @Param("bookingDetailId") Long bookingDetailId,
            @Param("status") RoomSlotStatus status
    );

    List<RoomSlot> findByBookingDetailId(Long bookingDetailId);

    List<RoomSlot> findByRoomInstanceIdAndBookingDetailId(Integer roomInstanceId,
                                                          Long bookingDetailId);

    List<RoomSlot> findByRoomInstanceIdAndSlotDateBetween(Integer roomInstanceId,
                                                          LocalDate startDate,
                                                          LocalDate endDate);

    // Tìm các slot của 1 phòng từ ngày hôm nay trở đi đang có status cụ thể
    List<RoomSlot> findByRoomInstanceIdAndStatusAndSlotDateGreaterThanEqualOrderBySlotDateAsc(Integer roomInstanceId,
                                                                            RoomSlotStatus status,
                                                                            LocalDate date);

    // Lấy các slot, JOIN FETCH luôn BookingDetail và Booking để lấy thông tin khách
    @Query("SELECT rs, b.id, b.status, g.fullName FROM RoomSlot rs " +
            "LEFT JOIN rs.bookingDetail bd " +
            "LEFT JOIN bd.booking b " +
            "LEFT JOIN b.guest g " +
            "WHERE rs.roomInstance.id IN :roomInstanceIds " +
            "AND rs.slotDate BETWEEN :startDate AND :endDate")
    List<Object[]> findTimelineSlotsRaw(
            @Param("roomInstanceIds") List<Integer> roomInstanceIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")}) // Timeout 3s để chống Deadlock
    @Query("SELECT rs FROM RoomSlot rs " +
            "WHERE rs.roomInstance.id = :roomInstanceId " +
            "AND rs.slotDate >= :checkInDate " +
            "AND rs.slotDate < :checkOutDate")
    List<RoomSlot> findSlotsForUpdate(
            @Param("roomInstanceId") Integer roomInstanceId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    List<RoomSlot> findByRoomInstanceIdAndStatusAndSlotDateBetween(
            Integer roomInstanceId, RoomSlotStatus status, LocalDate start, LocalDate end);

    List<RoomSlot> findByRoomInstanceIdAndStatusInAndSlotDateBetween(
            Integer roomInstanceId,
            List<RoomSlotStatus> statuses,
            LocalDate startDate,
            LocalDate endDate
    );
}
