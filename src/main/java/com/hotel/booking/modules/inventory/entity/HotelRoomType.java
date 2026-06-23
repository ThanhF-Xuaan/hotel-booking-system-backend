package com.hotel.booking.modules.inventory.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.core.enums.ActiveStatus;
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

import java.math.BigDecimal;

@Entity
@Table(name = "hotel_room_types", uniqueConstraints = {
        @UniqueConstraint(name = "uk_hotel_room_type", columnNames = { "hotel_id", "room_type_id" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelRoomType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    RoomType roomType;

    // Sức chứa tiêu chuẩn (Làm mốc tính base_price)
    @Column(name = "standard_adults", nullable = false)
    Integer standardAdults;

    @Column(name = "standard_children", nullable = false)
    Integer standardChildren;

    // Sức chứa tối đa của từng đối tượng (Validation)
    @Column(name = "max_adults", nullable = false)
    Integer maxAdults;

    @Column(name = "max_children", nullable = false)
    Integer maxChildren;

    @Column(name = "max_infants", nullable = false)
    Integer maxInfants;

    // Giới hạn phòng & Giường phụ
    @Column(name = "max_total_guests", nullable = false)
    Integer maxTotalGuests;

    @Column(name = "max_extra_beds", nullable = false)
    Integer maxExtraBeds;

    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    BigDecimal basePrice;

    @Column(name = "total_quantity", nullable = false)
    Integer totalQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    ActiveStatus status;

    @Column(name = "is_deleted")
    Boolean isDeleted = false;
}
