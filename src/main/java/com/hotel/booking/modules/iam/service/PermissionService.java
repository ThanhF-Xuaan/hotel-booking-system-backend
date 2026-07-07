package com.hotel.booking.modules.iam.service;

import com.hotel.booking.modules.iam.dto.request.PermissionCreationRequest;
import com.hotel.booking.modules.iam.dto.request.PermissionUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse createPermission(PermissionCreationRequest request);
    List<PermissionResponse> getAllPermissions();
    PermissionResponse getPermissionById(Short id);
    PermissionResponse updatePermission(Short id, PermissionUpdateRequest request);
    void deletePermission(Short id);
}
