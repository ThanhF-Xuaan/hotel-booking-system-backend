package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.RoomBedCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomBedUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomBedResponse;
import com.hotel.booking.modules.inventory.entity.RoomBed;
import com.hotel.booking.modules.inventory.mapper.RoomBedMapper;
import com.hotel.booking.modules.inventory.repository.RoomBedRepository;
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
public class RoomBedServiceImpl implements RoomBedService {

    RoomBedRepository roomBedRepository;
    RoomBedMapper roomBedMapper;

    @Override
    @Transactional
    public RoomBedResponse createRoomBed(RoomBedCreationRequest request) {
        log.info("Creating new room bed with name: {}", request.getName());

        if (roomBedRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new AppException(ErrorCode.ROOM_BED_NAME_EXISTED);
        }

        RoomBed roomBed = roomBedMapper.toEntity(request);
        roomBed.setIsDeleted(false);
        RoomBed savedRoomBed = roomBedRepository.save(roomBed);

        log.info("Room bed created successfully with id: {}", savedRoomBed.getId());
        return roomBedMapper.toResponse(savedRoomBed);
    }

    @Override
    public List<RoomBedResponse> getAllRoomBeds() {
        log.info("Fetching all active room beds");
        return roomBedRepository.findAllByIsDeletedFalse()
                .stream()
                .map(roomBedMapper::toResponse)
                .toList();
    }

    @Override
    public RoomBedResponse getRoomBedById(Short id) {
        log.info("Fetching room bed with id: {}", id);
        RoomBed roomBed = roomBedRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_BED_NOT_FOUND));
        return roomBedMapper.toResponse(roomBed);
    }

    @Override
    @Transactional
    public RoomBedResponse updateRoomBed(Short id, RoomBedUpdateRequest request) {
        log.info("Updating room bed with id: {}", id);

        RoomBed roomBed = roomBedRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_BED_NOT_FOUND));

        if (roomBedRepository.existsByNameAndIdNotAndIsDeletedFalse(request.getName(), id)) {
            throw new AppException(ErrorCode.ROOM_BED_NAME_EXISTED);
        }

        roomBedMapper.updateEntity(request, roomBed);
        RoomBed savedRoomBed = roomBedRepository.save(roomBed);

        log.info("Room bed updated successfully with id: {}", savedRoomBed.getId());
        return roomBedMapper.toResponse(savedRoomBed);
    }

    @Override
    @Transactional
    public void deleteRoomBed(Short id) {
        log.info("Soft-deleting room bed with id: {}", id);

        RoomBed roomBed = roomBedRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_BED_NOT_FOUND));

        roomBed.setIsDeleted(true);
        roomBedRepository.save(roomBed);

        log.info("Room bed soft-deleted successfully with id: {}", id);
    }
}
