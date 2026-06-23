package com.hotel.booking.modules.inventory.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.core.enums.ActiveStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import com.hotel.booking.modules.inventory.enums.FeatureCategory;

@Entity
@Table(
    name = "room_features",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_feature_name", columnNames = {"name"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomFeature extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Short id;

    @Column(name = "name", nullable = false, length = 150)
    String name;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    String code;

    @Column(name = "icon")
    String icon;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)
    FeatureCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    ActiveStatus status;

    @Column(name = "is_deleted", nullable = false)
    Boolean isDeleted = false;
}
