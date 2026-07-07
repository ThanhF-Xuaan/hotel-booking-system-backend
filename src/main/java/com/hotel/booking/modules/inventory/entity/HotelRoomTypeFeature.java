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
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "hotel_room_type_features")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelRoomTypeFeature {

    @EmbeddedId
    HotelRoomTypeFeatureId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hotelRoomTypeId")
    @JoinColumn(name = "hotel_room_type_id")
    HotelRoomType hotelRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomFeatureId")
    @JoinColumn(name = "room_feature_id")
    RoomFeature roomFeature;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    OffsetDateTime createdAt;
}
