package com.hotel.booking.modules.crm.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.crm.dto.request.GuestCreationRequest;
import com.hotel.booking.modules.crm.dto.request.GuestUpdateRequest;
import com.hotel.booking.modules.crm.dto.response.GuestResponse;
import com.hotel.booking.modules.crm.service.GuestService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crm/guests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "CRM - Guest Management", description = "Các API quản lý thông tin khách hàng (Guest)")
public class GuestController {

    GuestService guestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới hồ sơ khách hàng", description = "Tạo một hồ sơ khách hàng mới với các thông tin cá nhân. Số điện thoại phải là duy nhất chưa bị xóa mềm trong hệ thống.")
    public ApiResponse<GuestResponse> createGuest(@Valid @RequestBody GuestCreationRequest request) {
        return ApiResponse.<GuestResponse>builder()
                .result(guestService.createGuest(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả khách hàng", description = "Trả về danh sách tất cả các khách hàng đang hoạt động (chưa bị xóa mềm).")
    public ApiResponse<List<GuestResponse>> getAllGuests() {
        return ApiResponse.<List<GuestResponse>>builder()
                .result(guestService.getAllGuests())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết khách hàng theo ID", description = "Trả về thông tin chi tiết của khách hàng dựa trên ID nội bộ được cung cấp. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<GuestResponse> getGuestById(@PathVariable Long id) {
        return ApiResponse.<GuestResponse>builder()
                .result(guestService.getGuestById(id))
                .build();
    }

    @GetMapping("/public/{publicId}")
    @Operation(summary = "Lấy chi tiết khách hàng theo UUID công khai", description = "Trả về thông tin chi tiết của khách hàng dựa trên UUID công khai được cung cấp (phục vụ tra cứu từ bên ngoài). Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<GuestResponse> getGuestByPublicId(@PathVariable UUID publicId) {
        return ApiResponse.<GuestResponse>builder()
                .result(guestService.getGuestByPublicId(publicId))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin khách hàng", description = "Cập nhật thông tin chi tiết của một hồ sơ khách hàng dựa trên ID nội bộ cung cấp. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<GuestResponse> updateGuest(
            @PathVariable Long id,
            @Valid @RequestBody GuestUpdateRequest request) {
        return ApiResponse.<GuestResponse>builder()
                .result(guestService.updateGuest(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm hồ sơ khách hàng", description = "Thực hiện xóa mềm (soft-delete) hồ sơ khách hàng bằng cách đánh dấu isDeleted = true. Khách hàng sẽ không xuất hiện trong danh sách nữa.")
    public ApiResponse<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ApiResponse.<Void>builder()
                .message("Guest deleted successfully")
                .build();
    }
}
