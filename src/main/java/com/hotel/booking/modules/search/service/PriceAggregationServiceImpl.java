package com.hotel.booking.modules.search.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeCatalogItemRepository;
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
    HotelRoomTypeCatalogItemRepository hotelRoomTypeCatalogItemRepository;

    PricingEngineService pricingEngineService;
    TaxCalculatorService taxCalculatorService;

    @Override
    public PricingResponse calculatePrice(PricingRequest request) {
        try {
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
            BigDecimal grandAddOn = BigDecimal.ZERO; // BỔ SUNG ADD ON AMOUNT
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

                //Chống NPE do Auto-Unboxing
                int reqAdults = occ.getAdults() != null ? occ.getAdults() : 0;
                int reqChildren = occ.getChildren() != null ? occ.getChildren() : 0;
                int reqInfants = occ.getInfants() != null ? occ.getInfants() : 0;

                int totalGuests = reqAdults + reqChildren;

                if (totalGuests > roomType.getMaxTotalGuests()) {
                    throw new AppException(ErrorCode.ROOM_CAPACITY_EXCEEDED);
                }

                if (reqAdults > roomType.getMaxAdults()) {
                    log.warn("Validation failed: Adults {} > MaxAdults {}", reqAdults, roomType.getMaxAdults());
                    throw new AppException(ErrorCode.ROOM_ADULT_CAPACITY_EXCEEDED);
                }

                if (reqChildren > roomType.getMaxChildren()) {
                    log.warn("Validation failed: Children {} > MaxChildren {}", reqChildren, roomType.getMaxChildren());
                    throw new AppException(ErrorCode.ROOM_CHILD_CAPACITY_EXCEEDED);
                }

                if (reqInfants > roomType.getMaxInfants()) {
                    log.warn("Validation failed: Infants {} > MaxInfants {}", reqInfants, roomType.getMaxInfants());
                    throw new AppException(ErrorCode.ROOM_INFANT_CAPACITY_EXCEEDED);
                }

                // Fetch rules and tax category for this room type
                List<PricingRule> pricingRules = pricingRuleRepository
                        .findAllByHotelRoomTypeIdAndIsDeletedFalse(roomType.getId());
                List<DiscountRule> discountRules = discountRuleRepository
                        .findAllByHotelRoomTypeIdAndIsDeletedFalse(roomType.getId());
                List<SurchargeRule> surchargeRules = surchargeRuleRepository
                        .findAllByHotelRoomTypeIdAndIsDeletedFalse(roomType.getId());

                TaxCategory roomTaxCategory = taxCategoryRepository
                        .findByCategoryCodeAndIsDeletedFalse("ROOM")
                        .orElseThrow(() -> new AppException(ErrorCode.TAX_CATEGORY_NOT_FOUND));


                // Create a virtual PricingRequest representing this specific room unit
                RoomRequest virtualRoom = RoomRequest.builder()
                        .hotelRoomTypeId(roomReq.getHotelRoomTypeId())
                        .occupancy(occ)
                        .addOns(roomReq.getAddOns())
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
                BigDecimal roomAddOnAmount = BigDecimal.ZERO; // BỔ SUNG ADD ON AMOUNT
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
                            .addOnAmount(BigDecimal.ZERO) // Khởi tạo Zero
                            .addOnTaxAmount(BigDecimal.ZERO) // Khởi tạo Zero
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
                    roomAddOnAmount = roomAddOnAmount.add(context.getAddOnAmount()); // CỘNG DỒN ADD ON
                    roomServiceFee = roomServiceFee.add(context.getServiceFeeAmount());
                    roomTax = roomTax.add(context.getTaxAmount());
                    roomFinal = roomFinal.add(context.getFinalPrice());
                }

                // Gán đúng biến addOnAmount vào PriceDetail
                PricingResponse.PriceDetail priceDetail = PricingResponse.PriceDetail.builder()
                        .basePrice(roomBase)
                        .priceAdjustment(roomAdjustment)
                        .discountAmount(roomDiscount)
                        .surchargeAmount(roomSurcharge)
                        .addOnAmount(roomAddOnAmount) // TRUYỀN VÀO BUILDER
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
                grandAddOn = grandAddOn.add(roomAddOnAmount); // CỘNG DỒN GRAND ADD ON
                grandServiceFee = grandServiceFee.add(roomServiceFee);
                grandTax = grandTax.add(roomTax);
                grandFinal = grandFinal.add(roomFinal);
            }

            PricingResponse.PriceDetail grandTotal = PricingResponse.PriceDetail.builder()
                    .basePrice(grandBase)
                    .priceAdjustment(grandAdjustment)
                    .discountAmount(grandDiscount)
                    .surchargeAmount(grandSurcharge)
                    .addOnAmount(grandAddOn) // TRUYỀN VÀO BUILDER TỔNG
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
                // ==============================================================
                // Step 1: Setup giá gốc hạng phòng
                // ==============================================================
                context -> context.setBasePrice(context.getHotelRoomType().getBasePrice()),

                // ==============================================================
                // Step 2: Điều chỉnh giá theo lịch (Ngày lễ / Cuối tuần)
                // ==============================================================
                context -> {
                    List<PricingRule> activeRules = context.getPricingRules().stream()
                            .filter(rule -> {
                                String statusStr = rule.getStatus() != null ? rule.getStatus().toString() : "";
                                return "ACTIVE".equalsIgnoreCase(statusStr)
                                        && !rule.getStartDate().isAfter(context.getDate())
                                        && !rule.getEndDate().isBefore(context.getDate());
                            })
                            .toList();

                    var winningRuleOpt = pricingEngineService.resolveWinningRule(activeRules, context.getBasePrice());
                    if (winningRuleOpt.isPresent()) {
                        PricingRule rule = winningRuleOpt.get();
                        BigDecimal adjustment;
                        String adjTypeName = rule.getAdjustmentType() != null ? rule.getAdjustmentType().toString() : "";
                        if ("FIXED".equalsIgnoreCase(adjTypeName)) {
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

                // ==============================================================
                // Step 3: Tính toán giảm giá (Discount)
                // ==============================================================
                context -> {
                    long advanceDays = ChronoUnit.DAYS.between(LocalDate.now(), context.getRequest().getCheckIn());
                    List<DiscountRule> matchingRules = context.getDiscountRules().stream()
                            .filter(rule -> {
                                String statusStr = rule.getStatus() != null ? rule.getStatus().toString() : "";
                                return "ACTIVE".equalsIgnoreCase(statusStr)
                                        && !rule.getStartDate().isAfter(context.getDate())
                                        && !rule.getEndDate().isBefore(context.getDate());
                            })
                            .filter(rule -> {
                                var cond = rule.getConditions();
                                if (cond == null) return true;
                                if (cond.getPromoCode() != null && !cond.getPromoCode().isBlank()) return false;
                                if (cond.getMinNights() != null && context.getNumberOfNights() < cond.getMinNights()) return false;
                                if (cond.getMaxNights() != null && context.getNumberOfNights() > cond.getMaxNights()) return false;
                                if (cond.getMinAdvanceBookingDays() != null && advanceDays < cond.getMinAdvanceBookingDays()) return false;
                                return true;
                            })
                            .toList();

                    class DiscountEvaluationContext {
                        final DiscountRule rule;
                        final BigDecimal benefit;
                        DiscountEvaluationContext(DiscountRule rule, BigDecimal benefit) {
                            this.rule = rule;
                            this.benefit = benefit;
                        }
                        BigDecimal getBenefit() { return benefit; }
                        short getPriority() {
                            return rule.getRuleType() != null && rule.getRuleType().getPriority() != null
                                    ? rule.getRuleType().getPriority() : 0;
                        }
                    }

                    var winningDiscountOpt = matchingRules.stream()
                            .map(rule -> {
                                BigDecimal discountAmount;
                                String discTypeName = rule.getDiscountType() != null ? rule.getDiscountType().toString() : "";
                                if ("FIXED".equalsIgnoreCase(discTypeName)) {
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

                // ==============================================================
                // Step 4: Phụ thu thêm người (CẢ NGƯỜI LỚN VÀ TRẺ EM - STRICT MODE)
                // ==============================================================
                context -> {
                    BigDecimal dailySurcharge = BigDecimal.ZERO;
                    RoomOccupancy occ = context.getRequest().getRooms().get(0).getOccupancy();

                    // -----------------------------------------------------
                    // 1. XỬ LÝ PHỤ THU NGƯỜI LỚN (ADULT - CHECK HẲN HOI)
                    // -----------------------------------------------------
                    int standardAdults = context.getHotelRoomType().getStandardAdults();
                    int requestedAdults = occ.getAdults() != null ? occ.getAdults() : 0; // Chống NPE

                    if (requestedAdults > standardAdults) {
                        int extraAdults = requestedAdults - standardAdults;

                        var extraAdultRuleOpt = context.getSurchargeRules().stream()
                                .filter(rule -> {
                                    String ruleTypeStr = rule.getRuleType() != null ? rule.getRuleType().toString() : "";
                                    String statusStr = rule.getStatus() != null ? rule.getStatus().toString() : "";

                                    boolean matchesType = "EXTRA_PERSON".equalsIgnoreCase(ruleTypeStr);
                                    boolean isActive = "ACTIVE".equalsIgnoreCase(statusStr);
                                    boolean isCurrentDate = !rule.getStartDate().isAfter(context.getDate()) && !rule.getEndDate().isBefore(context.getDate());

                                    //Bắt buộc CÓ AgePolicy VÀ guestType = ADULT
                                    boolean isAdultPolicy = rule.getAgePolicy() != null
                                            && rule.getAgePolicy().getGuestType() != null
                                            && "ADULT".equalsIgnoreCase(rule.getAgePolicy().getGuestType().toString());

                                    return matchesType && isActive && isCurrentDate && isAdultPolicy;
                                })
                                .findFirst();

                        if (extraAdultRuleOpt.isPresent()) {
                            SurchargeRule rule = extraAdultRuleOpt.get();
                            BigDecimal chargePerAdult;

                            String adjTypeName = rule.getAdjustmentType() != null ? rule.getAdjustmentType().toString() : "";
                            if ("FIXED".equalsIgnoreCase(adjTypeName)) {
                                chargePerAdult = rule.getAdjustmentValue();
                            } else {
                                chargePerAdult = context.getHotelRoomType().getBasePrice()
                                        .multiply(rule.getAdjustmentValue())
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                            }
                            dailySurcharge = dailySurcharge.add(chargePerAdult.multiply(BigDecimal.valueOf(extraAdults)));
                        }
                    }

                    // -----------------------------------------------------
                    // 2. XỬ LÝ PHỤ THU TRẺ EM (CHILD)
                    // -----------------------------------------------------
                    int standardChildren = context.getHotelRoomType().getStandardChildren();
                    int requestedChildren = occ.getChildren() != null ? occ.getChildren() : 0; // Chống NPE

                    if (requestedChildren > standardChildren) {
                        int extraChildren = requestedChildren - standardChildren;

                        var extraChildRuleOpt = context.getSurchargeRules().stream()
                                .filter(rule -> {
                                    String ruleTypeStr = rule.getRuleType() != null ? rule.getRuleType().toString() : "";
                                    String statusStr = rule.getStatus() != null ? rule.getStatus().toString() : "";

                                    boolean matchesType = "EXTRA_PERSON".equalsIgnoreCase(ruleTypeStr);
                                    boolean isActive = "ACTIVE".equalsIgnoreCase(statusStr);
                                    boolean isCurrentDate = !rule.getStartDate().isAfter(context.getDate()) && !rule.getEndDate().isBefore(context.getDate());

                                    //Bắt buộc CÓ AgePolicy VÀ guestType = CHILD
                                    boolean isChildPolicy = rule.getAgePolicy() != null
                                            && rule.getAgePolicy().getGuestType() != null
                                            && "CHILD".equalsIgnoreCase(rule.getAgePolicy().getGuestType().toString());

                                    return matchesType && isActive && isCurrentDate && isChildPolicy;
                                })
                                .findFirst();

                        if (extraChildRuleOpt.isPresent()) {
                            SurchargeRule rule = extraChildRuleOpt.get();
                            BigDecimal chargePerChild;

                            String adjTypeName = rule.getAdjustmentType() != null ? rule.getAdjustmentType().toString() : "";
                            if ("FIXED".equalsIgnoreCase(adjTypeName)) {
                                chargePerChild = rule.getAdjustmentValue();
                            } else {
                                chargePerChild = context.getHotelRoomType().getBasePrice()
                                        .multiply(rule.getAdjustmentValue())
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                            }
                            dailySurcharge = dailySurcharge.add(chargePerChild.multiply(BigDecimal.valueOf(extraChildren)));
                        }
                    }

                    context.setSurchargeAmount(dailySurcharge);
                },

                // ==============================================================
                // Step 4.5: Tính toán dịch vụ đi kèm (Add-ons / Package Items)
                // ==============================================================
                context -> {
                    BigDecimal dailyAddOn = BigDecimal.ZERO;
                    BigDecimal dailyAddOnTax = BigDecimal.ZERO;

                    // Lấy % Phí dịch vụ của khách sạn
                    BigDecimal feePercent = context.getHotelRoomType().getHotel().getServiceFeePercent();
                    if (feePercent == null) feePercent = BigDecimal.ZERO;

                    var mappedItems = hotelRoomTypeCatalogItemRepository
                            .findAllByHotelRoomTypeId(context.getHotelRoomType().getId());

                    boolean isFirstDay = context.getDate().equals(context.getRequest().getCheckIn());

                    // A. Xử lý hàng bắt buộc (MANDATORY)
                    for (var mapped : mappedItems) {
                        String itemUsageStr = mapped.getItemUsage() != null ? mapped.getItemUsage().toString() : "";
                        if ("MANDATORY".equalsIgnoreCase(itemUsageStr)) {
                            String pricingTypeStr = mapped.getPricingType() != null ? mapped.getPricingType().toString() : "";
                            boolean isPerNight = "PER_NIGHT".equalsIgnoreCase(pricingTypeStr);

                            if (isPerNight || isFirstDay) {
                                BigDecimal itemPrice = mapped.getPrice();
                                dailyAddOn = dailyAddOn.add(itemPrice);

                                // Tính Phí dịch vụ riêng cho Item này
                                BigDecimal itemServiceFee = itemPrice.multiply(feePercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                                BigDecimal itemTaxableAmount = itemPrice.add(itemServiceFee);

                                // Dùng TaxCalculatorService tính Thuế chuẩn xác cho Item này
                                BigDecimal itemTax = taxCalculatorService.calculateTax(
                                        mapped.getCatalogItem().getTaxCategory().getId(),
                                        itemTaxableAmount,
                                        context.getDate()
                                );
                                dailyAddOnTax = dailyAddOnTax.add(itemTax);
                            }
                        }
                    }

                    // B. Xử lý hàng tự chọn (OPTIONAL)
                    if (context.getRequest().getRooms().get(0).getAddOns() != null) {
                        for (RoomRequest.SelectedAddOn reqAddOn : context.getRequest().getRooms().get(0).getAddOns()) {
                            for (var mapped : mappedItems) {
                                String itemUsageStr = mapped.getItemUsage() != null ? mapped.getItemUsage().toString() : "";
                                boolean isSameId = mapped.getCatalogItem().getId().longValue() == reqAddOn.getCatalogItemId().longValue();

                                if (isSameId && "OPTIONAL".equalsIgnoreCase(itemUsageStr)) {
                                    String pricingTypeStr = mapped.getPricingType() != null ? mapped.getPricingType().toString() : "";
                                    boolean isPerNight = "PER_NIGHT".equalsIgnoreCase(pricingTypeStr);

                                    if (isPerNight || isFirstDay) {
                                        BigDecimal itemPrice = mapped.getPrice().multiply(BigDecimal.valueOf(reqAddOn.getQuantity()));
                                        dailyAddOn = dailyAddOn.add(itemPrice);

                                        // Tương tự, tính thuế gánh cả phí dịch vụ của Item
                                        BigDecimal itemServiceFee = itemPrice.multiply(feePercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                                        BigDecimal itemTaxableAmount = itemPrice.add(itemServiceFee);

                                        BigDecimal itemTax = taxCalculatorService.calculateTax(
                                                mapped.getCatalogItem().getTaxCategory().getId(),
                                                itemTaxableAmount,
                                                context.getDate()
                                        );
                                        dailyAddOnTax = dailyAddOnTax.add(itemTax);
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    context.setAddOnAmount(dailyAddOn);
                    context.setAddOnTaxAmount(dailyAddOnTax); // Đã tính xong thuế rất rạch ròi
                },

                // ==============================================================
                // Step 5: Tính phí phục vụ (Service Fee)
                // ==============================================================
                context -> {
                    BigDecimal roomAndSurcharge = context.getDiscountedPrice().add(context.getSurchargeAmount());
                    BigDecimal addOnAmount = context.getAddOnAmount();

                    BigDecimal feePercent = context.getHotelRoomType().getHotel().getServiceFeePercent();
                    if (feePercent == null) feePercent = BigDecimal.ZERO;

                    // 1. Phí dịch vụ của riêng Phòng
                    BigDecimal roomFee = roomAndSurcharge.multiply(feePercent)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    // 2. Phí dịch vụ của khối Add-ons
                    BigDecimal addOnFee = addOnAmount.multiply(feePercent)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    // 3. Cộng dồn để đảm bảo KHÔNG BAO GIỜ LỆCH so với gốc tính Thuế
                    context.setServiceFeeAmount(roomFee.add(addOnFee));
                },

                // ==============================================================
                // Step 6: Tính toán thuế VAT tổng và chốt giá Final
                // ==============================================================
                context -> {
                    BigDecimal roomAndSurcharge = context.getDiscountedPrice().add(context.getSurchargeAmount());

                    BigDecimal feePercent = context.getHotelRoomType().getHotel().getServiceFeePercent();
                    if (feePercent == null) feePercent = BigDecimal.ZERO;

                    // Tính lại đúng cục Phí Dịch Vụ của Phòng (Trùng khớp 100% với Step 5)
                    BigDecimal roomServiceFee = roomAndSurcharge.multiply(feePercent)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    // Số tiền phòng chịu thuế = Tiền phòng + Phí dịch vụ của phòng
                    BigDecimal roomTaxableAmount = roomAndSurcharge.add(roomServiceFee);

                    // Tính Thuế Phòng bằng TaxCalculatorService siêu việt
                    BigDecimal roomTax = taxCalculatorService.calculateTax(
                            context.getTaxCategory().getId(),
                            roomTaxableAmount,
                            context.getDate()
                    );

                    // TỔNG THUẾ = Thuế Phòng + Thuế Add-ons (đã tính xong ở Step 4.5)
                    BigDecimal totalTaxAmount = roomTax.add(context.getAddOnTaxAmount());
                    context.setTaxAmount(totalTaxAmount);

                    // TỔNG CỘNG FINAL: Cộng đúng 5 cục lại với nhau
                    BigDecimal finalPrice = context.getDiscountedPrice()
                            .add(context.getSurchargeAmount())
                            .add(context.getAddOnAmount())
                            .add(context.getServiceFeeAmount()) // Đã chuẩn toán học từ Step 5
                            .add(totalTaxAmount);

                    context.setFinalPrice(finalPrice);
                }
        );
    }
}