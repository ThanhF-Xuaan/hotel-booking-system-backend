package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.request.DiscountTierRequest;
import com.hotel.booking.modules.pricing.dto.response.DiscountRuleResponse;
import com.hotel.booking.modules.pricing.entity.Campaign;
import com.hotel.booking.modules.pricing.entity.DiscountRule;
import com.hotel.booking.modules.pricing.entity.DiscountRuleTypeConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiscountRuleMapper {

    @Mapping(source = "hotelRoomType.id", target = "hotelRoomTypeId")
    @Mapping(source = "hotelRoomType.roomType.name", target = "hotelRoomTypeName")
    @Mapping(source = "campaign.id", target = "campaignId")
    @Mapping(source = "ruleType.code", target = "ruleTypeCode")
    DiscountRuleResponse toResponse(DiscountRule entity);

    List<DiscountRuleResponse> toResponseList(List<DiscountRule> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "hotelRoomType", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    @Mapping(target = "ruleType", ignore = true)
    void updateEntity(DiscountRuleUpdateRequest request, @MappingTarget DiscountRule entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "startDate", source = "request.startDate")
    @Mapping(target = "endDate", source = "request.endDate")
    @Mapping(target = "hotelRoomType", source = "roomType")
    @Mapping(target = "campaign", source = "campaign")
    @Mapping(target = "ruleType", source = "ruleTypeConfig")
    @Mapping(target = "conditions", source = "tier.conditions")
    @Mapping(target = "discountType", source = "tier.discountType")
    @Mapping(target = "discountValue", source = "tier.discountValue")
    DiscountRule toEntity(
            DiscountRuleCreateRequest request,
            DiscountTierRequest tier,
            HotelRoomType roomType,
            Campaign campaign,
            DiscountRuleTypeConfig ruleTypeConfig
    );
}
