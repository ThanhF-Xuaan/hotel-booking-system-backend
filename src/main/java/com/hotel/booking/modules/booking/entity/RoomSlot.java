package com.hotel.booking.modules.booking.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.modules.booking.enums.RoomSlotStatus;
import com.hotel.booking.modules.inventory.entity.RoomInstance;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "room_slots", uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_slot", columnNames = { "room_instance_id", "slot_date" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_instance_id", nullable = false)
    RoomInstance roomInstance;

    @Column(name = "slot_date", nullable = false)
    LocalDate slotDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_detail_id")
    BookingDetail bookingDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    RoomSlotStatus status;

    @Column(name = "locked_at")
    OffsetDateTime lockedAt;

    @Column(name = "reserved_at")
    OffsetDateTime reservedAt;
}
