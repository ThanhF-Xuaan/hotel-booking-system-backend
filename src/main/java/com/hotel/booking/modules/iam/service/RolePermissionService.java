package com.hotel.booking.modules.iam.service;

import com.hotel.booking.modules.iam.dto.request.RolePermissionSyncRequest;
import com.hotel.booking.modules.iam.dto.response.AssignedPermissionResponse;

import java.util.List;

public interface RolePermissionService {
    List<AssignedPermissionResponse> syncRolePermissions(Short roleId, RolePermissionSyncRequest request);
}
