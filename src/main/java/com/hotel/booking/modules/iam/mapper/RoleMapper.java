package com.hotel.booking.modules.iam.mapper;

import com.hotel.booking.modules.iam.dto.request.RoleCreationRequest;
import com.hotel.booking.modules.iam.dto.request.RoleUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.RoleResponse;
import com.hotel.booking.modules.iam.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toEntity(RoleCreationRequest request);

    RoleResponse toResponse(Role role);

    void updateEntity(RoleUpdateRequest request, @MappingTarget Role role);
}
