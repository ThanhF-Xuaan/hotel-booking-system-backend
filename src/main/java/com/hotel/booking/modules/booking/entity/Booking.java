package com.hotel.booking.modules.booking.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.modules.booking.enums.BookingStatus;
import com.hotel.booking.modules.crm.entity.Guest;
import com.hotel.booking.modules.inventory.entity.Hotel;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import java.util.List;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    Guest guest;

    @Column(name = "booking_number", nullable = false, unique = true, length = 50)
    String bookingNumber;

    @Column(name = "subtotal_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal subtotalAmount;

    @Column(name = "service_fee_rate", nullable = false, precision = 5, scale = 2)
    BigDecimal serviceFeeRate;

    @Column(name = "service_fee_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal serviceFeeAmount;

    @Column(name = "total_vat_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal totalVatAmount;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    BookingStatus status;

    @Column(name = "expired_at")
    OffsetDateTime expiredAt;

    @Column(name = "issued_at")
    OffsetDateTime issuedAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<BookingDetail> bookingDetails = new java.util.ArrayList<>();
}
