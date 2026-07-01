package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.HotelAgePolicyUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.HotelAgePolicyResponse;
import com.hotel.booking.modules.pricing.entity.HotelAgePolicy;
import com.hotel.booking.modules.pricing.mapper.HotelAgePolicyMapper;
import com.hotel.booking.modules.pricing.repository.HotelAgePolicyRepository;
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
public class HotelAgePolicyServiceImpl implements HotelAgePolicyService {

    HotelAgePolicyRepository hotelAgePolicyRepository;
    HotelRepository hotelRepository;
    HotelAgePolicyMapper hotelAgePolicyMapper;

    @Override
    @Transactional
    public HotelAgePolicyResponse createPolicy(HotelAgePolicyCreateRequest request) {
        log.info("Creating new hotel age policy for hotel ID: {}, guestType: {}", request.getHotelId(), request.getGuestType());

        Hotel hotel = validateAndGetHotel(request.getHotelId());
        validateAgeRange(request.getMinAge(), request.getMaxAge());

        if (hotelAgePolicyRepository.existsByHotelIdAndGuestTypeAndIsDeletedFalse(request.getHotelId(), request.getGuestType())) {
            throw new AppException(ErrorCode.AGE_POLICY_DUPLICATE_GUEST_TYPE);
        }

        HotelAgePolicy entity = hotelAgePolicyMapper.toEntity(request);
        entity.setHotel(hotel);
        if (entity.getStatus() == null) {
            entity.setStatus(ActiveStatus.ACTIVE);
        }
        entity.setIsDeleted(false);

        HotelAgePolicy saved = hotelAgePolicyRepository.save(entity);
        log.info("Hotel age policy created successfully with ID: {}", saved.getId());
        return hotelAgePolicyMapper.toResponse(saved);
    }

    @Override
    public HotelAgePolicyResponse getPolicyById(Short id) {
        log.info("Fetching hotel age policy by ID: {}", id);
        HotelAgePolicy entity = hotelAgePolicyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.AGE_POLICY_NOT_FOUND));
        return hotelAgePolicyMapper.toResponse(entity);
    }

    @Override
    public List<HotelAgePolicyResponse> getPolicies(Short hotelId) {
        if (hotelId != null) {
            log.info("Fetching all active hotel age policies for hotel ID: {}", hotelId);
            validateAndGetHotel(hotelId);
            return hotelAgePolicyRepository.findAllByHotelIdAndIsDeletedFalse(hotelId).stream()
                    .map(hotelAgePolicyMapper::toResponse)
                    .toList();
        } else {
            log.info("Fetching all active hotel age policies");
            return hotelAgePolicyRepository.findAllByIsDeletedFalse().stream()
                    .map(hotelAgePolicyMapper::toResponse)
                    .toList();
        }
    }

    @Override
    @Transactional
    public HotelAgePolicyResponse updatePolicy(Short id, HotelAgePolicyUpdateRequest request) {
        log.info("Updating hotel age policy ID: {}", id);

        HotelAgePolicy entity = hotelAgePolicyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.AGE_POLICY_NOT_FOUND));

        Hotel hotel = validateAndGetHotel(request.getHotelId());
        validateAgeRange(request.getMinAge(), request.getMaxAge());

        if (hotelAgePolicyRepository.existsByHotelIdAndGuestTypeAndIdNotAndIsDeletedFalse(request.getHotelId(), request.getGuestType(), id)) {
            throw new AppException(ErrorCode.AGE_POLICY_DUPLICATE_GUEST_TYPE);
        }

        hotelAgePolicyMapper.updateEntity(request, entity);
        entity.setHotel(hotel);

        HotelAgePolicy updated = hotelAgePolicyRepository.save(entity);
        log.info("Hotel age policy updated successfully with ID: {}", updated.getId());
        return hotelAgePolicyMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deletePolicy(Short id) {
        log.info("Soft-deleting hotel age policy ID: {}", id);
        HotelAgePolicy entity = hotelAgePolicyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.AGE_POLICY_NOT_FOUND));

        entity.setIsDeleted(true);
        hotelAgePolicyRepository.save(entity);
        log.info("Hotel age policy soft-deleted successfully with ID: {}", id);
    }

    private Hotel validateAndGetHotel(Short hotelId) {
        if (hotelId == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        return hotelRepository.findByIdAndIsDeletedFalse(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
    }

    private void validateAgeRange(Short minAge, Short maxAge) {
        if (minAge == null || maxAge == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        if (minAge > maxAge) {
            throw new AppException(ErrorCode.AGE_POLICY_INVALID_AGE_RANGE);
        }
    }
}
