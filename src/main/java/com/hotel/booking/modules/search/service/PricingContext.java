package com.hotel.booking.modules.search.service;

import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.pricing.entity.DiscountRule;
import com.hotel.booking.modules.pricing.entity.PricingRule;
import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import com.hotel.booking.modules.pricing.entity.TaxCategory;
import com.hotel.booking.modules.search.dto.request.PricingRequest;
import com.hotel.booking.modules.search.dto.request.RoomRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PricingContext {

    // Inputs
    HotelRoomType hotelRoomType;
    LocalDate date;
    PricingRequest request;
    long numberOfNights;

    // Configurations / Rules
    List<PricingRule> pricingRules;
    List<DiscountRule> discountRules;
    List<SurchargeRule> surchargeRules;
    TaxCategory taxCategory;

    // Running Prices & Amounts
    BigDecimal basePrice;
    BigDecimal priceAdjustment;
    BigDecimal calendarAdjustedPrice;
    BigDecimal discountAmount;
    BigDecimal discountedPrice;
    BigDecimal surchargeAmount;
    BigDecimal serviceFeeAmount;
    BigDecimal taxAmount;
    BigDecimal finalPrice;
    BigDecimal addOnAmount;
    BigDecimal addOnTaxAmount;
}
