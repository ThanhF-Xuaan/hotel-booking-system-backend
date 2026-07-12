package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.HotelAgePolicyResponse;
import com.hotel.booking.modules.pricing.service.HotelAgePolicyService;
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
@RequestMapping("/api/v1/pricing/age-policies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Hotel Age Policy Management", description = "Các API quản lý Chính sách độ tuổi của khách sạn (HotelAgePolicy)")
public class HotelAgePolicyController {

    HotelAgePolicyService hotelAgePolicyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới chính sách độ tuổi", description = "Tạo mới cấu hình chính sách độ tuổi cho khách sạn.")
    public ApiResponse<HotelAgePolicyResponse> createPolicy(@Valid @RequestBody HotelAgePolicyCreateRequest request) {
        return ApiResponse.<HotelAgePolicyResponse>builder()
                .result(hotelAgePolicyService.createPolicy(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách chính sách độ tuổi", description = "Lấy toàn bộ các chính sách độ tuổi đang hoạt động. Có thể lọc theo hotelId.")
    public ApiResponse<List<HotelAgePolicyResponse>> getPolicies(
            @RequestParam(required = false) Short hotelId) {
        return ApiResponse.<List<HotelAgePolicyResponse>>builder()
                .result(hotelAgePolicyService.getPolicies(hotelId))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết chính sách độ tuổi", description = "Trả về thông tin chi tiết của chính sách độ tuổi theo ID.")
    public ApiResponse<HotelAgePolicyResponse> getPolicyById(@PathVariable Short id) {
        return ApiResponse.<HotelAgePolicyResponse>builder()
                .result(hotelAgePolicyService.getPolicyById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật chính sách độ tuổi", description = "Cập nhật thông tin chính sách độ tuổi hiện có.")
    public ApiResponse<HotelAgePolicyResponse> updatePolicy(
            @PathVariable Short id,
            @Valid @RequestBody HotelAgePolicyUpdateRequest request) {
        return ApiResponse.<HotelAgePolicyResponse>builder()
                .result(hotelAgePolicyService.updatePolicy(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm chính sách độ tuổi", description = "Set isDeleted = true cho chính sách độ tuổi.")
    public ApiResponse<Void> deletePolicy(@PathVariable Short id) {
        hotelAgePolicyService.deletePolicy(id);
        return ApiResponse.<Void>builder()
                .message("Hotel age policy deleted successfully")
                .build();
    }
}
