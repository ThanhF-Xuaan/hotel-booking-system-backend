package com.hotel.booking.modules.inventory.mapper;

import com.hotel.booking.modules.inventory.dto.request.RoomTypeCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomTypeUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomTypeResponse;
import com.hotel.booking.modules.inventory.entity.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

    RoomType toEntity(RoomTypeCreationRequest request);

    RoomTypeResponse toResponse(RoomType roomType);

    void updateEntity(RoomTypeUpdateRequest request, @MappingTarget RoomType roomType);
}
