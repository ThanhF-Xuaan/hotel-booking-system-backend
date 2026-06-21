package com.hotel.booking.modules.inventory.entity;

import com.hotel.booking.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "room_availability", uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_availability", columnNames = { "hotel_room_type_id", "date" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomAvailability extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_room_type_id", nullable = false)
    HotelRoomType hotelRoomType;

    @Column(name = "date", nullable = false)
    LocalDate date;

    @Column(name = "total_rooms", nullable = false)
    Integer totalRooms;

    @Column(name = "booked_rooms", nullable = false)
    Integer bookedRooms;

    @Column(name = "locked_rooms", nullable = false)
    Integer lockedRooms;

    @Column(name = "available_count", insertable = false, updatable = false)
    Integer availableCount;

    @Version
    @Column(name = "version", nullable = false)
    Long version;
}
