package com.hotel.booking.modules.pricing.mapper;

import com.hotel.booking.modules.pricing.dto.request.TaxCategoryCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.TaxCategoryUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.TaxCategoryResponse;
import com.hotel.booking.modules.pricing.entity.TaxCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaxCategoryMapper {

    TaxCategoryResponse toResponse(TaxCategory entity);

    List<TaxCategoryResponse> toResponseList(List<TaxCategory> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    TaxCategory toEntity(TaxCategoryCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntity(TaxCategoryUpdateRequest request, @MappingTarget TaxCategory entity);
}
