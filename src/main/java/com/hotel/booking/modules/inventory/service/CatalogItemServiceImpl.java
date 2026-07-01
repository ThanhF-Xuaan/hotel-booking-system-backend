package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.CatalogItemCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.CatalogItemUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.CatalogItemResponse;
import com.hotel.booking.modules.inventory.entity.CatalogItem;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.mapper.CatalogItemMapper;
import com.hotel.booking.modules.inventory.repository.CatalogItemRepository;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.pricing.entity.TaxCategory;
import com.hotel.booking.modules.pricing.repository.TaxCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class CatalogItemServiceImpl implements CatalogItemService {

    CatalogItemRepository catalogItemRepository;
    HotelRepository hotelRepository;
    TaxCategoryRepository taxCategoryRepository;
    CatalogItemMapper catalogItemMapper;

    @Override
    @Transactional
    public CatalogItemResponse createCatalogItem(CatalogItemCreationRequest request) {
        log.info("Creating CatalogItem for hotelId: {}, taxCategoryId: {}", request.getHotelId(), request.getTaxCategoryId());

        Hotel hotel = hotelRepository.findByIdAndIsDeletedFalse(request.getHotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        TaxCategory taxCategory = taxCategoryRepository.findByIdAndIsDeletedFalse(request.getTaxCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Generic error or tax category not found

        CatalogItem entity = catalogItemMapper.toEntity(request);
        entity.setHotel(hotel);
        entity.setTaxCategory(taxCategory);
        entity.setIsDeleted(false);

        entity = catalogItemRepository.save(entity);
        return catalogItemMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public CatalogItemResponse updateCatalogItem(Integer id, CatalogItemUpdateRequest request) {
        log.info("Updating CatalogItem id: {}", id);

        CatalogItem entity = catalogItemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATALOG_ITEM_NOT_FOUND));

        if (!entity.getTaxCategory().getId().equals(request.getTaxCategoryId())) {
            TaxCategory taxCategory = taxCategoryRepository.findByIdAndIsDeletedFalse(request.getTaxCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
            entity.setTaxCategory(taxCategory);
        }

        catalogItemMapper.updateCatalogItem(request, entity);
        entity = catalogItemRepository.save(entity);

        return catalogItemMapper.toResponse(entity);
    }

    @Override
    public CatalogItemResponse getCatalogItem(Integer id) {
        log.info("Fetching CatalogItem id: {}", id);

        CatalogItem entity = catalogItemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATALOG_ITEM_NOT_FOUND));

        return catalogItemMapper.toResponse(entity);
    }

    @Override
    public List<CatalogItemResponse> getCatalogItems(Short hotelId) {
        if (hotelId != null) {
            log.info("Fetching all CatalogItems for hotelId: {}", hotelId);
            hotelRepository.findByIdAndIsDeletedFalse(hotelId)
                    .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
            return catalogItemRepository.findAllByHotelIdAndIsDeletedFalse(hotelId).stream()
                    .map(catalogItemMapper::toResponse)
                    .toList();
        } else {
            log.info("Fetching all active CatalogItems");
            return catalogItemRepository.findAllByIsDeletedFalse().stream()
                    .map(catalogItemMapper::toResponse)
                    .toList();
        }
    }

    @Override
    @Transactional
    public void deleteCatalogItem(Integer id) {
        log.info("Soft deleting CatalogItem id: {}", id);

        CatalogItem entity = catalogItemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATALOG_ITEM_NOT_FOUND));

        entity.setIsDeleted(true);
        catalogItemRepository.save(entity);
    }
}

