package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomAvailabilityServiceImpl implements RoomAvailabilityService {
    RoomAvailabilityRepository availabilityRepository;

    @Transactional
    public void lockRoomForPayment(int typeId, int quantity) {
        List<RoomAvailability> availabilities = availabilityRepository.findByHotelRoomTypeId(typeId);
        OffsetDateTime expiration = OffsetDateTime.now().plusMinutes(15);

        for (RoomAvailability avail : availabilities) {
            avail.setLockedRooms(avail.getLockedRooms() + quantity);
            avail.setLockedUntil(expiration);
        }
        availabilityRepository.saveAll(availabilities);
    }
}