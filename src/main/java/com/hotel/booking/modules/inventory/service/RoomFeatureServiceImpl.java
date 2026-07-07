package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.RoomFeatureCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomFeatureUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomFeatureResponse;
import com.hotel.booking.modules.inventory.entity.RoomFeature;
import com.hotel.booking.modules.inventory.mapper.RoomFeatureMapper;
import com.hotel.booking.modules.inventory.repository.RoomFeatureRepository;
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
public class RoomFeatureServiceImpl implements RoomFeatureService {

    RoomFeatureRepository roomFeatureRepository;
    RoomFeatureMapper roomFeatureMapper;

    @Override
    @Transactional
    public RoomFeatureResponse createRoomFeature(RoomFeatureCreationRequest request) {
        log.info("Creating new room feature with code: {}", request.getCode());

        if (roomFeatureRepository.existsByCodeAndIsDeletedFalse(request.getCode())) {
            throw new AppException(ErrorCode.ROOM_FEATURE_CODE_EXISTED);
        }

        if (roomFeatureRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new AppException(ErrorCode.ROOM_FEATURE_NAME_EXISTED);
        }

        RoomFeature roomFeature = roomFeatureMapper.toEntity(request);
        roomFeature.setIsDeleted(false);
        RoomFeature savedRoomFeature = roomFeatureRepository.save(roomFeature);

        log.info("Room feature created successfully with id: {}", savedRoomFeature.getId());
        return roomFeatureMapper.toResponse(savedRoomFeature);
    }

    @Override
    public List<RoomFeatureResponse> getAllRoomFeatures() {
        log.info("Fetching all active room features");
        return roomFeatureRepository.findAllByIsDeletedFalse()
                .stream()
                .map(roomFeatureMapper::toResponse)
                .toList();
    }

    @Override
    public RoomFeatureResponse getRoomFeatureById(Short id) {
        log.info("Fetching room feature with id: {}", id);
        RoomFeature roomFeature = roomFeatureRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_FEATURE_NOT_FOUND));
        return roomFeatureMapper.toResponse(roomFeature);
    }

    @Override
    @Transactional
    public RoomFeatureResponse updateRoomFeature(Short id, RoomFeatureUpdateRequest request) {
        log.info("Updating room feature with id: {}", id);

        RoomFeature roomFeature = roomFeatureRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_FEATURE_NOT_FOUND));

        if (roomFeatureRepository.existsByCodeAndIdNotAndIsDeletedFalse(request.getCode(), id)) {
            throw new AppException(ErrorCode.ROOM_FEATURE_CODE_EXISTED);
        }

        if (roomFeatureRepository.existsByNameAndIdNotAndIsDeletedFalse(request.getName(), id)) {
            throw new AppException(ErrorCode.ROOM_FEATURE_NAME_EXISTED);
        }

        roomFeatureMapper.updateEntity(request, roomFeature);
        RoomFeature savedRoomFeature = roomFeatureRepository.save(roomFeature);

        log.info("Room feature updated successfully with id: {}", savedRoomFeature.getId());
        return roomFeatureMapper.toResponse(savedRoomFeature);
    }

    @Override
    @Transactional
    public void deleteRoomFeature(Short id) {
        log.info("Soft-deleting room feature with id: {}", id);

        RoomFeature roomFeature = roomFeatureRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_FEATURE_NOT_FOUND));

        roomFeature.setIsDeleted(true);
        roomFeatureRepository.save(roomFeature);

        log.info("Room feature soft-deleted successfully with id: {}", id);
    }
}
