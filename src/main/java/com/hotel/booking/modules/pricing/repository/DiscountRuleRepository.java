package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRuleRepository extends JpaRepository<DiscountRule, Integer> {
}
