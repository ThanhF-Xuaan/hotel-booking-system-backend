package com.hotel.booking.modules.inventory.mapper;

import com.hotel.booking.modules.inventory.dto.request.RoomBedCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomBedUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomBedResponse;
import com.hotel.booking.modules.inventory.entity.RoomBed;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomBedMapper {

    RoomBed toEntity(RoomBedCreationRequest request);

    RoomBedResponse toResponse(RoomBed roomBed);

    void updateEntity(RoomBedUpdateRequest request, @MappingTarget RoomBed roomBed);
}
