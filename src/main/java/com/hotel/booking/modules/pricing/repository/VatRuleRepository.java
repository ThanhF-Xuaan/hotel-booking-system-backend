package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.VatRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VatRuleRepository extends JpaRepository<VatRule, Integer> {
}
