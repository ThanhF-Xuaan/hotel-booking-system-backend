package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Integer> {
    List<PricingRule> findAllByIsDeletedFalse();
}
