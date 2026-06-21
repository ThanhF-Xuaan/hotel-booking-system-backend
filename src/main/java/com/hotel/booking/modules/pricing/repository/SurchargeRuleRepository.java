package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurchargeRuleRepository extends JpaRepository<SurchargeRule, Integer> {
}
