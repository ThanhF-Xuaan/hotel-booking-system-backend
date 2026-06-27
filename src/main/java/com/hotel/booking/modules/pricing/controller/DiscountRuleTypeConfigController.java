package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.DiscountRuleTypeResponse;
import com.hotel.booking.modules.pricing.service.DiscountRuleTypeConfigService;
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
@RequestMapping("/hotel/api/v1/pricing/discount-rule-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Discount Rule Type Config Management", description = "Các API quản lý cấu hình loại quy tắc giảm giá (DiscountRuleTypeConfig)")
public class DiscountRuleTypeConfigController {

    DiscountRuleTypeConfigService discountRuleTypeConfigService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới cấu hình loại quy tắc giảm giá", description = "Thêm một cấu hình loại quy tắc mới vào hệ thống.")
    public ApiResponse<DiscountRuleTypeResponse> createDiscountRuleType(
            @Valid @RequestBody DiscountRuleTypeCreateRequest request) {
        return ApiResponse.<DiscountRuleTypeResponse>builder()
                .result(discountRuleTypeConfigService.createDiscountRuleType(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách cấu hình loại quy tắc giảm giá", description = "Trả về toàn bộ các cấu hình loại quy tắc chưa bị xóa.")
    public ApiResponse<List<DiscountRuleTypeResponse>> getAllDiscountRuleTypes() {
        return ApiResponse.<List<DiscountRuleTypeResponse>>builder()
                .result(discountRuleTypeConfigService.getAllDiscountRuleTypes())
                .build();
    }

    @PutMapping("/{code}")
    @Operation(summary = "Cập nhật cấu hình loại quy tắc giảm giá", description = "Cập nhật tên hiển thị và độ ưu tiên cho loại quy tắc giảm giá.")
    public ApiResponse<DiscountRuleTypeResponse> updateDiscountRuleType(
            @PathVariable String code,
            @Valid @RequestBody DiscountRuleTypeUpdateRequest request) {
        return ApiResponse.<DiscountRuleTypeResponse>builder()
                .result(discountRuleTypeConfigService.updateDiscountRuleType(code, request))
                .build();
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Xóa mềm cấu hình loại quy tắc giảm giá", description = "Đánh dấu xóa mềm và đặt trạng thái INACTIVE cho loại quy tắc giảm giá.")
    public ApiResponse<Void> deleteDiscountRuleType(@PathVariable String code) {
        discountRuleTypeConfigService.deleteDiscountRuleType(code);
        return ApiResponse.<Void>builder()
                .message("Discount rule type config deleted successfully")
                .build();
    }
}
