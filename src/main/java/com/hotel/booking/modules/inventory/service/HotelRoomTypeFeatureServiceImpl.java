package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeFeatureSyncRequest;
import com.hotel.booking.modules.inventory.dto.response.AssignedFeatureResponse;
import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeFeatureResponse;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeFeature;
import com.hotel.booking.modules.inventory.entity.HotelRoomTypeFeatureId;
import com.hotel.booking.modules.inventory.entity.RoomFeature;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeFeatureRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.inventory.repository.RoomFeatureRepository;
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
public class HotelRoomTypeFeatureServiceImpl implements HotelRoomTypeFeatureService {

        HotelRoomTypeFeatureRepository hotelRoomTypeFeatureRepository;
        HotelRoomTypeRepository hotelRoomTypeRepository;
        RoomFeatureRepository roomFeatureRepository;

        @Override
        @Transactional
        public List<AssignedFeatureResponse> syncFeatures(Integer hotelRoomTypeId,
                        HotelRoomTypeFeatureSyncRequest request) {
                log.info("Synchronizing features for hotelRoomTypeId: {}", hotelRoomTypeId);

                // a. Validate HotelRoomType exists and is not deleted
                HotelRoomType hotelRoomType = hotelRoomTypeRepository.findByIdAndIsDeletedFalse(hotelRoomTypeId)
                                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

                List<Short> requestedFeatureIds = request.getRoomFeatureIds();

                // Validate duplicates in request body to prevent Collector issues and 500 error
                Set<Short> uniqueFeatureIds = Set.copyOf(requestedFeatureIds);
                if (uniqueFeatureIds.size() != requestedFeatureIds.size()) {
                        throw new AppException(ErrorCode.DUPLICATE_FEATURE_IN_REQUEST);
                }

                // b. Fetch all valid RoomFeature entities in ONE single batch query (avoid N+1)
                List<RoomFeature> dbFeatures = roomFeatureRepository.findAllByIdInAndIsDeletedFalse(uniqueFeatureIds);

                // If count doesn't match, at least one requested feature is invalid or
                // soft-deleted
                if (dbFeatures.size() != uniqueFeatureIds.size()) {
                        throw new AppException(ErrorCode.ROOM_FEATURE_NOT_FOUND);
                }

                Map<Short, RoomFeature> dbFeatureMap = dbFeatures.stream()
                                .collect(Collectors.toMap(RoomFeature::getId, Function.identity()));

                // c. Fetch existing HotelRoomTypeFeature records for this hotelRoomTypeId
                List<HotelRoomTypeFeature> existingFeatures = hotelRoomTypeFeatureRepository
                                .findAllByHotelRoomTypeId(hotelRoomTypeId);
                Map<Short, HotelRoomTypeFeature> existingMap = existingFeatures.stream()
                                .collect(Collectors.toMap(f -> f.getId().getRoomFeatureId(), Function.identity()));

                // d. Perform the Diff & Sync algorithm:
                // Deletions: if an existing feature is NOT in the request -> DELETE it
                List<HotelRoomTypeFeatureId> deleteIds = existingFeatures.stream()
                                .filter(f -> !uniqueFeatureIds.contains(f.getId().getRoomFeatureId()))
                                .map(HotelRoomTypeFeature::getId)
                                .toList();

                // e. Execute repository.deleteAllByIdInBatch() for deletions
                if (!deleteIds.isEmpty()) {
                        log.info("Deleting {} feature mappings", deleteIds.size());
                        hotelRoomTypeFeatureRepository.deleteAllByIdInBatch(deleteIds);
                }

                // Insertions: if a requested feature is NOT in the DB -> INSERT it
                List<HotelRoomTypeFeature> toInsert = new ArrayList<>();
                for (Short reqFeatureId : uniqueFeatureIds) {
                        HotelRoomTypeFeature existing = existingMap.get(reqFeatureId);

                        if (existing == null) {
                                RoomFeature roomFeature = dbFeatureMap.get(reqFeatureId);
                                HotelRoomTypeFeatureId id = new HotelRoomTypeFeatureId(hotelRoomTypeId, reqFeatureId);
                                HotelRoomTypeFeature newFeature = HotelRoomTypeFeature.builder()
                                                .id(id)
                                                .hotelRoomType(hotelRoomType)
                                                .roomFeature(roomFeature)
                                                .build();
                                toInsert.add(newFeature);
                        }
                }

                if (!toInsert.isEmpty()) {
                        log.info("Inserting {} feature mappings", toInsert.size());
                        hotelRoomTypeFeatureRepository.saveAll(toInsert);
                }

                // f. Map the final state to a List and return
                List<HotelRoomTypeFeature> finalFeatures = hotelRoomTypeFeatureRepository
                                .findAllByHotelRoomTypeId(hotelRoomTypeId);
                return finalFeatures.stream()
                                .map(f -> AssignedFeatureResponse.builder()
                                                .roomFeatureId(f.getId().getRoomFeatureId())
                                                .featureName(f.getRoomFeature().getName())
                                                .build())
                                .toList();
        }

        @Override
        public List<HotelRoomTypeFeatureResponse> getFeaturesByHotelRoomTypeId(Integer hotelRoomTypeId) {
                log.info("Fetching features for hotelRoomTypeId: {}", hotelRoomTypeId);

                hotelRoomTypeRepository.findByIdAndIsDeletedFalse(hotelRoomTypeId)
                                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

                return hotelRoomTypeFeatureRepository.findAllByHotelRoomTypeId(hotelRoomTypeId).stream()
                                .map(f -> HotelRoomTypeFeatureResponse.builder()
                                                .roomFeatureId(f.getId().getRoomFeatureId())
                                                .featureName(f.getRoomFeature().getName())
                                                .build())
                                .toList();
        }
}
