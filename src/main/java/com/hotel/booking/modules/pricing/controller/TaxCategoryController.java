package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.TaxCategoryCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.TaxCategoryUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.TaxCategoryResponse;
import com.hotel.booking.modules.pricing.service.TaxCategoryService;
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
@RequestMapping("/hotel/api/v1/pricing/tax-categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Tax Category Management", description = "Các API quản lý Danh mục thuế (TaxCategory)")
public class TaxCategoryController {

    TaxCategoryService taxCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới danh mục thuế", description = "Tạo mới một cấu hình danh mục thuế.")
    public ApiResponse<TaxCategoryResponse> createTaxCategory(@Valid @RequestBody TaxCategoryCreateRequest request) {
        return ApiResponse.<TaxCategoryResponse>builder()
                .result(taxCategoryService.createTaxCategory(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách danh mục thuế", description = "Lấy toàn bộ danh sách các danh mục thuế đang hoạt động.")
    public ApiResponse<List<TaxCategoryResponse>> getAllTaxCategories() {
        return ApiResponse.<List<TaxCategoryResponse>>builder()
                .result(taxCategoryService.getAllTaxCategories())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết danh mục thuế", description = "Lấy thông tin chi tiết của danh mục thuế theo ID.")
    public ApiResponse<TaxCategoryResponse> getTaxCategoryById(@PathVariable Integer id) {
        return ApiResponse.<TaxCategoryResponse>builder()
                .result(taxCategoryService.getTaxCategoryById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật danh mục thuế", description = "Cập nhật thông tin của danh mục thuế hiện có.")
    public ApiResponse<TaxCategoryResponse> updateTaxCategory(
            @PathVariable Integer id,
            @Valid @RequestBody TaxCategoryUpdateRequest request) {
        return ApiResponse.<TaxCategoryResponse>builder()
                .result(taxCategoryService.updateTaxCategory(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm danh mục thuế", description = "Set isDeleted = true cho danh mục thuế theo ID.")
    public ApiResponse<Void> deleteTaxCategory(@PathVariable Integer id) {
        taxCategoryService.deleteTaxCategory(id);
        return ApiResponse.<Void>builder()
                .message("Tax category deleted successfully")
                .build();
    }
}
