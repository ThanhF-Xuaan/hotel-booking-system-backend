package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.VatRuleCreationRequest;
import com.hotel.booking.modules.pricing.dto.request.VatRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.VatRuleResponse;
import com.hotel.booking.modules.pricing.entity.VatRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VatRuleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "taxCategory", ignore = true)
    VatRule toEntity(VatRuleCreationRequest request);

    @Mapping(source = "taxCategory.id", target = "taxCategoryId")
    VatRuleResponse toResponse(VatRule vatRule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "taxCategory", ignore = true)
    void updateEntity(VatRuleUpdateRequest request, @MappingTarget VatRule vatRule);
}

