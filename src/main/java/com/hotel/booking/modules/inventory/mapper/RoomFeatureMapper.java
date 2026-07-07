package com.hotel.booking.modules.inventory.mapper;

import com.hotel.booking.modules.inventory.dto.request.RoomFeatureCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomFeatureUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomFeatureResponse;
import com.hotel.booking.modules.inventory.entity.RoomFeature;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomFeatureMapper {

    RoomFeature toEntity(RoomFeatureCreationRequest request);

    RoomFeatureResponse toResponse(RoomFeature roomFeature);

    void updateEntity(RoomFeatureUpdateRequest request, @MappingTarget RoomFeature roomFeature);
}
