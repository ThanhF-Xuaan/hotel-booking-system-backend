package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeBedSyncRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomBedSyncItemRequest;
import com.hotel.booking.modules.inventory.dto.response.AssignedBedResponse;
import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeBedResponse;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeBed;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeBedId;
import com.hotel.booking.modules.inventory.entity.RoomBed;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeBedRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.inventory.repository.RoomBedRepository;
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
public class HotelRoomTypeBedServiceImpl implements HotelRoomTypeBedService {

    HotelRoomTypeBedRepository hotelRoomTypeBedRepository;
    HotelRoomTypeRepository hotelRoomTypeRepository;
    RoomBedRepository roomBedRepository;

    @Override
    @Transactional
    public List<AssignedBedResponse> syncBeds(Integer hotelRoomTypeId, HotelRoomTypeBedSyncRequest request) {
        log.info("Synchronizing beds for hotelRoomTypeId: {}", hotelRoomTypeId);

        // a. Validate HotelRoomType exists and is not deleted
        HotelRoomType hotelRoomType = hotelRoomTypeRepository.findByIdAndIsDeletedFalse(hotelRoomTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

        // b. Validate physical bed capacity limit (standard beds quantity sum + maxExtraBeds <= maxBeds)
        int requestedBedsTotal = request.getBeds().stream()
                .mapToInt(b -> b.getQuantity())
                .sum();

        if (requestedBedsTotal + hotelRoomType.getMaxExtraBeds() > hotelRoomType.getMaxBeds()) {
            log.error("Bed capacity limit validation failed for hotelRoomTypeId {}: requested standard beds total {}, extra beds {}, max beds allowed {}",
                    hotelRoomTypeId, requestedBedsTotal, hotelRoomType.getMaxExtraBeds(), hotelRoomType.getMaxBeds());
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_BED_LIMIT_EXCEEDED);
        }

        // Get requested bed IDs
        List<Short> requestedBedIds = request.getBeds().stream()
                .map(RoomBedSyncItemRequest::getRoomBedId)
                .toList();

        // Validate duplicates in request body to prevent Collector issues and 500 error
        Set<Short> uniqueBedIds = Set.copyOf(requestedBedIds);
        if (uniqueBedIds.size() != requestedBedIds.size()) {
            throw new AppException(ErrorCode.DUPLICATE_BED_IN_REQUEST);
        }

        // b. Fetch all valid RoomBed entities in ONE single batch query (avoid N+1)
        List<RoomBed> dbBeds = roomBedRepository.findAllByIdInAndIsDeletedFalse(uniqueBedIds);

        // If count doesn't match, at least one requested bed is invalid or soft-deleted
        if (dbBeds.size() != uniqueBedIds.size()) {
            throw new AppException(ErrorCode.ROOM_BED_NOT_FOUND);
        }

        Map<Short, RoomBed> dbBedMap = dbBeds.stream()
                .collect(Collectors.toMap(RoomBed::getId, Function.identity()));

        // c. Fetch existing HotelRoomTypeBed records for this hotelRoomTypeId
        List<HotelRoomTypeBed> existingBeds = hotelRoomTypeBedRepository.findAllByHotelRoomTypeId(hotelRoomTypeId);
        Map<Short, HotelRoomTypeBed> existingMap = existingBeds.stream()
                .collect(Collectors.toMap(b -> b.getId().getRoomBedId(), Function.identity()));

        Map<Short, Short> requestedMap = request.getBeds().stream()
                .collect(Collectors.toMap(RoomBedSyncItemRequest::getRoomBedId, RoomBedSyncItemRequest::getQuantity));

        // d. Perform the Diff & Sync algorithm:
        // Deletions: if an existing bed is NOT in the request -> DELETE it
        List<HotelRoomTypeBedId> deleteIds = existingBeds.stream()
                .filter(b -> !requestedMap.containsKey(b.getId().getRoomBedId()))
                .map(HotelRoomTypeBed::getId)
                .toList();

        // e. Execute repository.deleteAllByIdInBatch() for deletions
        if (!deleteIds.isEmpty()) {
            log.info("Deleting {} bed mappings", deleteIds.size());
            hotelRoomTypeBedRepository.deleteAllByIdInBatch(deleteIds);
        }

        // Insertions and updates: execute repository.saveAll() for BOTH insertions and
        // updates
        List<HotelRoomTypeBed> toSave = new ArrayList<>();
        for (RoomBedSyncItemRequest reqItem : request.getBeds()) {
            Short reqBedId = reqItem.getRoomBedId();
            Short reqQty = reqItem.getQuantity();
            HotelRoomTypeBed existing = existingMap.get(reqBedId);

            if (existing == null) {
                // If a requested bed is NOT in the DB -> INSERT it
                RoomBed roomBed = dbBedMap.get(reqBedId);
                HotelRoomTypeBedId id = new HotelRoomTypeBedId(hotelRoomTypeId, reqBedId);
                HotelRoomTypeBed newBed = HotelRoomTypeBed.builder()
                        .id(id)
                        .hotelRoomType(hotelRoomType)
                        .roomBed(roomBed)
                        .quantity(reqQty)
                        .build();
                toSave.add(newBed);
            } else {
                // If an existing bed IS in the request -> UPDATE its quantity if changed
                if (!existing.getQuantity().equals(reqQty)) {
                    existing.setQuantity(reqQty);
                    toSave.add(existing);
                }
            }
        }

        if (!toSave.isEmpty()) {
            log.info("Saving/updating {} bed mappings", toSave.size());
            hotelRoomTypeBedRepository.saveAll(toSave);
        }

        // f. Map the final state to a List and return
        List<HotelRoomTypeBed> finalBeds = hotelRoomTypeBedRepository.findAllByHotelRoomTypeId(hotelRoomTypeId);
        return finalBeds.stream()
                .map(b -> AssignedBedResponse.builder()
                        .roomBedId(b.getId().getRoomBedId())
                        .bedName(b.getRoomBed().getName())
                        .quantity(b.getQuantity())
                        .build())
                .toList();
    }

    @Override
    public List<HotelRoomTypeBedResponse> getBedsByHotelRoomTypeId(Integer hotelRoomTypeId) {
        log.info("Fetching beds for hotelRoomTypeId: {}", hotelRoomTypeId);

        hotelRoomTypeRepository.findByIdAndIsDeletedFalse(hotelRoomTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

        return hotelRoomTypeBedRepository.findAllByHotelRoomTypeId(hotelRoomTypeId).stream()
                .map(b -> HotelRoomTypeBedResponse.builder()
                        .roomBedId(b.getId().getRoomBedId())
                        .bedName(b.getRoomBed().getName())
                        .quantity(b.getQuantity())
                        .build())
                .toList();
    }
}
