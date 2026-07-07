package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeCatalogItemSyncRequest;
import com.hotel.booking.modules.inventory.dto.response.AssignedCatalogItemResponse;
import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeCatalogItemResponse;
import com.hotel.booking.modules.inventory.service.HotelRoomTypeCatalogItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotel/api/v1/inventory/hotel-room-types/{hotelRoomTypeId}/catalog-items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Inventory - Hotel Room Type Catalog Items", description = "Các API đồng bộ cấu hình mặt hàng gán kèm cho Loại phòng (HotelRoomType)")
public class HotelRoomTypeCatalogItemController {

    HotelRoomTypeCatalogItemService hotelRoomTypeCatalogItemService;

    @PutMapping
    @Operation(summary = "Đồng bộ danh sách mặt hàng gán kèm cho Loại phòng", description = "Đồng bộ hàng loạt cấu hình mặt hàng gán kèm (thêm mới, cập nhật giá/usage, xóa cấu hình không gửi trong yêu cầu).")
    public ApiResponse<List<AssignedCatalogItemResponse>> syncCatalogItems(
            @PathVariable Integer hotelRoomTypeId,
            @Valid @RequestBody HotelRoomTypeCatalogItemSyncRequest request) {
        return ApiResponse.<List<AssignedCatalogItemResponse>>builder()
                .result(hotelRoomTypeCatalogItemService.syncCatalogItems(hotelRoomTypeId, request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách mặt hàng gán kèm cho Loại phòng", description = "Lấy danh sách mặt hàng gán kèm chi tiết đã được cấu hình cho loại phòng khách sạn theo ID.")
    public ApiResponse<List<HotelRoomTypeCatalogItemResponse>> getCatalogItems(@PathVariable Integer hotelRoomTypeId) {
        return ApiResponse.<List<HotelRoomTypeCatalogItemResponse>>builder()
                .result(hotelRoomTypeCatalogItemService.getCatalogItemsByHotelRoomTypeId(hotelRoomTypeId))
                .build();
    }
}

