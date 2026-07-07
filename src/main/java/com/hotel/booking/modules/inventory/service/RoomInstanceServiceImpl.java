package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.RoomInstanceCreateRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomInstanceStatusUpdateRequest;
import com.hotel.booking.modules.inventory.dto.request.RoomInstanceUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.RoomInstanceResponse;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.RoomInstance;
import com.hotel.booking.modules.inventory.mapper.RoomInstanceMapper;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.inventory.repository.RoomInstanceRepository;
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
public class RoomInstanceServiceImpl implements RoomInstanceService {

    RoomInstanceRepository roomInstanceRepository;
    HotelRepository hotelRepository;
    HotelRoomTypeRepository hotelRoomTypeRepository;
    RoomInstanceMapper roomInstanceMapper;

    @Override
    @Transactional
    public RoomInstanceResponse createRoomInstance(RoomInstanceCreateRequest request) {
        log.info("Creating new room instance for hotelId: {}, roomNumber: {}", request.getHotelId(), request.getRoomNumber());

        // 1. Validate parent Hotel existence
        Hotel hotel = hotelRepository.findByIdAndIsDeletedFalse(request.getHotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        // 2. Validate parent HotelRoomType existence
        HotelRoomType hotelRoomType = hotelRoomTypeRepository.findByIdAndIsDeletedFalse(request.getHotelRoomTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

        // 3. Business Logic 1: Cross-Hotel Mismatch check
        if (!hotelRoomType.getHotel().getId().equals(request.getHotelId())) {
            log.error("Cross-hotel mismatch: room type hotel ID {} does not match request hotel ID {}", 
                    hotelRoomType.getHotel().getId(), request.getHotelId());
            throw new AppException(ErrorCode.ROOM_INSTANCE_HOTEL_MISMATCH);
        }

        // 4. Business Logic 2: Unique Room Number check
        if (roomInstanceRepository.existsByHotelIdAndRoomNumberAndIsDeletedFalse(request.getHotelId(), request.getRoomNumber())) {
            log.error("Room number {} already exists in hotel ID {}", request.getRoomNumber(), request.getHotelId());
            throw new AppException(ErrorCode.ROOM_INSTANCE_ALREADY_EXISTS);
        }

        // 5. Business Logic 3: Physical Capacity Check
        long activeCount = roomInstanceRepository.countByHotelRoomTypeIdAndIsDeletedFalse(request.getHotelRoomTypeId());
        if (activeCount >= hotelRoomType.getTotalQuantity()) {
            log.error("Physical capacity exceeded for hotelRoomTypeId {}: active count is {}, total quantity is {}", 
                    request.getHotelRoomTypeId(), activeCount, hotelRoomType.getTotalQuantity());
            throw new AppException(ErrorCode.ROOM_INSTANCE_EXCEEDS_TOTAL_QUANTITY);
        }

        // 6. Map and persist
        RoomInstance roomInstance = roomInstanceMapper.toEntity(request);
        roomInstance.setHotel(hotel);
        roomInstance.setHotelRoomType(hotelRoomType);
        roomInstance.setIsDeleted(false);

        RoomInstance saved = roomInstanceRepository.save(roomInstance);
        log.info("Room instance created successfully with ID: {}", saved.getId());
        return roomInstanceMapper.toResponse(saved);
    }

    @Override
    public RoomInstanceResponse getRoomInstanceById(Integer id) {
        log.info("Fetching room instance by ID: {}", id);
        RoomInstance roomInstance = roomInstanceRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_INSTANCE_NOT_FOUND));
        return roomInstanceMapper.toResponse(roomInstance);
    }

