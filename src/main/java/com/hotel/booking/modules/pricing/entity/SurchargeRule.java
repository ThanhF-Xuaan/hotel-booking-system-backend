package com.hotel.booking.modules.pricing.entity;

import com.hotel.booking.core.entity.BaseEntity;
import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.pricing.entity.pojo.SurchargeCondition;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "surcharge_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SurchargeRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_room_type_id", nullable = false)
    HotelRoomType hotelRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "age_policy_id")
    HotelAgePolicy agePolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false, length = 50)
    SurchargeRuleType ruleType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conditions", nullable = true, columnDefinition = "jsonb")
    SurchargeCondition conditions;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", nullable = false, length = 20)
    AdjustmentType adjustmentType;

    @Column(name = "adjustment_value", nullable = false, precision = 15, scale = 2)
    BigDecimal adjustmentValue;

    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    ActiveStatus status;

    @Column(name = "is_deleted")
    Boolean isDeleted = false;
}

