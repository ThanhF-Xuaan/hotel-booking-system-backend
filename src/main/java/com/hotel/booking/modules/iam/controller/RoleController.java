package com.hotel.booking.modules.iam.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.iam.dto.request.RoleCreationRequest;
import com.hotel.booking.modules.iam.dto.request.RoleUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.RoleResponse;
import com.hotel.booking.modules.iam.service.RoleService;
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
@RequestMapping("/hotel/api/v1/iam/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "IAM - Role Management", description = "Các API quản lý Nhóm Quyền")
public class RoleController {

        RoleService roleService;

        @GetMapping
        @Operation(summary = "Lấy tất cả nhóm quyền", description = "Trả về danh sách tất cả các nhóm quyền đang hoạt động (chưa bị xóa mềm).")
        public ApiResponse<List<RoleResponse>> getAllRoles() {
                return ApiResponse.<List<RoleResponse>>builder()
                                .result(roleService.getAllRoles())
                                .build();
        }

        @GetMapping("/{id}")
        @Operation(summary = "Lấy chi tiết nhóm quyền theo ID", description = "Trả về thông tin chi tiết của một nhóm quyền dựa trên ID được cung cấp. Trả về lỗi 404 nếu không tìm thấy.")
        public ApiResponse<RoleResponse> getRoleById(@PathVariable Short id) {
                return ApiResponse.<RoleResponse>builder()
                                .result(roleService.getRoleById(id))
                                .build();
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(summary = "Tạo mới nhóm quyền", description = "Tạo một nhóm quyền mới với các thông tin name, code và status. Mã code phải là duy nhất trong hệ thống, nếu trùng sẽ trả về lỗi 400.")
        public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleCreationRequest request) {
                return ApiResponse.<RoleResponse>builder()
                                .result(roleService.createRole(request))
                                .build();
        }

        @PutMapping("/{id}")
        @Operation(summary = "Cập nhật nhóm quyền", description = "Cập nhật toàn bộ thông tin của một nhóm quyền theo ID. Kiểm tra trùng mã code với các bản ghi khác. Trả về lỗi 404 nếu không tìm thấy, lỗi 400 nếu mã code bị trùng.")
        public ApiResponse<RoleResponse> updateRole(
                        @PathVariable Short id,
                        @Valid @RequestBody RoleUpdateRequest request) {
                return ApiResponse.<RoleResponse>builder()
                                .result(roleService.updateRole(id, request))
                                .build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Xóa mềm nhóm quyền", description = "Thực hiện xóa mềm (soft-delete) một nhóm quyền bằng cách đánh dấu isDeleted = true. Nhóm quyền sẽ không còn xuất hiện trong danh sách nhưng vẫn tồn tại trong cơ sở dữ liệu.")
        public ApiResponse<Void> deleteRole(@PathVariable Short id) {
                roleService.deleteRole(id);
                return ApiResponse.<Void>builder()
                                .message("Role deleted successfully")
                                .build();
        }
}
