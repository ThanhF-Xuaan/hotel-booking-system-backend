package com.hotel.booking.modules.inventory.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import com.hotel.booking.modules.inventory.entity.RoomInstance;
import com.hotel.booking.modules.inventory.entity.RoomSlot;
import com.hotel.booking.modules.inventory.enums.RoomInstanceStatus;
import com.hotel.booking.modules.inventory.enums.RoomSlotStatus;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import com.hotel.booking.modules.inventory.repository.RoomInstanceRepository;
import com.hotel.booking.modules.inventory.repository.RoomSlotRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomMaintenanceServiceImpl implements RoomMaintenanceService {

    RoomInstanceRepository roomInstanceRepository;
    RoomSlotRepository roomSlotRepository;
    RoomAvailabilityRepository roomAvailabilityRepository;

    @Override
    @Transactional
    public void scheduleMaintenance(Integer roomInstanceId, LocalDate startDate, LocalDate endDate) {
        log.info("Scheduling maintenance for room instance ID: {} from {} to {}", roomInstanceId, startDate, endDate);

        RoomInstance roomInstance = roomInstanceRepository.findByIdAndIsDeletedFalse(roomInstanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        List<RoomSlotStatus> conflictStatuses = List.of(RoomSlotStatus.OCCUPIED, RoomSlotStatus.RESERVED, RoomSlotStatus.BLOCKED);
        boolean hasConflict = roomSlotRepository.existsByRoomInstanceIdAndSlotDateBetweenAndStatusIn(
                roomInstanceId, startDate, endDate, conflictStatuses
        );

        if (hasConflict) {
            throw new AppException(ErrorCode.ROOM_HAS_BOOKING_CONFLICT);
        }

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentSlotDate = date;
            RoomSlot slot = roomSlotRepository.findByRoomInstanceIdAndSlotDate(roomInstanceId, currentSlotDate)
                    .orElseGet(() -> RoomSlot.builder()
                            .roomInstance(roomInstance)
                            .slotDate(currentSlotDate)
                            .status(RoomSlotStatus.READY)
                            .build());

            if (slot.getStatus() != RoomSlotStatus.MAINTENANCE) {
                slot.setStatus(RoomSlotStatus.MAINTENANCE);
                roomSlotRepository.save(slot);

                RoomAvailability availability = roomAvailabilityRepository.findByHotelRoomTypeIdAndDate(
                        roomInstance.getHotelRoomType().getId(), date
                ).orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

                availability.setTotalRooms(availability.getTotalRooms() - 1);
                roomAvailabilityRepository.save(availability);
            }
        }

        LocalDate now = LocalDate.now();
        if (!now.isBefore(startDate) && !now.isAfter(endDate)) {
            roomInstance.setCurrentStatus(RoomInstanceStatus.MAINTENANCE);
            roomInstanceRepository.save(roomInstance);
            log.info("Room instance ID: {} current status updated to MAINTENANCE", roomInstanceId);
        }
    }
}
