package com.hotel.booking.modules.crm.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.crm.dto.request.GuestCreationRequest;
import com.hotel.booking.modules.crm.dto.request.GuestUpdateRequest;
import com.hotel.booking.modules.crm.dto.response.GuestResponse;
import com.hotel.booking.modules.crm.entity.Guest;
import com.hotel.booking.modules.crm.mapper.GuestMapper;
import com.hotel.booking.modules.crm.repository.GuestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class GuestServiceImpl implements GuestService {

    GuestRepository guestRepository;
    GuestMapper guestMapper;

    @Override
    @Transactional
    public GuestResponse createGuest(GuestCreationRequest request) {
        log.info("Creating guest with phone: {}", request.getPhone());
        
        if (guestRepository.findByPhoneAndIsDeletedFalse(request.getPhone()).isPresent()) {
            throw new AppException(ErrorCode.GUEST_PHONE_EXISTED);
        }

        Guest guest = guestMapper.toEntity(request);
        guest.setIsDeleted(false);
        Guest savedGuest = guestRepository.save(guest);

        log.info("Guest created successfully with id: {}", savedGuest.getId());
        return guestMapper.toResponse(savedGuest);
    }

    @Override
    public List<GuestResponse> getAllGuests() {
        log.info("Fetching all active guests");
        return guestRepository.findAllByIsDeletedFalse()
                .stream()
                .map(guestMapper::toResponse)
                .toList();
    }

    @Override
    public GuestResponse getGuestByPublicId(UUID publicId) {
        log.info("Fetching guest with publicId: {}", publicId);
        Guest guest = guestRepository.findByPublicIdAndIsDeletedFalse(publicId)
                .orElseThrow(() -> new AppException(ErrorCode.GUEST_NOT_FOUND));
        return guestMapper.toResponse(guest);
    }

    @Override
    public GuestResponse getGuestById(Long id) {
        log.info("Fetching guest with id: {}", id);
        Guest guest = guestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.GUEST_NOT_FOUND));
        return guestMapper.toResponse(guest);
    }

    @Override
    @Transactional
    public GuestResponse updateGuest(Long id, GuestUpdateRequest request) {
        log.info("Updating guest with id: {}", id);

        Guest guest = guestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.GUEST_NOT_FOUND));

        guestMapper.updateEntity(request, guest);
        Guest savedGuest = guestRepository.save(guest);

        log.info("Guest updated successfully with id: {}", savedGuest.getId());
        return guestMapper.toResponse(savedGuest);
    }

    @Override
    @Transactional
    public void deleteGuest(Long id) {
        log.info("Soft-deleting guest with id: {}", id);

        Guest guest = guestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.GUEST_NOT_FOUND));

        guest.setIsDeleted(true);
        guestRepository.save(guest);

        log.info("Guest soft-deleted successfully with id: {}", id);
    }
}
