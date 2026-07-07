package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.inventory.dto.request.RoomFeatureCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomFeatureUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomFeatureResponse;
import com.hotel.booking.modules.inventory.service.RoomFeatureService;
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
@RequestMapping("/hotel/api/v1/inventory/room-features")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Inventory - Room Feature Management", description = "Các API quản lý danh mục tiện ích phòng (RoomFeature)")
public class RoomFeatureController {

    RoomFeatureService roomFeatureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới tiện ích phòng", description = "Tạo một tiện ích phòng mới trong danh mục. Mã code tiện ích phải là duy nhất.")
    public ApiResponse<RoomFeatureResponse> createRoomFeature(@Valid @RequestBody RoomFeatureCreationRequest request) {
        return ApiResponse.<RoomFeatureResponse>builder()
                .result(roomFeatureService.createRoomFeature(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả tiện ích phòng", description = "Trả về danh sách tất cả các tiện ích phòng đang hoạt động (chưa bị xóa mềm).")
    public ApiResponse<List<RoomFeatureResponse>> getAllRoomFeatures() {
        return ApiResponse.<List<RoomFeatureResponse>>builder()
                .result(roomFeatureService.getAllRoomFeatures())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết tiện ích phòng theo ID", description = "Trả về thông tin chi tiết của một tiện ích phòng dựa trên ID cung cấp. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<RoomFeatureResponse> getRoomFeatureById(@PathVariable Short id) {
        return ApiResponse.<RoomFeatureResponse>builder()
                .result(roomFeatureService.getRoomFeatureById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin tiện ích phòng", description = "Cập nhật thông tin chi tiết của một tiện ích phòng theo ID. Tránh đổi mã code trùng với tiện ích phòng khác. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<RoomFeatureResponse> updateRoomFeature(
            @PathVariable Short id,
            @Valid @RequestBody RoomFeatureUpdateRequest request) {
        return ApiResponse.<RoomFeatureResponse>builder()
                .result(roomFeatureService.updateRoomFeature(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm tiện ích phòng", description = "Thực hiện xóa mềm (soft-delete) một tiện ích phòng bằng cách đánh dấu isDeleted = true. Tiện ích sẽ không còn xuất hiện trong các danh sách.")
    public ApiResponse<Void> deleteRoomFeature(@PathVariable Short id) {
        roomFeatureService.deleteRoomFeature(id);
        return ApiResponse.<Void>builder()
                .message("Room feature deleted successfully")
                .build();
    }
}
