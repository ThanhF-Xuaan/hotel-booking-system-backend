package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.PricingRuleTypeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRuleTypeConfigRepository extends JpaRepository<PricingRuleTypeConfig, String> {
    Optional<PricingRuleTypeConfig> findByCodeAndIsDeletedFalse(String code);
    List<PricingRuleTypeConfig> findAllByIsDeletedFalse();
}
