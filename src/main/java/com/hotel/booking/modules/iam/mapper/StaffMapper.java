package com.hotel.booking.modules.iam.mapper;

import com.hotel.booking.modules.iam.dto.request.StaffCreationRequest;
import com.hotel.booking.modules.iam.dto.request.StaffUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.StaffResponse;
import com.hotel.booking.modules.iam.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StaffMapper {

    @Mapping(source = "hotel.id", target = "hotelId")
    @Mapping(source = "hotel.name", target = "hotelName")
    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.name", target = "roleName")
    StaffResponse toResponse(Staff staff);

    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "role", ignore = true)
    Staff toEntity(StaffCreationRequest request);

    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(StaffUpdateRequest request, @MappingTarget Staff staff);
}
