package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.inventory.dto.request.CatalogItemCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.CatalogItemUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.CatalogItemResponse;
import com.hotel.booking.modules.inventory.service.CatalogItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory/catalog-items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Inventory - Catalog Items", description = "Các API quản lý mặt hàng/dịch vụ trong danh mục kho hàng (CatalogItem)")
public class CatalogItemController {

    CatalogItemService catalogItemService;

    @PostMapping
    @Operation(summary = "Tạo mới một mặt hàng/dịch vụ", description = "Tạo mới một mặt hàng/dịch vụ trong danh mục hàng hóa.")
    public ApiResponse<CatalogItemResponse> createCatalogItem(@Valid @RequestBody CatalogItemCreationRequest request) {
        return ApiResponse.<CatalogItemResponse>builder()
                .result(catalogItemService.createCatalogItem(request))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật mặt hàng/dịch vụ", description = "Cập nhật thông tin chi tiết một mặt hàng/dịch vụ hiện có theo ID.")
    public ApiResponse<CatalogItemResponse> updateCatalogItem(
            @PathVariable Integer id,
            @Valid @RequestBody CatalogItemUpdateRequest request) {
        return ApiResponse.<CatalogItemResponse>builder()
                .result(catalogItemService.updateCatalogItem(id, request))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết mặt hàng/dịch vụ", description = "Lấy thông tin chi tiết của một mặt hàng/dịch vụ theo ID.")
    public ApiResponse<CatalogItemResponse> getCatalogItem(@PathVariable Integer id) {
        return ApiResponse.<CatalogItemResponse>builder()
                .result(catalogItemService.getCatalogItem(id))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách mặt hàng/dịch vụ", description = "Lấy tất cả các mặt hàng/dịch vụ đang hoạt động. Có thể lọc theo hotelId sử dụng query parameter.")
    public ApiResponse<List<CatalogItemResponse>> getCatalogItems(@RequestParam(required = false) Short hotelId) {
        return ApiResponse.<List<CatalogItemResponse>>builder()
                .result(catalogItemService.getCatalogItems(hotelId))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mặt hàng/dịch vụ", description = "Xóa mềm (is_deleted = true) một mặt hàng/dịch vụ theo ID.")
    public ApiResponse<String> deleteCatalogItem(@PathVariable Integer id) {
        catalogItemService.deleteCatalogItem(id);
        return ApiResponse.<String>builder()
                .result("CatalogItem deleted successfully")
                .build();
    }
}
