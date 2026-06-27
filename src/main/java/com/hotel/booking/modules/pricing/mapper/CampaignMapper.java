package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.CampaignCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.CampaignUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.CampaignResponse;
import com.hotel.booking.modules.pricing.entity.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    @Mapping(source = "hotel.id", target = "hotelId")
    CampaignResponse toResponse(Campaign entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    Campaign toEntity(CampaignCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    void updateEntity(CampaignUpdateRequest request, @MappingTarget Campaign entity);
}
