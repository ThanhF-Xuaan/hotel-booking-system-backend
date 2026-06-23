package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.VatRuleCreationRequest;
import com.hotel.booking.modules.pricing.dto.request.VatRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.VatRuleResponse;
import com.hotel.booking.modules.pricing.entity.VatRule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VatRuleMapper {

    VatRule toEntity(VatRuleCreationRequest request);

    VatRuleResponse toResponse(VatRule vatRule);

    void updateEntity(VatRuleUpdateRequest request, @MappingTarget VatRule vatRule);
}
