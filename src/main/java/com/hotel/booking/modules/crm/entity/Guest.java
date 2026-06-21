package com.hotel.booking.modules.crm.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.crm.enums.IdentityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "guests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Guest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "public_id", updatable = false, nullable = false)
    UUID publicId;

    @Column(name = "first_name", nullable = false, length = 100)
    String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    String lastName;

    @Column(name = "full_name", nullable = false, length = 250)
    String fullName;

    @Column(name = "birth_date")
    LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_type", length = 20)
    IdentityType identityType;

    @Column(name = "identity_number", length = 50)
    String identityNumber;

    @Column(name = "nationality", length = 100)
    String nationality;

    @Column(name = "email", length = 150)
    String email;

    @Column(name = "phone", nullable = false, length = 20)
    String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    ActiveStatus status;

    @Column(name = "is_deleted")
    Boolean isDeleted = false;
}
