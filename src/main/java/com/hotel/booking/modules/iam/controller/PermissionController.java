package com.hotel.booking.modules.iam.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.iam.dto.request.PermissionCreationRequest;
import com.hotel.booking.modules.iam.dto.request.PermissionUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.PermissionResponse;
import com.hotel.booking.modules.iam.service.PermissionService;
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
@RequestMapping("/hotel/api/v1/iam/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "IAM - Permission Management", description = "Các API quản lý Quyền hạn (Permission)")
public class PermissionController {

    PermissionService permissionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới quyền hạn", description = "Tạo một quyền hạn mới với các thông tin action, resource và status. Cặp action và resource phải là duy nhất, nếu trùng sẽ trả về lỗi 400.")
    public ApiResponse<PermissionResponse> createPermission(
            @Valid @RequestBody PermissionCreationRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả quyền hạn", description = "Trả về danh sách tất cả các quyền hạn đang hoạt động (chưa bị xóa mềm).")
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAllPermissions())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết quyền hạn theo ID", description = "Trả về thông tin chi tiết của một quyền hạn dựa trên ID cung cấp. Trả về lỗi 404 nếu không tìm thấy hoặc đã bị xóa mềm.")
    public ApiResponse<PermissionResponse> getPermissionById(@PathVariable Short id) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.getPermissionById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật trạng thái quyền hạn", description = "Cập nhật trạng thái (status) của một quyền hạn dựa trên ID cung cấp. Không cho phép thay đổi action và resource để tránh hỏng logic phân quyền.")
    public ApiResponse<PermissionResponse> updatePermission(
            @PathVariable Short id,
            @Valid @RequestBody PermissionUpdateRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.updatePermission(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm quyền hạn", description = "Thực hiện xóa mềm một quyền hạn bằng cách đánh dấu isDeleted = true. Quyền hạn sẽ không còn xuất hiện trong các lượt tìm kiếm tiếp theo.")
    public ApiResponse<Void> deletePermission(@PathVariable Short id) {
        permissionService.deletePermission(id);
        return ApiResponse.<Void>builder()
                .message("Permission deleted successfully")
                .build();
    }
}
