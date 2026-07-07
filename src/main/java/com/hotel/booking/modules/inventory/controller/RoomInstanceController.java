package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.inventory.dto.request.RoomInstanceCreateRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomInstanceStatusUpdateRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomInstanceUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomInstanceResponse;
import com.hotel.booking.modules.inventory.service.RoomInstanceService;
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
@RequestMapping("/hotel/api/v1/inventory/room-instances")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Inventory - Room Instance Management", description = "Các API quản lý danh mục phòng vật lý (RoomInstance)")
public class RoomInstanceController {

    RoomInstanceService roomInstanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới phòng vật lý", description = "Tạo một phòng vật lý mới cho khách sạn. Số phòng phải là duy nhất trong khách sạn đó.")
    public ApiResponse<RoomInstanceResponse> createRoomInstance(@Valid @RequestBody RoomInstanceCreateRequest request) {
        return ApiResponse.<RoomInstanceResponse>builder()
                .result(roomInstanceService.createRoomInstance(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách phòng vật lý", description = "Trả về danh sách tất cả phòng vật lý đang hoạt động. Có thể lọc theo hotelId sử dụng query parameter.")
    public ApiResponse<List<RoomInstanceResponse>> getRoomInstances(@RequestParam(required = false) Short hotelId) {
        return ApiResponse.<List<RoomInstanceResponse>>builder()
                .result(roomInstanceService.getRoomInstances(hotelId))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết phòng vật lý theo ID", description = "Trả về thông tin chi tiết của một phòng vật lý theo ID. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<RoomInstanceResponse> getRoomInstanceById(@PathVariable Integer id) {
        return ApiResponse.<RoomInstanceResponse>builder()
                .result(roomInstanceService.getRoomInstanceById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin phòng vật lý", description = "Cập nhật loại phòng vật lý và số phòng. Không được phép thay đổi hotelId và currentStatus qua endpoint này.")
    public ApiResponse<RoomInstanceResponse> updateRoomInstance(
            @PathVariable Integer id,
            @Valid @RequestBody RoomInstanceUpdateRequest request) {
        return ApiResponse.<RoomInstanceResponse>builder()
                .result(roomInstanceService.updateRoomInstance(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm phòng vật lý", description = "Thực hiện xóa mềm phòng vật lý bằng cách đánh dấu isDeleted = true.")
    public ApiResponse<Void> deleteRoomInstance(@PathVariable Integer id) {
        roomInstanceService.deleteRoomInstance(id);
        return ApiResponse.<Void>builder()
                .message("Room instance deleted successfully")
                .build();
    }
}
