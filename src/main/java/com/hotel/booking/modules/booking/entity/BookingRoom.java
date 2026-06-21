package com.hotel.booking.modules.booking.entity;

import com.hotel.booking.modules.inventory.entity.RoomInstance;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "booking_rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRoom {

    @EmbeddedId
    BookingRoomId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookingDetailId")
    @JoinColumn(name = "booking_detail_id")
    BookingDetail bookingDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomInstanceId")
    @JoinColumn(name = "room_instance_id")
    RoomInstance roomInstance;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    OffsetDateTime assignedAt;
}
