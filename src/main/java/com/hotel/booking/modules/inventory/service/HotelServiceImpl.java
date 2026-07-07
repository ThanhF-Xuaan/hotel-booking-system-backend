package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.dto.request.HotelCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.HotelUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.HotelResponse;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.mapper.HotelMapper;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
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
public class HotelServiceImpl implements HotelService {

    HotelRepository hotelRepository;
    HotelMapper hotelMapper;

    @Override
    @Transactional
    public HotelResponse createHotel(HotelCreationRequest request) {
        log.info("Creating new hotel with name: {}", request.getName());

        if (hotelRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.HOTEL_NAME_EXISTED);
        }

        Hotel hotel = hotelMapper.toEntity(request);
        hotel.setIsDeleted(false);
        Hotel savedHotel = hotelRepository.save(hotel);

        log.info("Hotel created successfully with id: {}", savedHotel.getId());
        return hotelMapper.toResponse(savedHotel);
    }

    @Override
    public List<HotelResponse> getAllHotels() {
        log.info("Fetching all active hotels");
        return hotelRepository.findAllByIsDeletedFalse()
                .stream()
                .map(hotelMapper::toResponse)
                .toList();
    }

    @Override
    public HotelResponse getHotelById(Short id) {
        log.info("Fetching hotel with id: {}", id);
        Hotel hotel = hotelRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        return hotelMapper.toResponse(hotel);
    }

    @Override
    @Transactional
    public HotelResponse updateHotel(Short id, HotelUpdateRequest request) {
        log.info("Updating hotel with id: {}", id);

        Hotel hotel = hotelRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        if (hotelRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new AppException(ErrorCode.HOTEL_NAME_EXISTED);
        }

        hotelMapper.updateEntity(request, hotel);
        Hotel savedHotel = hotelRepository.save(hotel);

        log.info("Hotel updated successfully with id: {}", savedHotel.getId());
        return hotelMapper.toResponse(savedHotel);
    }

    @Override
    @Transactional
    public void deleteHotel(Short id) {
        log.info("Soft-deleting hotel with id: {}", id);

        Hotel hotel = hotelRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        hotel.setIsDeleted(true);
        hotelRepository.save(hotel);

        log.info("Hotel soft-deleted successfully with id: {}", id);
    }
}
