package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.modules.inventory.job.RoomAvailabilityBatchJob;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory/internal/jobs") // Đường dẫn nội bộ
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobTriggerController {

    RoomAvailabilityBatchJob batchJob;

    @PostMapping("/trigger-availability")
    public ResponseEntity<String> triggerRoomAvailabilityJob() {
        batchJob.preGenerateRoomAvailability();
        return ResponseEntity.ok("Đã chạy lệnh sinh dữ liệu 180 ngày thành công!");
    }
}
