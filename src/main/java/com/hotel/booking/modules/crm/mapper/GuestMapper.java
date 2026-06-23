package com.hotel.booking.modules.crm.mapper;

import com.hotel.booking.modules.crm.dto.request.GuestCreationRequest;
import com.hotel.booking.modules.crm.dto.request.GuestUpdateRequest;
import com.hotel.booking.modules.crm.dto.response.GuestResponse;
import com.hotel.booking.modules.crm.entity.Guest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GuestMapper {

    Guest toEntity(GuestCreationRequest request);

    GuestResponse toResponse(Guest guest);

    void updateEntity(GuestUpdateRequest request, @MappingTarget Guest guest);
}
