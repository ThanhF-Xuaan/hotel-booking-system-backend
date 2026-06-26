package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.CatalogItemSyncItemRequest;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeCatalogItemSyncRequest;
import com.hotel.booking.modules.inventory.dto.response.AssignedCatalogItemResponse;
import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeCatalogItemResponse;
import com.hotel.booking.modules.inventory.entity.CatalogItem;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeCatalogItem;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeCatalogItemId;
import com.hotel.booking.modules.inventory.repository.CatalogItemRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeCatalogItemRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class HotelRoomTypeCatalogItemServiceImpl implements HotelRoomTypeCatalogItemService {

    HotelRoomTypeCatalogItemRepository hotelRoomTypeCatalogItemRepository;
    HotelRoomTypeRepository hotelRoomTypeRepository;
    CatalogItemRepository catalogItemRepository;

    @Override
    @Transactional
    public List<AssignedCatalogItemResponse> syncCatalogItems(Integer hotelRoomTypeId,
            HotelRoomTypeCatalogItemSyncRequest request) {
        log.info("Synchronizing catalog items for hotelRoomTypeId: {}", hotelRoomTypeId);

        // a. Validate HotelRoomType exists and is not deleted
        HotelRoomType hotelRoomType = hotelRoomTypeRepository.findByIdAndIsDeletedFalse(hotelRoomTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

        List<Integer> requestedIds = request.getItems().stream()
                .map(CatalogItemSyncItemRequest::getCatalogItemId)
                .toList();

        // Validate duplicates in request body to prevent Collector issues and 500 error
        Set<Integer> uniqueIds = Set.copyOf(requestedIds);
        if (uniqueIds.size() != requestedIds.size()) {
            throw new AppException(ErrorCode.DUPLICATE_CATALOG_ITEM_IN_REQUEST);
        }

        // b. Fetch all valid CatalogItem entities in ONE single batch query (avoid N+1)
        List<CatalogItem> dbItems = catalogItemRepository.findAllByIdInAndIsDeletedFalse(uniqueIds);

        // If count doesn't match, at least one requested item is invalid or
        // soft-deleted
        if (dbItems.size() != uniqueIds.size()) {
            throw new AppException(ErrorCode.CATALOG_ITEM_NOT_FOUND);
        }

        // c. Cross-Hotel validation: Catalog items must belong to the same hotel as the
        // room type
        Short targetHotelId = hotelRoomType.getHotel().getId();
        for (CatalogItem catalogItem : dbItems) {
            if (!catalogItem.getHotel().getId().equals(targetHotelId)) {
                log.warn("Hotel ID mismatch: Room type hotel is {}, but catalog item {} is owned by hotel {}",
                        targetHotelId, catalogItem.getId(), catalogItem.getHotel().getId());
                throw new AppException(ErrorCode.CATALOG_ITEM_HOTEL_MISMATCH);
            }
        }

        Map<Integer, CatalogItem> dbItemMap = dbItems.stream()
                .collect(Collectors.toMap(CatalogItem::getId, Function.identity()));

        // d. Fetch existing HotelRoomTypeCatalogItem records for this hotelRoomTypeId
        List<HotelRoomTypeCatalogItem> existingList = hotelRoomTypeCatalogItemRepository
                .findAllByHotelRoomTypeId(hotelRoomTypeId);
        Map<Integer, HotelRoomTypeCatalogItem> existingMap = existingList.stream()
                .collect(Collectors.toMap(e -> e.getId().getCatalogItemId(), Function.identity()));

        Map<Integer, CatalogItemSyncItemRequest> requestedMap = request.getItems().stream()
                .collect(Collectors.toMap(CatalogItemSyncItemRequest::getCatalogItemId, Function.identity()));

        // e. Perform the Diff & Sync algorithm:
        // Deletions: if an existing item mapping is NOT in the request -> DELETE it
        List<HotelRoomTypeCatalogItemId> deleteIds = existingList.stream()
                .filter(e -> !requestedMap.containsKey(e.getId().getCatalogItemId()))
                .map(HotelRoomTypeCatalogItem::getId)
                .toList();

        if (!deleteIds.isEmpty()) {
            log.info("Deleting {} catalog item mappings", deleteIds.size());
            hotelRoomTypeCatalogItemRepository.deleteAllByIdInBatch(deleteIds);
        }

        // Insertions and Updates
        List<HotelRoomTypeCatalogItem> toSave = new ArrayList<>();
        for (CatalogItemSyncItemRequest reqItem : request.getItems()) {
            Integer reqItemId = reqItem.getCatalogItemId();
            HotelRoomTypeCatalogItem existing = existingMap.get(reqItemId);

            if (existing == null) {
                // If requested item mapping is NOT in DB -> INSERT it
                CatalogItem catalogItem = dbItemMap.get(reqItemId);
                HotelRoomTypeCatalogItemId id = new HotelRoomTypeCatalogItemId(hotelRoomTypeId, reqItemId);
                HotelRoomTypeCatalogItem newMapping = HotelRoomTypeCatalogItem.builder()
                        .id(id)
                        .hotelRoomType(hotelRoomType)
                        .catalogItem(catalogItem)
                        .itemUsage(reqItem.getItemUsage())
                        .price(reqItem.getPrice())
                        .build();
                toSave.add(newMapping);
            } else {
                // If existing item mapping IS in request -> UPDATE it if changed
                boolean modified = false;
                if (!existing.getItemUsage().equals(reqItem.getItemUsage())) {
                    existing.setItemUsage(reqItem.getItemUsage());
                    modified = true;
                }
                if (existing.getPrice().compareTo(reqItem.getPrice()) != 0) {
                    existing.setPrice(reqItem.getPrice());
                    modified = true;
                }
                if (modified) {
                    toSave.add(existing);
                }
            }
        }

        if (!toSave.isEmpty()) {
            log.info("Saving/updating {} catalog item mappings", toSave.size());
            hotelRoomTypeCatalogItemRepository.saveAll(toSave);
        }

        // f. Map final state to response and return
        List<HotelRoomTypeCatalogItem> finalMappings = hotelRoomTypeCatalogItemRepository
                .findAllByHotelRoomTypeId(hotelRoomTypeId);
        return finalMappings.stream()
                .map(m -> AssignedCatalogItemResponse.builder()
                        .catalogItemId(m.getId().getCatalogItemId())
                        .itemName(m.getCatalogItem().getName())
                        .itemUsage(m.getItemUsage())
                        .price(m.getPrice())
                        .build())
                .toList();
    }

    @Override
    public List<HotelRoomTypeCatalogItemResponse> getCatalogItemsByHotelRoomTypeId(Integer hotelRoomTypeId) {
        log.info("Fetching catalog items for hotelRoomTypeId: {}", hotelRoomTypeId);

        hotelRoomTypeRepository.findByIdAndIsDeletedFalse(hotelRoomTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

        return hotelRoomTypeCatalogItemRepository.findAllByHotelRoomTypeId(hotelRoomTypeId).stream()
                .map(m -> HotelRoomTypeCatalogItemResponse.builder()
                        .catalogItemId(m.getId().getCatalogItemId())
                        .itemName(m.getCatalogItem().getName())
                        .itemUsage(m.getItemUsage())
                        .price(m.getPrice())
                        .build())
                .toList();
    }
}
