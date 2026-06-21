package com.hotel.booking.modules.pos.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.modules.inventory.entity.CatalogItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "service_order_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceOrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_order_id", nullable = false)
    ServiceOrder serviceOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_item_id", nullable = false)
    CatalogItem catalogItem;

    @Column(name = "item_name", nullable = false, length = 150)
    String itemName;

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
}
