package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.PricingRuleTypeResponse;
import com.hotel.booking.modules.pricing.entity.PricingRuleTypeConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PricingRuleTypeConfigMapper {

    PricingRuleTypeResponse toResponse(PricingRuleTypeConfig entity);

    @Mapping(target = "isDeleted", ignore = true)
    PricingRuleTypeConfig toEntity(PricingRuleTypeCreateRequest request);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntity(PricingRuleTypeUpdateRequest request, @MappingTarget PricingRuleTypeConfig entity);
}
