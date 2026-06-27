package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.DiscountRuleResponse;
import com.hotel.booking.modules.pricing.service.DiscountRuleService;
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
@RequestMapping("/hotel/api/v1/pricing/discount-rules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Discount Rule Management", description = "Các API quản lý Quy tắc giảm giá (DiscountRule)")
public class DiscountRuleController {

    DiscountRuleService discountRuleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới quy tắc giảm giá", description = "Cấu hình quy tắc giảm giá cho một loại phòng khách sạn.")
    public ApiResponse<DiscountRuleResponse> createDiscountRule(@Valid @RequestBody DiscountRuleCreateRequest request) {
        return ApiResponse.<DiscountRuleResponse>builder()
                .result(discountRuleService.createDiscountRule(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách quy tắc giảm giá", description = "Lấy toàn bộ các quy tắc giảm giá đang hoạt động. Có thể lọc theo hotelRoomTypeId.")
    public ApiResponse<List<DiscountRuleResponse>> getDiscountRules(
            @RequestParam(required = false) Integer hotelRoomTypeId) {
        return ApiResponse.<List<DiscountRuleResponse>>builder()
                .result(discountRuleService.getDiscountRules(hotelRoomTypeId))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết quy tắc giảm giá", description = "Trả về thông tin chi tiết của quy tắc giảm giá theo ID.")
    public ApiResponse<DiscountRuleResponse> getDiscountRuleById(@PathVariable Integer id) {
        return ApiResponse.<DiscountRuleResponse>builder()
                .result(discountRuleService.getDiscountRuleById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật quy tắc giảm giá", description = "Cập nhật thông tin quy tắc giảm giá hiện có.")
    public ApiResponse<DiscountRuleResponse> updateDiscountRule(
            @PathVariable Integer id,
            @Valid @RequestBody DiscountRuleUpdateRequest request) {
        return ApiResponse.<DiscountRuleResponse>builder()
                .result(discountRuleService.updateDiscountRule(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm quy tắc giảm giá", description = "Set isDeleted = true cho quy tắc giảm giá.")
    public ApiResponse<Void> deleteDiscountRule(@PathVariable Integer id) {
        discountRuleService.deleteDiscountRule(id);
        return ApiResponse.<Void>builder()
                .message("Discount rule deleted successfully")
                .build();
    }
}
