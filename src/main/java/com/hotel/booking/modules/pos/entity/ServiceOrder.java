package com.hotel.booking.modules.pos.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.modules.booking.entity.Booking;
import com.hotel.booking.modules.inventory.entity.RoomInstance;
import com.hotel.booking.modules.pos.enums.ServiceOrderStatus;
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
@Table(name = "service_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_instance_id", nullable = false)
    RoomInstance roomInstance;

    @Column(name = "sub_total", nullable = false, precision = 15, scale = 2)
    BigDecimal subTotal;

    @Column(name = "service_fee_rate", nullable = false, precision = 5, scale = 2)
    BigDecimal serviceFeeRate;

    @Column(name = "service_fee_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal serviceFeeAmount;

    @Column(name = "vat_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal vatAmount;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    ServiceOrderStatus status;

    @Column(name = "issued_at")
    OffsetDateTime issuedAt;

    @Column(name = "is_deleted")
    Boolean isDeleted = false;
}
