package com.hotel.booking.modules.inventory.job;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomAvailabilityBatchJob {

    HotelRoomTypeRepository hotelRoomTypeRepository;
    RoomAvailabilityRepository roomAvailabilityRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void preGenerateRoomAvailability() {
        log.info("Starting RoomAvailabilityBatchJob to pre-generate 180 days of room availability data");

        List<HotelRoomType> activeRoomTypes = hotelRoomTypeRepository
                .findAllByIsDeletedFalseAndStatus(ActiveStatus.ACTIVE);
        log.info("Found {} active room types for generation", activeRoomTypes.size());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(180);

        int count = 0;
        for (HotelRoomType roomType : activeRoomTypes) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                try {
                    roomAvailabilityRepository.insertIgnoreConflict(
                            roomType.getId(),
                            date,
                            roomType.getTotalQuantity());
                    count++;
                } catch (Exception e) {
                    log.error("Failed to insert room availability for room type ID: {} on date: {}", roomType.getId(),
                            date, e);
                }
            }
        }

        log.info("RoomAvailabilityBatchJob completed. Attempted to generate availability for {} slots", count);
    }
}