    @Override
    public List<RoomInstanceResponse> getRoomInstances(Short hotelId) {
        if (hotelId != null) {
            log.info("Fetching all active room instances for hotelId: {}", hotelId);
            // Verify hotel existence first
            hotelRepository.findByIdAndIsDeletedFalse(hotelId)
                    .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
            return roomInstanceRepository.findAllByHotelIdAndIsDeletedFalse(hotelId).stream()
                    .map(roomInstanceMapper::toResponse)
                    .toList();
        } else {
            log.info("Fetching all active room instances (no hotel filter)");
            return roomInstanceRepository.findAllByIsDeletedFalse().stream()
                    .map(roomInstanceMapper::toResponse)
                    .toList();
        }
    }

    @Override
    @Transactional
    public RoomInstanceResponse updateRoomInstance(Integer id, RoomInstanceUpdateRequest request) {
        log.info("Updating room instance ID: {}", id);

        // 1. Retrieve existing RoomInstance
        RoomInstance roomInstance = roomInstanceRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_INSTANCE_NOT_FOUND));

        Short hotelId = roomInstance.getHotel().getId();

        // 2. Validate parent HotelRoomType existence
        HotelRoomType hotelRoomType = hotelRoomTypeRepository.findByIdAndIsDeletedFalse(request.getHotelRoomTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

        // 3. Business Logic 1: Cross-Hotel Mismatch check (target room type must belong to current hotel)
        if (!hotelRoomType.getHotel().getId().equals(hotelId)) {
            log.error("Cross-hotel mismatch on update: room type hotel ID {} does not match existing hotel ID {}", 
                    hotelRoomType.getHotel().getId(), hotelId);
            throw new AppException(ErrorCode.ROOM_INSTANCE_HOTEL_MISMATCH);
        }

        // 4. Business Logic 2: Unique Room Number check (with IdNot)
        if (roomInstanceRepository.existsByHotelIdAndRoomNumberAndIdNotAndIsDeletedFalse(hotelId, request.getRoomNumber(), id)) {
            log.error("Room number {} already exists in hotel ID {} (other than room ID {})", 
                    request.getRoomNumber(), hotelId, id);
            throw new AppException(ErrorCode.ROOM_INSTANCE_ALREADY_EXISTS);
        }

        // 5. Business Logic 3: Physical Capacity Check (if changing hotelRoomTypeId)
        if (!roomInstance.getHotelRoomType().getId().equals(request.getHotelRoomTypeId())) {
            long activeCount = roomInstanceRepository.countByHotelRoomTypeIdAndIsDeletedFalse(request.getHotelRoomTypeId());
            if (activeCount >= hotelRoomType.getTotalQuantity()) {
                log.error("Physical capacity exceeded for hotelRoomTypeId {}: active count is {}, total quantity is {}", 
                        request.getHotelRoomTypeId(), activeCount, hotelRoomType.getTotalQuantity());
                throw new AppException(ErrorCode.ROOM_INSTANCE_EXCEEDS_TOTAL_QUANTITY);
            }
        }

        // 6. Map and update
        roomInstanceMapper.updateEntity(request, roomInstance);
        roomInstance.setHotelRoomType(hotelRoomType);

        RoomInstance updated = roomInstanceRepository.save(roomInstance);
        log.info("Room instance updated successfully with ID: {}", updated.getId());
        return roomInstanceMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public RoomInstanceResponse updateRoomInstanceStatus(Integer id, RoomInstanceStatusUpdateRequest request) {
        log.info("Updating room instance status for ID: {} to {}", id, request.getCurrentStatus());

        RoomInstance roomInstance = roomInstanceRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_INSTANCE_NOT_FOUND));

        roomInstance.setCurrentStatus(request.getCurrentStatus());
        RoomInstance updated = roomInstanceRepository.save(roomInstance);

        log.info("Room instance status updated successfully with ID: {}", updated.getId());
        return roomInstanceMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteRoomInstance(Integer id) {
        log.info("Soft-deleting room instance ID: {}", id);

        RoomInstance roomInstance = roomInstanceRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_INSTANCE_NOT_FOUND));

        roomInstance.setIsDeleted(true);
        roomInstanceRepository.save(roomInstance);
        log.info("Room instance soft-deleted successfully with ID: {}", id);
    }
}
