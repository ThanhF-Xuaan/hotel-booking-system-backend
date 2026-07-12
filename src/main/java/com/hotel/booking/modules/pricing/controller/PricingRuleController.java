package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.PricingRuleResponse;
import com.hotel.booking.modules.pricing.service.PricingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pricing/pricing-rules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Pricing Rule Management", description = "Các API quản lý Quy tắc giá (PricingRule)")
public class PricingRuleController {

    PricingRuleService pricingRuleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới quy tắc giá", description = "Cấu hình quy tắc điều chỉnh giá cho một loại phòng trong một khoảng thời gian.")
    public ApiResponse<PricingRuleResponse> createPricingRule(@Valid @RequestBody PricingRuleCreateRequest request) {
        return ApiResponse.<PricingRuleResponse>builder()
                .result(pricingRuleService.createPricingRule(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách quy tắc giá", description = "Lấy toàn bộ các quy tắc giá đang hoạt động. Có thể lọc theo hotelRoomTypeId.")
    public ApiResponse<List<PricingRuleResponse>> getPricingRules(
            @RequestParam(required = false) Integer hotelRoomTypeId) {
        return ApiResponse.<List<PricingRuleResponse>>builder()
                .result(pricingRuleService.getPricingRules(hotelRoomTypeId))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết quy tắc giá", description = "Trả về thông tin chi tiết của quy tắc giá theo ID.")
    public ApiResponse<PricingRuleResponse> getPricingRuleById(@PathVariable Integer id) {
        return ApiResponse.<PricingRuleResponse>builder()
                .result(pricingRuleService.getPricingRuleById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật quy tắc giá", description = "Cập nhật thông tin quy tắc giá hiện có.")
    public ApiResponse<PricingRuleResponse> updatePricingRule(
            @PathVariable Integer id,
            @Valid @RequestBody PricingRuleUpdateRequest request) {
        return ApiResponse.<PricingRuleResponse>builder()
                .result(pricingRuleService.updatePricingRule(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm quy tắc giá", description = "Set isDeleted = true cho quy tắc giá.")
    public ApiResponse<Void> deletePricingRule(@PathVariable Integer id) {
        pricingRuleService.deletePricingRule(id);
        return ApiResponse.<Void>builder()
                .message("Pricing rule deleted successfully")
                .build();
    }
}
