package com.hotel.booking.modules.inventory.mapper;

import com.hotel.booking.modules.inventory.dto.request.CatalogItemCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.CatalogItemUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.CatalogItemResponse;
import com.hotel.booking.modules.inventory.entity.CatalogItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CatalogItemMapper {

    @Mapping(source = "hotel.id", target = "hotelId")
    @Mapping(source = "taxCategory.id", target = "taxCategoryId")
    CatalogItemResponse toResponse(CatalogItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "taxCategory", ignore = true)
    CatalogItem toEntity(CatalogItemCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "taxCategory", ignore = true)
    void updateCatalogItem(CatalogItemUpdateRequest request, @MappingTarget CatalogItem entity);
}

