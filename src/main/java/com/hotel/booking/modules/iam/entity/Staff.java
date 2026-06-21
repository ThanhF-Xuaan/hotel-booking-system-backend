package com.hotel.booking.modules.iam.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.inventory.entity.Hotel;
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

import java.util.UUID;

@Entity
@Table(name = "staffs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Staff extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "public_id", updatable = false, nullable = false)
    UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    Role role;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    String username;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "first_name", nullable = false, length = 100)
    String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    String lastName;

    @Column(name = "full_name", nullable = false, length = 250)
    String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    ActiveStatus status;

    @Column(name = "is_deleted")
    Boolean isDeleted = false;
}
