package com.hotel.booking.modules.inventory.mapper;

import com.hotel.booking.modules.inventory.dto.request.HotelCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.HotelUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.HotelResponse;
import com.hotel.booking.modules.inventory.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    Hotel toEntity(HotelCreationRequest request);

    HotelResponse toResponse(Hotel hotel);

    void updateEntity(HotelUpdateRequest request, @MappingTarget Hotel hotel);
}
