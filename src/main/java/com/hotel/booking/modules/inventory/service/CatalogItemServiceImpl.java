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
import com.hotel.booking.modules.pricing.entity.VatRule;
import com.hotel.booking.modules.pricing.repository.VatRuleRepository;
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
    VatRuleRepository vatRuleRepository;
    CatalogItemMapper catalogItemMapper;

    @Override
    @Transactional
    public CatalogItemResponse createCatalogItem(CatalogItemCreationRequest request) {
        log.info("Creating CatalogItem for hotelId: {}, vatRuleId: {}", request.getHotelId(), request.getVatRuleId());

        Hotel hotel = hotelRepository.findByIdAndIsDeletedFalse(request.getHotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        VatRule vatRule = vatRuleRepository.findByIdAndIsDeletedFalse(request.getVatRuleId())
                .orElseThrow(() -> new AppException(ErrorCode.VAT_RULE_NOT_FOUND));

        CatalogItem entity = catalogItemMapper.toEntity(request);
        entity.setHotel(hotel);
        entity.setVatRule(vatRule);
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

        if (!entity.getVatRule().getId().equals(request.getVatRuleId())) {
            VatRule vatRule = vatRuleRepository.findByIdAndIsDeletedFalse(request.getVatRuleId())
                    .orElseThrow(() -> new AppException(ErrorCode.VAT_RULE_NOT_FOUND));
            entity.setVatRule(vatRule);
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
