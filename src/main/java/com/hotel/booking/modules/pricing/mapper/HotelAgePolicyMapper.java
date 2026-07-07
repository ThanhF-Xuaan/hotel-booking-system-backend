package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.HotelAgePolicyResponse;
import com.hotel.booking.modules.pricing.entity.HotelAgePolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelAgePolicyMapper {

    @Mapping(source = "hotel.id", target = "hotelId")
    HotelAgePolicyResponse toResponse(HotelAgePolicy entity);

    List<HotelAgePolicyResponse> toResponseList(List<HotelAgePolicy> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "hotel", ignore = true)
    HotelAgePolicy toEntity(HotelAgePolicyCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    void updateEntity(HotelAgePolicyUpdateRequest request, @MappingTarget HotelAgePolicy entity);
}
