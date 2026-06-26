package com.hotel.booking.modules.iam.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.iam.dto.request.RolePermissionSyncRequest;
import com.hotel.booking.modules.iam.dto.response.AssignedPermissionResponse;
import com.hotel.booking.modules.iam.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel/api/v1/iam/roles/{roleId}/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "IAM - Role Permission Mapping", description = "Các API đồng bộ Quyền cho Vai trò (Role)")
public class RolePermissionController {

    RolePermissionService rolePermissionService;

    @PutMapping
    @Operation(summary = "Đồng bộ danh sách quyền của Vai trò", description = "Đồng bộ hàng loạt quyền được gán cho một Vai trò dựa trên danh sách ID truyền lên.")
    public ApiResponse<List<AssignedPermissionResponse>> syncRolePermissions(
            @PathVariable Short roleId,
            @Valid @RequestBody RolePermissionSyncRequest request) {
        return ApiResponse.<List<AssignedPermissionResponse>>builder()
                .result(rolePermissionService.syncRolePermissions(roleId, request))
                .build();
    }
}
