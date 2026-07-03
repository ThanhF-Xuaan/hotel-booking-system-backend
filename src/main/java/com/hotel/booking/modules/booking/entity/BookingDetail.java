package com.hotel.booking.modules.booking.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "booking_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_room_type_id", nullable = false)
    HotelRoomType hotelRoomType;

    @Column(name = "room_type_name", nullable = false, length = 150)
    String roomTypeName;

    @Column(name = "quantity", nullable = false)
    Short quantity;

    @Column(name = "guest_count", nullable = false)
    Short guestCount;

    @Column(name = "check_in_date", nullable = false)
    LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    LocalDate checkOutDate;

    @Column(name = "selection_deadline")
    OffsetDateTime selectionDeadline;

    @Column(name = "actual_check_in_at")
    OffsetDateTime actualCheckInAt;

    @Column(name = "actual_check_out_at")
    OffsetDateTime actualCheckOutAt;

    @Column(name = "room_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal roomAmount;

    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal discountAmount;

    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    BigDecimal vatRate;

    @Column(name = "vat_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal vatAmount;

    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    BigDecimal finalAmount;
}
