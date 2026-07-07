package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.inventory.dto.request.RoomTypeCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomTypeUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomTypeResponse;
import com.hotel.booking.modules.inventory.service.RoomTypeService;
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
@RequestMapping("/hotel/api/v1/inventory/room-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Inventory - Room Type Management", description = "Các API quản lý Loại phòng (RoomType)")
public class RoomTypeController {

    RoomTypeService roomTypeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới loại phòng", description = "Tạo một loại phòng mới với các thông tin code, name và status. Code phải là duy nhất trong hệ thống.")
    public ApiResponse<RoomTypeResponse> createRoomType(@Valid @RequestBody RoomTypeCreationRequest request) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.createRoomType(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả loại phòng", description = "Trả về danh sách tất cả các loại phòng đang hoạt động (chưa bị xóa mềm).")
    public ApiResponse<List<RoomTypeResponse>> getAllRoomTypes() {
        return ApiResponse.<List<RoomTypeResponse>>builder()
                .result(roomTypeService.getAllRoomTypes())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết loại phòng theo ID", description = "Trả về thông tin chi tiết của một loại phòng dựa trên ID được cung cấp. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<RoomTypeResponse> getRoomTypeById(@PathVariable Short id) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.getRoomTypeById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin loại phòng", description = "Cập nhật tên và trạng thái của một loại phòng theo ID. Code là master data nên không cho phép cập nhật. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<RoomTypeResponse> updateRoomType(
            @PathVariable Short id,
            @Valid @RequestBody RoomTypeUpdateRequest request) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.updateRoomType(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm loại phòng", description = "Thực hiện xóa mềm (soft-delete) một loại phòng bằng cách đánh dấu isDeleted = true. Loại phòng sẽ không xuất hiện trong các danh sách.")
    public ApiResponse<Void> deleteRoomType(@PathVariable Short id) {
        roomTypeService.deleteRoomType(id);
        return ApiResponse.<Void>builder()
                .message("Room type deleted successfully")
                .build();
    }
}
