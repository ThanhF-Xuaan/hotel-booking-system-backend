package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SurchargeRuleRepository extends JpaRepository<SurchargeRule, Integer> {
    List<SurchargeRule> findAllByIsDeletedFalse();
    Optional<SurchargeRule> findByIdAndIsDeletedFalse(Integer id);
}
