package com.hotel.booking.modules.inventory.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.inventory.enums.ItemType;
import com.hotel.booking.modules.pricing.entity.VatRule;
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

@Entity
@Table(name = "catalog_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CatalogItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    Hotel hotel;

    @Column(name = "name", nullable = false, length = 150)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 50)
    ItemType itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_rule_id", nullable = false)
    VatRule vatRule;

    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    ActiveStatus status;

    @Column(name = "is_deleted")
    Boolean isDeleted = false;
}
