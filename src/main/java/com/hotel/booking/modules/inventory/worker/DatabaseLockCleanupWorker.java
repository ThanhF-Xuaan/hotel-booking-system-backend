package com.hotel.booking.modules.inventory.worker;

import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DatabaseLockCleanupWorker {

    RoomAvailabilityRepository availabilityRepository;

    // Chạy mỗi 5 phút/lần
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void cleanupExpiredLocks() {
        log.info("Database Cleanup Worker: Bắt đầu quét các phòng bị khóa quá hạn...");

        OffsetDateTime now = OffsetDateTime.now();

        int updatedRows = availabilityRepository.resetExpiredLocks(now);

        if (updatedRows > 0) {
            log.info("Đã giải phóng {} record phòng bị treo do Redis sập hoặc lỗi hệ thống.", updatedRows);
        }
    }
}