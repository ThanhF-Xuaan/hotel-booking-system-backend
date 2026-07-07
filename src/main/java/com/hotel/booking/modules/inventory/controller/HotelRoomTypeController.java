package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeResponse;
import com.hotel.booking.modules.inventory.service.HotelRoomTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel/api/v1/inventory/hotel-room-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Inventory - Hotel Room Types", description = "Các API quản lý cấu hình loại phòng cho Khách sạn (HotelRoomType)")
public class HotelRoomTypeController {

    HotelRoomTypeService hotelRoomTypeService;

    @PostMapping
    @Operation(summary = "Tạo mới cấu hình loại phòng cho Khách sạn", description = "Tạo mới và cấu hình sức chứa, giá cả, số lượng phòng cho một Khách sạn.")
    public ApiResponse<HotelRoomTypeResponse> createHotelRoomType(@Valid @RequestBody HotelRoomTypeCreationRequest request) {
        return ApiResponse.<HotelRoomTypeResponse>builder()
                .result(hotelRoomTypeService.createHotelRoomType(request))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật cấu hình loại phòng", description = "Cập nhật chi tiết cấu hình loại phòng hiện có theo ID.")
    public ApiResponse<HotelRoomTypeResponse> updateHotelRoomType(
            @PathVariable Integer id,
            @Valid @RequestBody HotelRoomTypeUpdateRequest request) {
        return ApiResponse.<HotelRoomTypeResponse>builder()
                .result(hotelRoomTypeService.updateHotelRoomType(id, request))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết cấu hình loại phòng", description = "Lấy thông tin chi tiết cấu hình loại phòng của khách sạn theo ID.")
    public ApiResponse<HotelRoomTypeResponse> getHotelRoomType(@PathVariable Integer id) {
        return ApiResponse.<HotelRoomTypeResponse>builder()
                .result(hotelRoomTypeService.getHotelRoomType(id))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách cấu hình loại phòng", description = "Lấy tất cả các cấu hình loại phòng đang hoạt động. Có thể lọc theo hotelId sử dụng query parameter.")
    public ApiResponse<List<HotelRoomTypeResponse>> getHotelRoomTypes(@RequestParam(required = false) Short hotelId) {
        return ApiResponse.<List<HotelRoomTypeResponse>>builder()
                .result(hotelRoomTypeService.getHotelRoomTypes(hotelId))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa cấu hình loại phòng", description = "Xóa mềm (is_deleted = true) cấu hình loại phòng theo ID.")
    public ApiResponse<String> deleteHotelRoomType(@PathVariable Integer id) {
        hotelRoomTypeService.deleteHotelRoomType(id);
        return ApiResponse.<String>builder()
                .result("HotelRoomType deleted successfully")
                .build();
    }
}
