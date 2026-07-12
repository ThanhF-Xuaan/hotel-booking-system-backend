package com.hotel.booking.modules.inventory.repository;

import com.hotel.booking.modules.inventory.entity.RoomInstance;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomInstanceRepository extends JpaRepository<RoomInstance, Integer> {
    interface RoomStatusProjection {
        Integer getRoomInstanceId();
        String getRoomNumber();
        String getStatus();
    }

    List<RoomInstance> findAllByIsDeletedFalse();
    Optional<RoomInstance> findByIdAndIsDeletedFalse(Integer id);
    long countByHotelRoomTypeIdAndIsDeletedFalse(Integer hotelRoomTypeId);
    boolean existsByHotelIdAndRoomNumberAndIsDeletedFalse(Short hotelId, String roomNumber);
    boolean existsByHotelIdAndRoomNumberAndIdNotAndIsDeletedFalse(Short hotelId, String roomNumber, Integer id);
    List<RoomInstance> findAllByHotelIdAndIsDeletedFalse(Short hotelId);

    /**
     * Logic Query:
     * 1. NOT EXISTS (... IN ('RESERVED', 'OCCUPIED', 'CLEANING', 'MAINTENANCE')):
     *    -> Nếu phòng dính bất kỳ ngày nào có status này trong khoảng checkIn -> checkOut, LOẠI BỎ hoàn toàn.
     * 2. CASE WHEN EXISTS (... = 'BLOCKED'):
     *    -> Trong số các phòng lọt qua bước 1, nếu có ngày bị BLOCKED -> Trả về BLOCKED.
     * 3. ELSE:
     *    -> Trả về READY.
     */
    @Query("""
        SELECT ri.id AS roomInstanceId,
               ri.roomNumber AS roomNumber,
               CASE
                   WHEN EXISTS (
                       SELECT 1 FROM RoomSlot rs
                       WHERE rs.roomInstance.id = ri.id
                         AND rs.slotDate >= :checkIn 
                         AND rs.slotDate < :checkOut
                         AND rs.status = 'BLOCKED'
                   ) THEN 'BLOCKED'
                   ELSE 'READY'
               END AS status
        FROM RoomInstance ri
        WHERE ri.hotelRoomType.id = :hotelRoomTypeId
          AND ri.isDeleted = false
          AND NOT EXISTS (
              SELECT 1 FROM RoomSlot rs_invalid
              WHERE rs_invalid.roomInstance.id = ri.id
                AND rs_invalid.slotDate >= :checkIn 
                AND rs_invalid.slotDate < :checkOut
                AND rs_invalid.status IN ('RESERVED', 'OCCUPIED', 'CLEANING', 'MAINTENANCE')
          )
    """)
    List<RoomStatusProjection> findSelectableRooms(
            @Param("hotelRoomTypeId") Integer hotelRoomTypeId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")}) // Chờ tối đa 3 giây (3000ms)
    @Query("SELECT r FROM RoomInstance r WHERE r.id = :id")
    Optional<RoomInstance> findByIdWithPessimisticLock(@Param("id") Integer id);

    @Query("""
        SELECT ri FROM RoomInstance ri 
        WHERE ri.hotelRoomType.id = :hotelRoomTypeId
          AND ri.isDeleted = false
          AND NOT EXISTS (
              SELECT 1 FROM RoomSlot rs 
              WHERE rs.roomInstance.id = ri.id 
                AND rs.slotDate >= :checkIn 
                AND rs.slotDate < :checkOut 
                AND rs.status != com.hotel.booking.modules.inventory.enums.RoomSlotStatus.READY
          )
    """)
    List<RoomInstance> findCompletelyFreeRooms(
            @Param("hotelRoomTypeId") Integer hotelRoomTypeId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Query("SELECT r " +
            "FROM RoomInstance r " +
            "JOIN FETCH r.hotelRoomType hrt " +
            "JOIN FETCH hrt.roomType rt " +
            "WHERE hrt.hotel.id = :hotelId " +
            "ORDER BY hrt.id, r.roomNumber")
    List<RoomInstance> findAllByHotelIdWithRoomType(@Param("hotelId") Integer hotelId);
}
