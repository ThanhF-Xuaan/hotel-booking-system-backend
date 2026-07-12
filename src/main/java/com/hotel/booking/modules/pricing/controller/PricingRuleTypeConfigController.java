package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.PricingRuleTypeResponse;
import com.hotel.booking.modules.pricing.service.PricingRuleTypeConfigService;
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
@RequestMapping("/api/v1/pricing/pricing-rule-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Pricing Rule Type Config Management", description = "Các API quản lý cấu hình loại quy tắc giá (PricingRuleTypeConfig)")
public class PricingRuleTypeConfigController {

    PricingRuleTypeConfigService pricingRuleTypeConfigService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới cấu hình loại quy tắc giá", description = "Thêm một cấu hình loại quy tắc mới vào hệ thống.")
    public ApiResponse<PricingRuleTypeResponse> createPricingRuleType(
            @Valid @RequestBody PricingRuleTypeCreateRequest request) {
        return ApiResponse.<PricingRuleTypeResponse>builder()
                .result(pricingRuleTypeConfigService.createPricingRuleType(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách cấu hình loại quy tắc giá", description = "Trả về toàn bộ các cấu hình loại quy tắc chưa bị xóa.")
    public ApiResponse<List<PricingRuleTypeResponse>> getAllPricingRuleTypes() {
        return ApiResponse.<List<PricingRuleTypeResponse>>builder()
                .result(pricingRuleTypeConfigService.getAllPricingRuleTypes())
                .build();
    }

    @PutMapping("/{code}")
    @Operation(summary = "Cập nhật cấu hình loại quy tắc giá", description = "Cập nhật tên hiển thị và độ ưu tiên cho loại quy tắc giá.")
    public ApiResponse<PricingRuleTypeResponse> updatePricingRuleType(
            @PathVariable String code,
            @Valid @RequestBody PricingRuleTypeUpdateRequest request) {
        return ApiResponse.<PricingRuleTypeResponse>builder()
                .result(pricingRuleTypeConfigService.updatePricingRuleType(code, request))
                .build();
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Xóa mềm cấu hình loại quy tắc giá", description = "Đánh dấu xóa mềm và đặt trạng thái INACTIVE cho loại quy tắc giá.")
    public ApiResponse<Void> deletePricingRuleType(@PathVariable String code) {
        pricingRuleTypeConfigService.deletePricingRuleType(code);
        return ApiResponse.<Void>builder()
                .message("Pricing rule type config deleted successfully")
                .build();
    }
}
