package com.hotel.booking.modules.operation.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.entity.Booking;
import com.hotel.booking.modules.booking.entity.BookingDetail;
import com.hotel.booking.modules.booking.enums.BookingStatus;
import com.hotel.booking.modules.booking.enums.InvoiceStatus;
import com.hotel.booking.modules.booking.repository.BookingRepository;
import com.hotel.booking.modules.booking.repository.BookingRoomRepository;
import com.hotel.booking.modules.booking.repository.InvoiceRepository;
import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import com.hotel.booking.modules.inventory.entity.RoomSlot;
import com.hotel.booking.modules.inventory.enums.RoomSlotStatus;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import com.hotel.booking.modules.inventory.repository.RoomSlotRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingOperationServiceImpl implements BookingOperationService {

    BookingRepository bookingRepository;
    RoomAvailabilityRepository availabilityRepository;
    RoomSlotRepository roomSlotRepository;
    BookingRoomRepository bookingRoomRepository;
    InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        log.info("Bắt đầu xử lý Hủy phòng (Cancel) cho Booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }

        booking.setStatus(BookingStatus.CANCELLED);

        for (BookingDetail detail : booking.getBookingDetails()) {
            // 1. NHẢ KHO PHÒNG
            List<RoomAvailability> availabilities = availabilityRepository
                    .findByHotelRoomTypeIdAndDateBetween(
                            detail.getHotelRoomType().getId(),
                            detail.getCheckInDate(),
                            detail.getCheckOutDate().minusDays(1)
                    );

            for (RoomAvailability avail : availabilities) {
                avail.setBookedRooms(Math.max(0, avail.getBookedRooms() - detail.getQuantity()));
            }
            availabilityRepository.saveAll(availabilities);

            // 2. NHẢ ROOM SLOTS
            List<RoomSlot> slots = roomSlotRepository.findByBookingDetailId(detail.getId());
            for (RoomSlot slot : slots) {
                slot.setStatus(RoomSlotStatus.READY);
                slot.setBookingDetail(null);
            }
            roomSlotRepository.saveAll(slots);

            // 3. XÓA GÁN PHÒNG VẬT LÝ
            bookingRoomRepository.deleteByBookingDetailId(detail.getId());
        }

        // 4. CHỐT HÓA ĐƠN ĐỂ GHI NHẬN DOANH THU KHÔNG HOÀN LẠI
        invoiceRepository.findByBookingIdAndStatus(bookingId, InvoiceStatus.DRAFT)
                .ifPresent(invoice -> {
                    invoice.setStatus(InvoiceStatus.ISSUED);
                    invoice.setIssuedAt(OffsetDateTime.now());
                    invoiceRepository.save(invoice);
                });

        bookingRepository.save(booking);
        log.info("Hủy phòng thành công Booking ID {}: Đã giải phóng phòng để bán tiếp và CHỐT DOANH THU hóa đơn.", bookingId);
    }


    @Override
    @Transactional
    public void processNoShow(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            throw  new AppException(ErrorCode.INVALID_BOOKING_STATUS); // Bỏ qua nếu trạng thái không hợp lệ
        }

        //KHÔNG động vào Inventory và RoomSlot, giữ nguyên RESERVED để ôm quỹ phòng.
        booking.setStatus(BookingStatus.NO_SHOW);

        // Chốt Invoice luôn thành ISSUED vì giao dịch này coi như khách mất tiền, khách sạn thu đủ
        invoiceRepository.findByBookingIdAndStatus(bookingId, InvoiceStatus.DRAFT)
                .ifPresent(invoice -> {
                    invoice.setStatus(InvoiceStatus.ISSUED);
                    invoice.setIssuedAt(OffsetDateTime.now());
                    invoiceRepository.save(invoice);
                });

        bookingRepository.save(booking);
        log.info("Booking ID {} đã chuyển thành NO_SHOW (Đã chốt doanh thu, giữ nguyên phòng).", bookingId);
    }
}