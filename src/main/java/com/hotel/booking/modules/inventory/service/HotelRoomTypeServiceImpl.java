package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.HotelRoomTypeUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.HotelRoomTypeResponse;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.RoomType;
import com.hotel.booking.modules.inventory.mapper.HotelRoomTypeMapper;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.inventory.repository.RoomTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class HotelRoomTypeServiceImpl implements HotelRoomTypeService {

    HotelRoomTypeRepository hotelRoomTypeRepository;
    HotelRepository hotelRepository;
    RoomTypeRepository roomTypeRepository;
    HotelRoomTypeMapper hotelRoomTypeMapper;

    @Override
    @Transactional
    public HotelRoomTypeResponse createHotelRoomType(HotelRoomTypeCreationRequest request) {
        log.info("Creating HotelRoomType mapping for hotelId: {}, roomTypeId: {}", request.getHotelId(), request.getRoomTypeId());

        // 1. Foreign Key Validations
        Hotel hotel = hotelRepository.findByIdAndIsDeletedFalse(request.getHotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        RoomType roomType = roomTypeRepository.findByIdAndIsDeletedFalse(request.getRoomTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        // 2. Unique Check
        if (hotelRoomTypeRepository.existsByHotelIdAndRoomTypeIdAndIsDeletedFalse(request.getHotelId(), request.getRoomTypeId())) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_ALREADY_EXISTS);
        }

        // 3. Proactive Capacity and Pricing Validations
        validateCapacityAndPricing(request);

        // 4. Map & Persist
        HotelRoomType entity = hotelRoomTypeMapper.toEntity(request);
        entity.setHotel(hotel);
        entity.setRoomType(roomType);
        entity.setIsDeleted(false);

        entity = hotelRoomTypeRepository.save(entity);
        return hotelRoomTypeMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public HotelRoomTypeResponse updateHotelRoomType(Integer id, HotelRoomTypeUpdateRequest request) {
        log.info("Updating HotelRoomType id: {}", id);

        HotelRoomType entity = hotelRoomTypeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

        // Proactive Capacity and Pricing Validations
        validateCapacityAndPricing(request);

        hotelRoomTypeMapper.updateHotelRoomType(request, entity);
        entity = hotelRoomTypeRepository.save(entity);

        return hotelRoomTypeMapper.toResponse(entity);
    }

    @Override
    public HotelRoomTypeResponse getHotelRoomType(Integer id) {
        log.info("Fetching HotelRoomType id: {}", id);

        HotelRoomType entity = hotelRoomTypeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

        return hotelRoomTypeMapper.toResponse(entity);
    }

    @Override
    public List<HotelRoomTypeResponse> getHotelRoomTypes(Short hotelId) {
        if (hotelId != null) {
            log.info("Fetching all HotelRoomTypes for hotelId: {}", hotelId);
            hotelRepository.findByIdAndIsDeletedFalse(hotelId)
                    .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
            return hotelRoomTypeRepository.findAllByHotelIdAndIsDeletedFalse(hotelId).stream()
                    .map(hotelRoomTypeMapper::toResponse)
                    .toList();
        } else {
            log.info("Fetching all active HotelRoomTypes");
            return hotelRoomTypeRepository.findAllByIsDeletedFalse().stream()
                    .map(hotelRoomTypeMapper::toResponse)
                    .toList();
        }
    }

    @Override
    @Transactional
    public void deleteHotelRoomType(Integer id) {
        log.info("Soft deleting HotelRoomType id: {}", id);

        HotelRoomType entity = hotelRoomTypeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ROOM_TYPE_NOT_FOUND));

        entity.setIsDeleted(true);
        hotelRoomTypeRepository.save(entity);
    }

    private void validateCapacityAndPricing(HotelRoomTypeCreationRequest request) {
        if (request.getBasePrice() != null && request.getBasePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_PRICE_INVALID);
        }
        if (request.getTotalQuantity() != null && request.getTotalQuantity() < 0) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_QUANTITY_INVALID);
        }
        if (request.getStandardAdults() > request.getMaxAdults()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_INVALID_ADULTS_CAPACITY);
        }
        if (request.getStandardChildren() > request.getMaxChildren()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_INVALID_CHILDREN_CAPACITY);
        }
        if (request.getStandardAdults() + request.getStandardChildren() > request.getMaxTotalGuests()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_INVALID_TOTAL_GUEST_CAPACITY);
        }
        if (request.getMaxAdults() > request.getMaxTotalGuests()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_MAX_ADULTS_EXCEED_TOTAL);
        }
        if (request.getMaxChildren() > request.getMaxTotalGuests()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_MAX_CHILDREN_EXCEED_TOTAL);
        }
        if (request.getMaxBeds() != null && request.getMaxBeds() < 0) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_MAX_BEDS_MIN_ZERO);
        }
        if (request.getMaxExtraBeds() != null && request.getMaxBeds() != null && request.getMaxExtraBeds() > request.getMaxBeds()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_MAX_EXTRA_BEDS_EXCEED_MAX_BEDS);
        }
    }

    private void validateCapacityAndPricing(HotelRoomTypeUpdateRequest request) {
        if (request.getBasePrice() != null && request.getBasePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_PRICE_INVALID);
        }
        if (request.getTotalQuantity() != null && request.getTotalQuantity() < 0) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_QUANTITY_INVALID);
        }
        if (request.getStandardAdults() > request.getMaxAdults()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_INVALID_ADULTS_CAPACITY);
        }
        if (request.getStandardChildren() > request.getMaxChildren()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_INVALID_CHILDREN_CAPACITY);
        }
        if (request.getStandardAdults() + request.getStandardChildren() > request.getMaxTotalGuests()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_INVALID_TOTAL_GUEST_CAPACITY);
        }
        if (request.getMaxAdults() > request.getMaxTotalGuests()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_MAX_ADULTS_EXCEED_TOTAL);
        }
        if (request.getMaxChildren() > request.getMaxTotalGuests()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_MAX_CHILDREN_EXCEED_TOTAL);
        }
        if (request.getMaxBeds() != null && request.getMaxBeds() < 0) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_MAX_BEDS_MIN_ZERO);
        }
        if (request.getMaxExtraBeds() != null && request.getMaxBeds() != null && request.getMaxExtraBeds() > request.getMaxBeds()) {
            throw new AppException(ErrorCode.HOTEL_ROOM_TYPE_MAX_EXTRA_BEDS_EXCEED_MAX_BEDS);
        }
    }
}
