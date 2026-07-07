package com.hotel.booking.modules.operation.worker;

import com.hotel.booking.modules.booking.entity.Booking;
import com.hotel.booking.modules.booking.enums.BookingStatus;
import com.hotel.booking.modules.booking.repository.BookingRepository;
import com.hotel.booking.modules.operation.service.BookingOperationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NightAuditWorker {

    BookingRepository bookingRepository;
    BookingOperationService bookingOperationService;

    /**
     * Chạy tự động vào lúc 02:00 AM mỗi ngày để kiểm toán.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void executeNoShowAudit() {
        log.info("========== BẮT ĐẦU NIGHT AUDIT: QUÉT ĐƠN HÀNG NO-SHOW ==========");

        // Tìm các Booking có ngày Check-out là "hôm qua" (hoặc trước đó),
        // nhưng trạng thái vẫn đang là CONFIRMED (nghĩa là trong suốt chu kỳ, khách chưa từng Check-in).
        LocalDate targetCheckOutDate = LocalDate.now().minusDays(1);

        // SỬA: Dùng hàm custom query mới và truyền thẳng Enum
        List<Booking> noShowBookings = bookingRepository.findBookingsForNoShow(
                BookingStatus.CONFIRMED, // Không dùng .toString()
                targetCheckOutDate
        );

        int count = 0;
        for (Booking booking : noShowBookings) {
            try {
                bookingOperationService.processNoShow(booking.getId());
                count++;
            } catch (Exception e) {
                log.error("Lỗi khi xử lý No-Show cho Booking ID {}: {}", booking.getId(), e.getMessage());
            }
        }

        log.info("========== NIGHT AUDIT HOÀN TẤT. Đã chốt {} đơn hàng sang NO_SHOW ==========", count);
    }
}