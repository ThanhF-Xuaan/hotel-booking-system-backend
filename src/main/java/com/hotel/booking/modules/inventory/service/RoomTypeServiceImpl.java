package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.RoomTypeCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomTypeUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomTypeResponse;
import com.hotel.booking.modules.inventory.entity.RoomType;
import com.hotel.booking.modules.inventory.mapper.RoomTypeMapper;
import com.hotel.booking.modules.inventory.repository.RoomTypeRepository;
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
public class RoomTypeServiceImpl implements RoomTypeService {

    RoomTypeRepository roomTypeRepository;
    RoomTypeMapper roomTypeMapper;

    @Override
    @Transactional
    public RoomTypeResponse createRoomType(RoomTypeCreationRequest request) {
        log.info("Creating new room type with code: {}", request.getCode());

        if (roomTypeRepository.existsByCodeAndIsDeletedFalse(request.getCode())) {
            throw new AppException(ErrorCode.ROOM_TYPE_CODE_EXISTED);
        }

        if (roomTypeRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new AppException(ErrorCode.ROOM_TYPE_NAME_EXISTED);
        }

        RoomType roomType = roomTypeMapper.toEntity(request);
        roomType.setIsDeleted(false);
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        log.info("Room type created successfully with id: {}", savedRoomType.getId());
        return roomTypeMapper.toResponse(savedRoomType);
    }

    @Override
    public List<RoomTypeResponse> getAllRoomTypes() {
        log.info("Fetching all active room types");
        return roomTypeRepository.findAllByIsDeletedFalse()
                .stream()
                .map(roomTypeMapper::toResponse)
                .toList();
    }

    @Override
    public RoomTypeResponse getRoomTypeById(Short id) {
        log.info("Fetching room type with id: {}", id);
        RoomType roomType = roomTypeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        return roomTypeMapper.toResponse(roomType);
    }

    @Override
    @Transactional
    public RoomTypeResponse updateRoomType(Short id, RoomTypeUpdateRequest request) {
        log.info("Updating room type with id: {}", id);

        RoomType roomType = roomTypeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        if (roomTypeRepository.existsByNameAndIdNotAndIsDeletedFalse(request.getName(), id)) {
            throw new AppException(ErrorCode.ROOM_TYPE_NAME_EXISTED);
        }

        roomTypeMapper.updateEntity(request, roomType);
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        log.info("Room type updated successfully with id: {}", savedRoomType.getId());
        return roomTypeMapper.toResponse(savedRoomType);
    }

    @Override
    @Transactional
    public void deleteRoomType(Short id) {
        log.info("Soft-deleting room type with id: {}", id);

        RoomType roomType = roomTypeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        roomType.setIsDeleted(true);
        roomTypeRepository.save(roomType);

        log.info("Room type soft-deleted successfully with id: {}", id);
    }
}
