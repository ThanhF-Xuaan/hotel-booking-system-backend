package com.hotel.booking.modules.inventory.entity;

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

@Entity
@Table(name = "hotel_room_type_beds")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelRoomTypeBed {

    @EmbeddedId
    HotelRoomTypeBedId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hotelRoomTypeId")
    @JoinColumn(name = "hotel_room_type_id")
    HotelRoomType hotelRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomBedId")
    @JoinColumn(name = "room_bed_id")
    RoomBed roomBed;

    @Column(name = "quantity", nullable = false)
    Short quantity;
}
