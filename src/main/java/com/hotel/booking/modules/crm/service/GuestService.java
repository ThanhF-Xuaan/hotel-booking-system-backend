package com.hotel.booking.modules.crm.service;

import com.hotel.booking.modules.crm.dto.request.GuestCreationRequest;
import com.hotel.booking.modules.crm.dto.request.GuestUpdateRequest;
import com.hotel.booking.modules.crm.dto.response.GuestResponse;

import java.util.List;
import java.util.UUID;

public interface GuestService {
    GuestResponse createGuest(GuestCreationRequest request);
    List<GuestResponse> getAllGuests();
    GuestResponse getGuestByPublicId(UUID publicId);
    GuestResponse getGuestById(Long id);
    GuestResponse updateGuest(Long id, GuestUpdateRequest request);
    void deleteGuest(Long id);
}
