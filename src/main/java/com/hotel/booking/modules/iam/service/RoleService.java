package com.hotel.booking.modules.iam.service;

import com.hotel.booking.modules.iam.dto.request.RoleCreationRequest;
import com.hotel.booking.modules.iam.dto.request.RoleUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAllRoles();
    RoleResponse getRoleById(Short id);
    RoleResponse createRole(RoleCreationRequest request);
    RoleResponse updateRole(Short id, RoleUpdateRequest request);
    void deleteRole(Short id);
}
