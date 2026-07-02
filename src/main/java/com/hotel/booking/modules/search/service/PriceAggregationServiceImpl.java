package com.hotel.booking.modules.search.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.crm.enums.GuestType;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.pricing.entity.DiscountRule;
import com.hotel.booking.modules.pricing.entity.PricingRule;
import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import com.hotel.booking.modules.pricing.entity.TaxCategory;
import com.hotel.booking.modules.pricing.enums.AdjustmentType;
import com.hotel.booking.modules.pricing.enums.SurchargeRuleType;
import com.hotel.booking.modules.pricing.repository.DiscountRuleRepository;
import com.hotel.booking.modules.pricing.repository.PricingRuleRepository;
import com.hotel.booking.modules.pricing.repository.SurchargeRuleRepository;
import com.hotel.booking.modules.pricing.repository.TaxCategoryRepository;
import com.hotel.booking.modules.pricing.service.PricingEngineService;
import com.hotel.booking.modules.pricing.service.TaxCalculatorService;
import com.hotel.booking.modules.search.dto.request.PricingRequest;
import com.hotel.booking.modules.search.dto.request.RoomRequest;
import com.hotel.booking.modules.search.dto.request.RoomOccupancy;
import com.hotel.booking.modules.search.dto.response.PricingResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class PriceAggregationServiceImpl implements PriceAggregationService {

    HotelRoomTypeRepository hotelRoomTypeRepository;
    PricingRuleRepository pricingRuleRepository;
    DiscountRuleRepository discountRuleRepository;
    SurchargeRuleRepository surchargeRuleRepository;
    TaxCategoryRepository taxCategoryRepository;

    PricingEngineService pricingEngineService;
    TaxCalculatorService taxCalculatorService;

    @Override
    public PricingResponse calculatePrice(PricingRequest request) {
        try{
            log.info("Calculating flat-list room price breakdown for hotel: {}, checkIn: {}, checkOut: {}",
                    request.getHotelId(), request.getCheckIn(), request.getCheckOut());

            // Basic date validation
            if (request.getCheckIn().isBefore(LocalDate.now())) {
                throw new AppException(ErrorCode.INVALID_DATE_RANGE);
            }
            if (!request.getCheckOut().isAfter(request.getCheckIn())) {
                throw new AppException(ErrorCode.INVALID_DATE_RANGE);
            }

            List<RoomRequest> requestedRooms = request.getRooms();
            if (requestedRooms == null || requestedRooms.isEmpty()) {
                throw new AppException(ErrorCode.PRICING_ROOMS_NOT_EMPTY);
            }

            long numberOfNights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
            List<PriceProcessor> processors = buildProcessors();

            List<PricingResponse.RoomPricingResult> roomPricingResults = new ArrayList<>();
            BigDecimal grandBase = BigDecimal.ZERO;
            BigDecimal grandAdjustment = BigDecimal.ZERO;
            BigDecimal grandDiscount = BigDecimal.ZERO;
            BigDecimal grandSurcharge = BigDecimal.ZERO;
            BigDecimal grandServiceFee = BigDecimal.ZERO;
            BigDecimal grandTax = BigDecimal.ZERO;
            BigDecimal grandFinal = BigDecimal.ZERO;

            for (RoomRequest roomReq : requestedRooms) {
                if (roomReq.getHotelRoomTypeId() == null || roomReq.getOccupancy() == null) {
                    throw new AppException(ErrorCode.SEARCH_INVALID_ROOM_OCCUPANCY_CONFIGURATION);
                }

                HotelRoomType roomType = hotelRoomTypeRepository.findByIdAndIsDeletedFalse(roomReq.getHotelRoomTypeId())
                        .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

                if (!roomType.getHotel().getId().equals(request.getHotelId().shortValue())) {
                    throw new AppException(ErrorCode.ROOM_INSTANCE_HOTEL_MISMATCH);
                }

                // Capacity validation per room unit
                RoomOccupancy occ = roomReq.getOccupancy();
                int totalGuests = occ.getAdults() + (occ.getChildren() != null ? occ.getChildren() : 0);
                if (totalGuests > roomType.getMaxTotalGuests()) {
                    throw new AppException(ErrorCode.ROOM_CAPACITY_EXCEEDED);
                }

                if (occ.getAdults() != null && occ.getAdults() > roomType.getMaxAdults()) {
                    log.warn("Validation failed: Adults {} > MaxAdults {}", occ.getAdults(), roomType.getMaxAdults());
                    throw new AppException(ErrorCode.ROOM_ADULT_CAPACITY_EXCEEDED);
                }

                if (occ.getChildren() != null && occ.getChildren() > roomType.getMaxChildren()) {
                    log.warn("Validation failed: Children {} > MaxChildren {}", occ.getChildren(), roomType.getMaxChildren());
                    throw new AppException(ErrorCode.ROOM_CHILD_CAPACITY_EXCEEDED);
                }

                if (occ.getInfants() != null && occ.getInfants() > roomType.getMaxInfants()) {
                    log.warn("Validation failed: Infants {} > MaxInfants {}", occ.getInfants(), roomType.getMaxInfants());
                    throw new AppException(ErrorCode.ROOM_INFANT_CAPACITY_EXCEEDED);
                }

                // Fetch rules and tax category for this room type
                List<PricingRule> pricingRules = pricingRuleRepository
                        .findAllByHotelRoomTypeIdAndIsDeletedFalse(roomType.getId());
                List<DiscountRule> discountRules = discountRuleRepository
                        .findAllByHotelRoomTypeIdAndIsDeletedFalse(roomType.getId());
                List<SurchargeRule> surchargeRules = surchargeRuleRepository
                        .findAllByHotelRoomTypeIdAndIsDeletedFalse(roomType.getId());

                TaxCategory roomTaxCategory = taxCategoryRepository.findAllByIsDeletedFalse().stream()
                        .filter(tc -> "ROOM".equalsIgnoreCase(tc.getCategoryCode()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.TAX_CATEGORY_NOT_FOUND));

                // Create a virtual PricingRequest representing this specific room unit
                RoomRequest virtualRoom = RoomRequest.builder()
                        .hotelRoomTypeId(roomReq.getHotelRoomTypeId())
                        .occupancy(occ)
                        .build();

                PricingRequest virtualRequest = PricingRequest.builder()
                        .hotelId(request.getHotelId())
                        .checkIn(request.getCheckIn())
                        .checkOut(request.getCheckOut())
                        .rooms(List.of(virtualRoom))
                        .build();

                BigDecimal roomBase = BigDecimal.ZERO;
                BigDecimal roomAdjustment = BigDecimal.ZERO;
                BigDecimal roomDiscount = BigDecimal.ZERO;
                BigDecimal roomSurcharge = BigDecimal.ZERO;
                BigDecimal roomServiceFee = BigDecimal.ZERO;
                BigDecimal roomTax = BigDecimal.ZERO;
                BigDecimal roomFinal = BigDecimal.ZERO;

                for (LocalDate date = request.getCheckIn(); date.isBefore(request.getCheckOut()); date = date.plusDays(1)) {
                    PricingContext context = PricingContext.builder()
                            .hotelRoomType(roomType)
                            .date(date)
                            .request(virtualRequest)
                            .numberOfNights(numberOfNights)
                            .pricingRules(pricingRules)
                            .discountRules(discountRules)
                            .surchargeRules(surchargeRules)
                            .taxCategory(roomTaxCategory)
                            .basePrice(BigDecimal.ZERO)
                            .priceAdjustment(BigDecimal.ZERO)
                            .calendarAdjustedPrice(BigDecimal.ZERO)
                            .discountAmount(BigDecimal.ZERO)
                            .discountedPrice(BigDecimal.ZERO)
                            .surchargeAmount(BigDecimal.ZERO)
                            .serviceFeeAmount(BigDecimal.ZERO)
                            .taxAmount(BigDecimal.ZERO)
                            .finalPrice(BigDecimal.ZERO)
                            .build();

                    for (PriceProcessor processor : processors) {
                        processor.process(context);
                    }

                    roomBase = roomBase.add(context.getBasePrice());
                    roomAdjustment = roomAdjustment.add(context.getPriceAdjustment());
                    roomDiscount = roomDiscount.add(context.getDiscountAmount());
                    roomSurcharge = roomSurcharge.add(context.getSurchargeAmount());
                    roomServiceFee = roomServiceFee.add(context.getServiceFeeAmount());
                    roomTax = roomTax.add(context.getTaxAmount());
                    roomFinal = roomFinal.add(context.getFinalPrice());
                }

                PricingResponse.PriceDetail priceDetail = PricingResponse.PriceDetail.builder()
                        .basePrice(roomBase)
                        .priceAdjustment(roomAdjustment)
                        .discountAmount(roomDiscount)
                        .surchargeAmount(roomSurcharge)
                        .serviceFeeAmount(roomServiceFee)
                        .taxAmount(roomTax)
                        .finalPrice(roomFinal)
                        .build();

                roomPricingResults.add(PricingResponse.RoomPricingResult.builder()
                        .hotelRoomTypeId(roomReq.getHotelRoomTypeId())
                        .occupancy(occ)
                        .priceDetail(priceDetail)
                        .build());

                grandBase = grandBase.add(roomBase);
                grandAdjustment = grandAdjustment.add(roomAdjustment);
                grandDiscount = grandDiscount.add(roomDiscount);
                grandSurcharge = grandSurcharge.add(roomSurcharge);
                grandServiceFee = grandServiceFee.add(roomServiceFee);
                grandTax = grandTax.add(roomTax);
                grandFinal = grandFinal.add(roomFinal);
            }

            PricingResponse.PriceDetail grandTotal = PricingResponse.PriceDetail.builder()
                    .basePrice(grandBase)
                    .priceAdjustment(grandAdjustment)
                    .discountAmount(grandDiscount)
                    .surchargeAmount(grandSurcharge)
                    .serviceFeeAmount(grandServiceFee)
                    .taxAmount(grandTax)
                    .finalPrice(grandFinal)
                    .build();

            return PricingResponse.builder()
                    .rooms(roomPricingResults)
                    .grandTotal(grandTotal)
                    .build();
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Có lỗi xảy ra trong quá trình xử lý (PriceAggregationServiceImpl - module search): {}", e.getMessage(), e);
            throw new AppException(ErrorCode.PRICING_CALCULATION_FAILED, e);
        }
    }

    private List<PriceProcessor> buildProcessors() {
        return List.of(
                // Step 1: Base Price Setup
                context -> context.setBasePrice(context.getHotelRoomType().getBasePrice()),

                // Step 2: Calendar Adjustments (Holiday / Weekend)
                context -> {
                    List<PricingRule> activeRules = context.getPricingRules().stream()
                            .filter(rule -> ActiveStatus.ACTIVE == rule.getStatus()
                                    && !rule.getStartDate().isAfter(context.getDate())
                                    && !rule.getEndDate().isBefore(context.getDate()))
                            .toList();

                    var winningRuleOpt = pricingEngineService.resolveWinningRule(activeRules, context.getBasePrice());
                    if (winningRuleOpt.isPresent()) {
                        PricingRule rule = winningRuleOpt.get();
                        BigDecimal adjustment;
                        if (rule.getAdjustmentType() == AdjustmentType.FIXED) {
                            adjustment = rule.getAdjustmentValue();
                        } else {
                            adjustment = context.getBasePrice().multiply(rule.getAdjustmentValue())
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        }
                        context.setPriceAdjustment(adjustment);
                        context.setCalendarAdjustedPrice(context.getBasePrice().add(adjustment));
                    } else {
                        context.setPriceAdjustment(BigDecimal.ZERO);
                        context.setCalendarAdjustedPrice(context.getBasePrice());
                    }
                },

                // Step 3: Discount Evaluation (Applies on room price: basePrice +
                // priceAdjustment)
                context -> {
                    long advanceDays = ChronoUnit.DAYS.between(LocalDate.now(), context.getRequest().getCheckIn());
                    List<DiscountRule> matchingRules = context.getDiscountRules().stream()
                            .filter(rule -> ActiveStatus.ACTIVE == rule.getStatus()
                                    && !rule.getStartDate().isAfter(context.getDate())
                                    && !rule.getEndDate().isBefore(context.getDate()))
                            .filter(rule -> {
                                var cond = rule.getConditions();
                                if (cond == null) {
                                    return true;
                                }
                                if (cond.getPromoCode() != null && !cond.getPromoCode().isBlank()) {
                                    return false; // Skip promo codes for search queries
                                }
                                if (cond.getMinNights() != null && context.getNumberOfNights() < cond.getMinNights()) {
                                    return false;
                                }
                                if (cond.getMaxNights() != null && context.getNumberOfNights() > cond.getMaxNights()) {
                                    return false;
                                }
                                if (cond.getMinAdvanceBookingDays() != null
                                        && advanceDays < cond.getMinAdvanceBookingDays()) {
                                    return false;
                                }
                                return true;
                            })
                            .toList();

                    // Calculate discount amount for each matching rule based on
                    // calendarAdjustedPrice
                    class DiscountEvaluationContext {
                        final DiscountRule rule;
                        final BigDecimal benefit;

                        DiscountEvaluationContext(DiscountRule rule, BigDecimal benefit) {
                            this.rule = rule;
                            this.benefit = benefit;
                        }

                        BigDecimal getBenefit() {
                            return benefit;
                        }

                        short getPriority() {
                            return rule.getRuleType() != null && rule.getRuleType().getPriority() != null
                                    ? rule.getRuleType().getPriority()
                                    : 0;
                        }
                    }

                    var winningDiscountOpt = matchingRules.stream()
                            .map(rule -> {
                                BigDecimal discountAmount;
                                if (rule.getDiscountType() == AdjustmentType.FIXED) {
                                    discountAmount = rule.getDiscountValue();
                                } else {
                                    discountAmount = context.getCalendarAdjustedPrice()
                                            .multiply(rule.getDiscountValue())
                                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                                }
                                return new DiscountEvaluationContext(rule, discountAmount);
                            })
                            .max(java.util.Comparator.comparing(DiscountEvaluationContext::getBenefit)
                                    .thenComparing(DiscountEvaluationContext::getPriority));

                    if (winningDiscountOpt.isPresent()) {
                        BigDecimal maxDiscount = winningDiscountOpt.get().getBenefit();
                        context.setDiscountAmount(maxDiscount);
                        context.setDiscountedPrice(context.getCalendarAdjustedPrice().subtract(maxDiscount));
                    } else {
                        context.setDiscountAmount(BigDecimal.ZERO);
                        context.setDiscountedPrice(context.getCalendarAdjustedPrice());
                    }
                },

                // Step 4: Surcharge Assessment (Extra Person)
                context -> {
                    List<SurchargeRule> activeRules = context.getSurchargeRules().stream()
                            .filter(rule -> (rule.getStatus() == null || ActiveStatus.ACTIVE == rule.getStatus())
                                    && !rule.getStartDate().isAfter(context.getDate())
                                    && !rule.getEndDate().isBefore(context.getDate())
                                    && rule.getRuleType() == SurchargeRuleType.EXTRA_PERSON)
                            .toList();

                    log.info("Surcharge assessment: loaded {} active EXTRA_PERSON rules for date {}",
                            activeRules.size(), context.getDate());

                    BigDecimal dailySurcharge = BigDecimal.ZERO;
                    var roomType = context.getHotelRoomType();
                    var roomRequest = context.getRequest().getRooms().get(0);
                    var roomOccupancy = roomRequest.getOccupancy();
                    int extraAdults = Math.max(0, roomOccupancy.getAdults() - (roomType.getStandardAdults() != null ? roomType.getStandardAdults() : 0));
                    int extraChildren = Math.max(0, (roomOccupancy.getChildren() != null ? roomOccupancy.getChildren() : 0) - (roomType.getStandardChildren() != null ? roomType.getStandardChildren() : 0));
                    int extraInfants = roomOccupancy.getInfants() != null ? roomOccupancy.getInfants() : 0;

                    for (SurchargeRule rule : activeRules) {
                        var policy = rule.getAgePolicy();
                        int extraCount = 0;
                        if (policy == null) {
                            // General rule: applies to extra adults and children
                            extraCount = extraAdults + extraChildren;
                            log.info("Rule ID {}: general policy (null) applied. extraCount = {}", rule.getId(),
                                    extraCount);
                        } else {
                            try {
                                GuestType type = GuestType.valueOf(policy.getGuestType().toUpperCase());
                                switch (type) {
                                    case ADULT -> extraCount = extraAdults;
                                    case CHILD -> extraCount = extraChildren;
                                    case INFANT -> extraCount = extraInfants;
                                    default -> {
                                        extraCount = extraAdults + extraChildren;
                                        log.warn(
                                                "Rule ID {}: unknown enum guestType {}, falling back to sum of extra adults and children",
                                                rule.getId(), type);
                                    }
                                }
                                log.info(
                                        "Rule ID {}: policy type {} matched. extraCount = {} (adults={}, children={}, infants={})",
                                        rule.getId(), type, extraCount, extraAdults, extraChildren, extraInfants);
                            } catch (IllegalArgumentException | NullPointerException e) {
                                extraCount = extraAdults + extraChildren;
                                log.warn(
                                        "Rule ID {}: failed to parse guestType '{}' or agePolicy is invalid, falling back to sum of extra adults and children.",
                                        rule.getId(), policy.getGuestType());
                            }
                        }

                        if (extraCount > 0) {
                            BigDecimal singleSurcharge;
                            if (rule.getAdjustmentType() == AdjustmentType.FIXED) {
                                singleSurcharge = rule.getAdjustmentValue();
                            } else {
                                singleSurcharge = context.getBasePrice().multiply(rule.getAdjustmentValue())
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                            }
                            BigDecimal calculatedSurcharge = singleSurcharge.multiply(BigDecimal.valueOf(extraCount));
                            dailySurcharge = dailySurcharge.add(calculatedSurcharge);
                            log.info("Rule ID {}: single surcharge = {}, calculated surcharge for {} guests = {}",
                                    rule.getId(), singleSurcharge, extraCount, calculatedSurcharge);
                        }
                    }
                    context.setSurchargeAmount(dailySurcharge);
                },

                // Step 5: Service Fee Calculation
                // Subtotal = (Base + Adjustment) - Discount + Surcharge
                context -> {
                    BigDecimal subtotal = context.getDiscountedPrice().add(context.getSurchargeAmount());
                    BigDecimal feePercent = context.getHotelRoomType().getHotel().getServiceFeePercent();
                    if (feePercent == null) {
                        feePercent = BigDecimal.ZERO;
                    }
                    BigDecimal feeAmount = subtotal.multiply(feePercent)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    context.setServiceFeeAmount(feeAmount);
                },

                // Step 6: VAT Calculation
                context -> {
                    BigDecimal subtotal = context.getDiscountedPrice().add(context.getSurchargeAmount());
                    BigDecimal taxableAmount = subtotal.add(context.getServiceFeeAmount());
                    BigDecimal vatPercent = taxCalculatorService.getTaxRate(context.getTaxCategory().getId(),
                            context.getDate());
                    BigDecimal taxAmount = taxableAmount.multiply(vatPercent)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    context.setTaxAmount(taxAmount);
                    context.setFinalPrice(taxableAmount.add(taxAmount));
                });
    }
}
