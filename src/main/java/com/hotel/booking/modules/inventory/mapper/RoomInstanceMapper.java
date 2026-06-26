package com.hotel.booking.modules.inventory.mapper;

import com.hotel.booking.modules.inventory.dto.request.RoomInstanceCreateRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomInstanceUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomInstanceResponse;
import com.hotel.booking.modules.inventory.entity.RoomInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomInstanceMapper {

    @Mapping(source = "hotel.id", target = "hotelId")
    @Mapping(source = "hotelRoomType.id", target = "hotelRoomTypeId")
    RoomInstanceResponse toResponse(RoomInstance entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "hotelRoomType", ignore = true)
    RoomInstance toEntity(RoomInstanceCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "hotelRoomType", ignore = true)
    void updateEntity(RoomInstanceUpdateRequest request, @MappingTarget RoomInstance entity);
}
