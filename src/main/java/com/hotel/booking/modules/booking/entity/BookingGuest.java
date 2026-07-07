package com.hotel.booking.modules.booking.entity;

import com.hotel.booking.modules.crm.entity.Guest;
import com.hotel.booking.modules.crm.enums.GuestType;
import com.hotel.booking.modules.crm.enums.IdentityType;
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
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "booking_guests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_detail_id", nullable = false)
    BookingDetail bookingDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    Guest guest;

    @Column(name = "full_name", nullable = false, length = 250)
    String fullName;

    @Column(name = "birth_date")
    LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "guest_type", nullable = false, length = 20)
    GuestType guestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_type", length = 20)
    IdentityType identityType;

    @Column(name = "identity_number", length = 50)
    String identityNumber;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    OffsetDateTime createdAt;
}
