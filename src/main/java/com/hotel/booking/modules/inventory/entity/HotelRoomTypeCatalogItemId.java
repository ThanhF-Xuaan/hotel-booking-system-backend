package com.hotel.booking.modules.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HotelRoomTypeCatalogItemId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "hotel_room_type_id")
    private Integer hotelRoomTypeId;

    @Column(name = "catalog_item_id")
    private Integer catalogItemId;
}
