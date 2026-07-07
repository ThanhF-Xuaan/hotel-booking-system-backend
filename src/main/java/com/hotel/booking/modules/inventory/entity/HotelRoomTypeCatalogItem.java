package com.hotel.booking.modules.inventory.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.modules.inventory.enums.ItemUsage;
import com.hotel.booking.modules.inventory.enums.PricingType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.math.BigDecimal;

@Entity
@Table(name = "hotel_room_type_catalog_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelRoomTypeCatalogItem extends BaseEntity {

    @EmbeddedId
    HotelRoomTypeCatalogItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hotelRoomTypeId")
    @JoinColumn(name = "hotel_room_type_id")
    HotelRoomType hotelRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("catalogItemId")
    @JoinColumn(name = "catalog_item_id")
    CatalogItem catalogItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_usage", nullable = false, length = 20)
    ItemUsage itemUsage;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type", nullable = false, length = 20)
    PricingType pricingType;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    BigDecimal price;
}
