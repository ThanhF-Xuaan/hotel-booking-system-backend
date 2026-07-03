package com.hotel.booking.modules.booking.worker;

import com.hotel.booking.modules.booking.entity.BookingDetail;
import com.hotel.booking.modules.booking.repository.BookingDetailRepository;
import com.hotel.booking.modules.booking.service.AutoAssignServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AutoAssignWorker {
    BookingDetailRepository bookingDetailRepository;
    AutoAssignServiceImpl autoAssignService;

    @Scheduled(fixedDelay = 90000)
    public void runAutoAssignTask() {
        OffsetDateTime now = OffsetDateTime.now();

        List<BookingDetail> targetDetails = bookingDetailRepository.findDetailsNeedingAutoAssignment(now);

        if (targetDetails.isEmpty()) return;

        log.info("Worker tìm thấy {} BookingDetail đã hết hạn tự chọn phòng. Bắt đầu Auto-Assign.", targetDetails.size());

        for (BookingDetail detail : targetDetails) {
            try {
                autoAssignService.processAutoAssignmentForDetail(detail);
            } catch (Exception e) {
                log.error("Lỗi khi chạy Auto-Assign cho Detail {}: {}", detail.getId(), e.getMessage(), e);
            }
        }
    }
}
