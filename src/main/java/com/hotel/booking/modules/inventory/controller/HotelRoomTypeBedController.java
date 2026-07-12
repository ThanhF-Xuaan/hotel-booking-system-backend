package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeBedSyncRequest;
import com.hotel.booking.modules.inventory.dto.response.AssignedBedResponse;
import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeBedResponse;
import com.hotel.booking.modules.inventory.service.HotelRoomTypeBedService;
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
@RequestMapping("/api/v1/inventory/hotel-room-types/{hotelRoomTypeId}/beds")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Inventory - Hotel Room Type Beds", description = "Các API đồng bộ cấu hình giường cho Loại phòng (HotelRoomType)")
public class HotelRoomTypeBedController {

    HotelRoomTypeBedService hotelRoomTypeBedService;

    @PutMapping
    @Operation(summary = "Đồng bộ danh sách giường cấu hình cho Loại phòng", description = "Đồng bộ hàng loạt cấu hình giường (thêm mới, cập nhật số lượng, xóa giường không gửi trong yêu cầu).")
    public ApiResponse<List<AssignedBedResponse>> syncBeds(
            @PathVariable Integer hotelRoomTypeId,
            @Valid @RequestBody HotelRoomTypeBedSyncRequest request) {
        return ApiResponse.<List<AssignedBedResponse>>builder()
                .result(hotelRoomTypeBedService.syncBeds(hotelRoomTypeId, request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách giường cấu hình cho Loại phòng", description = "Lấy danh sách cấu hình giường chi tiết đã được gán cho loại phòng khách sạn theo ID.")
    public ApiResponse<List<HotelRoomTypeBedResponse>> getBeds(@PathVariable Integer hotelRoomTypeId) {
        return ApiResponse.<List<HotelRoomTypeBedResponse>>builder()
                .result(hotelRoomTypeBedService.getBedsByHotelRoomTypeId(hotelRoomTypeId))
                .build();
    }
}

