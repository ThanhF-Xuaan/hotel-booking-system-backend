package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.SurchargeRuleResponse;
import com.hotel.booking.modules.pricing.service.SurchargeRuleService;
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
@RequestMapping("/hotel/api/v1/pricing/surcharge-rules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Surcharge Rule Management", description = "Các API quản lý Quy tắc phụ phí (SurchargeRule)")
public class SurchargeRuleController {

    SurchargeRuleService surchargeRuleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới quy tắc phụ phí", description = "Cấu hình quy tắc phụ phí cho một loại phòng khách sạn.")
    public ApiResponse<SurchargeRuleResponse> createSurchargeRule(@Valid @RequestBody SurchargeRuleCreateRequest request) {
        return ApiResponse.<SurchargeRuleResponse>builder()
                .result(surchargeRuleService.createSurchargeRule(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách quy tắc phụ phí", description = "Lấy toàn bộ các quy tắc phụ phí đang hoạt động. Có thể lọc theo hotelRoomTypeId.")
    public ApiResponse<List<SurchargeRuleResponse>> getSurchargeRules(
            @RequestParam(required = false) Integer hotelRoomTypeId) {
        return ApiResponse.<List<SurchargeRuleResponse>>builder()
                .result(surchargeRuleService.getSurchargeRules(hotelRoomTypeId))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết quy tắc phụ phí", description = "Trả về thông tin chi tiết của quy tắc phụ phí theo ID.")
    public ApiResponse<SurchargeRuleResponse> getSurchargeRuleById(@PathVariable Integer id) {
        return ApiResponse.<SurchargeRuleResponse>builder()
                .result(surchargeRuleService.getSurchargeRuleById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật quy tắc phụ phí", description = "Cập nhật thông tin quy tắc phụ phí hiện có.")
    public ApiResponse<SurchargeRuleResponse> updateSurchargeRule(
            @PathVariable Integer id,
            @Valid @RequestBody SurchargeRuleUpdateRequest request) {
        return ApiResponse.<SurchargeRuleResponse>builder()
                .result(surchargeRuleService.updateSurchargeRule(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm quy tắc phụ phí", description = "Set isDeleted = true cho quy tắc phụ phí.")
    public ApiResponse<Void> deleteSurchargeRule(@PathVariable Integer id) {
        surchargeRuleService.deleteSurchargeRule(id);
        return ApiResponse.<Void>builder()
                .message("Surcharge rule deleted successfully")
                .build();
    }
}
