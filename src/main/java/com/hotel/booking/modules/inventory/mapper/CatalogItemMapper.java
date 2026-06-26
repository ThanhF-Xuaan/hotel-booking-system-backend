package com.hotel.booking.modules.inventory.mapper;

import com.hotel.booking.modules.inventory.dto.request.CatalogItemCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.CatalogItemUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.CatalogItemResponse;
import com.hotel.booking.modules.inventory.entity.CatalogItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CatalogItemMapper {

    @Mapping(source = "hotel.id", target = "hotelId")
    @Mapping(source = "vatRule.id", target = "vatRuleId")
    CatalogItemResponse toResponse(CatalogItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "vatRule", ignore = true)
    CatalogItem toEntity(CatalogItemCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "vatRule", ignore = true)
    void updateCatalogItem(CatalogItemUpdateRequest request, @MappingTarget CatalogItem entity);
}
