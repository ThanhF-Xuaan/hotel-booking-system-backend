package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.SurchargeRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.SurchargeRuleResponse;
import com.hotel.booking.modules.pricing.entity.SurchargeRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SurchargeRuleMapper {

    @Mapping(source = "hotelRoomType.id", target = "hotelRoomTypeId")
    @Mapping(source = "agePolicy.id", target = "agePolicyId")
    SurchargeRuleResponse toResponse(SurchargeRule entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "hotelRoomType", ignore = true)
    @Mapping(target = "agePolicy", ignore = true)
    SurchargeRule toEntity(SurchargeRuleCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotelRoomType", ignore = true)
    @Mapping(target = "agePolicy", ignore = true)
    void updateEntity(SurchargeRuleUpdateRequest request, @MappingTarget SurchargeRule entity);
}

