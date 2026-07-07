package com.hotel.booking.modules.booking.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.modules.booking.enums.InvoiceStatus;
import jakarta.persistence.*;
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
@Table(name = "invoices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true) // Thêm unique = true
    Booking booking;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    String invoiceNumber;

    @Column(name = "sub_total", nullable = false, precision = 15, scale = 2)
    BigDecimal subTotal;

    @Column(name = "service_fee_rate", nullable = false, precision = 5, scale = 2)
    BigDecimal serviceFeeRate;

    @Column(name = "service_fee_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal serviceFeeAmount;

    @Column(name = "vat_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal vatAmount;

    @Column(name = "grand_total", nullable = false, precision = 15, scale = 2)
    BigDecimal grandTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    InvoiceStatus status;

    @Column(name = "issued_at")
    OffsetDateTime issuedAt;
}
