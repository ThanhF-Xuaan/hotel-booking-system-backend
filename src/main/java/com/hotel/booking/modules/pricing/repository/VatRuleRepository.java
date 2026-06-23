package com.hotel.booking.modules.pricing.repository;

import com.hotel.booking.modules.pricing.entity.VatRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VatRuleRepository extends JpaRepository<VatRule, Integer> {
    List<VatRule> findAllByIsDeletedFalse();
    Optional<VatRule> findByIdAndIsDeletedFalse(Integer id);
    boolean existsByVatCodeAndIsDeletedFalse(String vatCode);
    boolean existsByVatNameAndIsDeletedFalse(String vatName);
    boolean existsByVatCodeAndIdNotAndIsDeletedFalse(String vatCode, Integer id);
    boolean existsByVatNameAndIdNotAndIsDeletedFalse(String vatName, Integer id);
}
