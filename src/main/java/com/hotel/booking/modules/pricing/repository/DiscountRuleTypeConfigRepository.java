package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.DiscountRuleTypeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRuleTypeConfigRepository extends JpaRepository<DiscountRuleTypeConfig, String> {
    Optional<DiscountRuleTypeConfig> findByCodeAndIsDeletedFalse(String code);
    List<DiscountRuleTypeConfig> findAllByIsDeletedFalse();
}
