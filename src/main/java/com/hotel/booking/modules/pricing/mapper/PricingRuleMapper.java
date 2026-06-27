package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.PricingRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.PricingRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.PricingRuleResponse;
import com.hotel.booking.modules.pricing.entity.PricingRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PricingRuleMapper {

    @Mapping(source = "hotelRoomType.id", target = "hotelRoomTypeId")
    @Mapping(source = "holidayCalendar.id", target = "holidayCalendarId")
    @Mapping(source = "ruleType.code", target = "ruleTypeCode")
    PricingRuleResponse toResponse(PricingRule entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotelRoomType", ignore = true)
    @Mapping(target = "holidayCalendar", ignore = true)
    @Mapping(target = "ruleType", ignore = true)
    PricingRule toEntity(PricingRuleCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotelRoomType", ignore = true)
    @Mapping(target = "holidayCalendar", ignore = true)
    @Mapping(target = "ruleType", ignore = true)
    void updateEntity(PricingRuleUpdateRequest request, @MappingTarget PricingRule entity);
}
