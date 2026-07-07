package com.hotel.booking.modules.iam.mapper;

import com.hotel.booking.modules.iam.dto.request.PermissionCreationRequest;
import com.hotel.booking.modules.iam.dto.request.PermissionUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.PermissionResponse;
import com.hotel.booking.modules.iam.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toEntity(PermissionCreationRequest request);

    PermissionResponse toResponse(Permission permission);

    void updateEntity(PermissionUpdateRequest request, @MappingTarget Permission permission);
}
