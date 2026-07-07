package com.hotel.booking.modules.booking.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.modules.booking.enums.ChargeType;
import com.hotel.booking.modules.inventory.entity.CatalogItem;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "booking_charges")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCharge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_detail_id", nullable = false)
    BookingDetail bookingDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_guest_id")
    BookingGuest bookingGuest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_item_id")
    CatalogItem catalogItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type", nullable = false, length = 50)
    ChargeType chargeType;

    @Column(name = "item_name", length = 150)
    String itemName;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    BigDecimal subtotal;

    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    BigDecimal vatRate;

    @Column(name = "vat_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal vatAmount;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal totalAmount;

    @Column(name = "issued_at")
    OffsetDateTime issuedAt;
}
