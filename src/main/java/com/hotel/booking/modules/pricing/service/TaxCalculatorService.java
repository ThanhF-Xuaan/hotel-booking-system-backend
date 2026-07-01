package com.hotel.booking.modules.pricing.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TaxCalculatorService {
    BigDecimal calculateTax(Integer taxCategoryId, BigDecimal basePrice, LocalDate date);
    BigDecimal getTaxRate(Integer taxCategoryId, LocalDate date);
}
