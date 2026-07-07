package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleTypeUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.DiscountRuleTypeResponse;
import com.hotel.booking.modules.pricing.entity.DiscountRuleTypeConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DiscountRuleTypeConfigMapper {

    DiscountRuleTypeResponse toResponse(DiscountRuleTypeConfig entity);

    @Mapping(target = "isDeleted", ignore = true)
    DiscountRuleTypeConfig toEntity(DiscountRuleTypeCreateRequest request);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntity(DiscountRuleTypeUpdateRequest request, @MappingTarget DiscountRuleTypeConfig entity);
}
