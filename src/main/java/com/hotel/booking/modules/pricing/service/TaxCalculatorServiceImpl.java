package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.entity.VatRule;
import com.hotel.booking.modules.pricing.repository.VatRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaxCalculatorServiceImpl implements TaxCalculatorService {

    VatRuleRepository vatRuleRepository;

    @Override
    public BigDecimal calculateTax(Integer taxCategoryId, BigDecimal basePrice, LocalDate date) {
        if (taxCategoryId == null || basePrice == null || date == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = getTaxRate(taxCategoryId, date);
        return basePrice.multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getTaxRate(Integer taxCategoryId, LocalDate date) {
        if (taxCategoryId == null || date == null) {
            return BigDecimal.ZERO;
        }
        List<VatRule> activeRules = vatRuleRepository.findActiveVatRules(taxCategoryId, date);
        if (activeRules.isEmpty()) {
            log.warn("No active VAT rule found for tax category ID: {} on date: {}", taxCategoryId, date);
            return BigDecimal.ZERO;
        }
        // Take the first active rule (ordered by startDate DESC)
        return activeRules.get(0).getVatPercent();
    }
}
