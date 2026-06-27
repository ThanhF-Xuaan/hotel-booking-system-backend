package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.DiscountRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.DiscountRuleResponse;
import com.hotel.booking.modules.pricing.entity.DiscountRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DiscountRuleMapper {

    @Mapping(source = "hotelRoomType.id", target = "hotelRoomTypeId")
    @Mapping(source = "campaign.id", target = "campaignId")
    @Mapping(source = "ruleType.code", target = "ruleTypeCode")
    DiscountRuleResponse toResponse(DiscountRule entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotelRoomType", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    @Mapping(target = "ruleType", ignore = true)
    DiscountRule toEntity(DiscountRuleCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotelRoomType", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    @Mapping(target = "ruleType", ignore = true)
    void updateEntity(DiscountRuleUpdateRequest request, @MappingTarget DiscountRule entity);
}
