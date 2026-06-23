package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.inventory.dto.request.RoomBedCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomBedUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomBedResponse;
import com.hotel.booking.modules.inventory.service.RoomBedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotel/api/v1/inventory/room-beds")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Inventory - Room Bed Management", description = "Các API quản lý danh mục giường (RoomBed)")
public class RoomBedController {

    RoomBedService roomBedService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới loại giường", description = "Tạo một loại giường mới trong danh mục. Tên loại giường phải là duy nhất.")
    public ApiResponse<RoomBedResponse> createRoomBed(@Valid @RequestBody RoomBedCreationRequest request) {
        return ApiResponse.<RoomBedResponse>builder()
                .result(roomBedService.createRoomBed(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả loại giường", description = "Trả về danh sách tất cả các loại giường đang hoạt động (chưa bị xóa mềm).")
    public ApiResponse<List<RoomBedResponse>> getAllRoomBeds() {
        return ApiResponse.<List<RoomBedResponse>>builder()
                .result(roomBedService.getAllRoomBeds())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết loại giường theo ID", description = "Trả về thông tin chi tiết của một loại giường dựa trên ID được cung cấp. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<RoomBedResponse> getRoomBedById(@PathVariable Short id) {
        return ApiResponse.<RoomBedResponse>builder()
                .result(roomBedService.getRoomBedById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin loại giường", description = "Cập nhật thông tin chi tiết của một loại giường theo ID. Tránh đổi tên trùng với loại giường khác. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<RoomBedResponse> updateRoomBed(
            @PathVariable Short id,
            @Valid @RequestBody RoomBedUpdateRequest request) {
        return ApiResponse.<RoomBedResponse>builder()
                .result(roomBedService.updateRoomBed(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm loại giường", description = "Thực hiện xóa mềm (soft-delete) một loại giường bằng cách đánh dấu isDeleted = true. Giường sẽ không còn xuất hiện trong các danh sách.")
    public ApiResponse<Void> deleteRoomBed(@PathVariable Short id) {
        roomBedService.deleteRoomBed(id);
        return ApiResponse.<Void>builder()
                .message("Room bed deleted successfully")
                .build();
    }
}
