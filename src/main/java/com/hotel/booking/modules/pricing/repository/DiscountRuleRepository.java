package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRuleRepository extends JpaRepository<DiscountRule, Integer> {
    List<DiscountRule> findAllByIsDeletedFalse();
    Optional<DiscountRule> findByIdAndIsDeletedFalse(Integer id);
}
