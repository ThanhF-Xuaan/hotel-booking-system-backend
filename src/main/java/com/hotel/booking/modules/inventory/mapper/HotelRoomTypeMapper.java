package com.hotel.booking.modules.inventory.mapper;

import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeResponse;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelRoomTypeMapper {

    @Mapping(source = "hotel.id", target = "hotelId")
    @Mapping(source = "roomType.id", target = "roomTypeId")
    HotelRoomTypeResponse toResponse(HotelRoomType entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "roomType", ignore = true)
    HotelRoomType toEntity(HotelRoomTypeCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "roomType", ignore = true)
    void updateHotelRoomType(HotelRoomTypeUpdateRequest request, @MappingTarget HotelRoomType entity);
}
