package com.hotel.booking.modules.booking.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.dto.request.RoomConfirmRequest;
import com.hotel.booking.modules.booking.entity.BookingDetail;
import com.hotel.booking.modules.booking.entity.BookingRoom;
import com.hotel.booking.modules.booking.entity.BookingRoomId;
import com.hotel.booking.modules.booking.repository.BookingDetailRepository;
import com.hotel.booking.modules.booking.repository.BookingRoomRepository;
import com.hotel.booking.modules.inventory.entity.RoomInstance;
import com.hotel.booking.modules.inventory.entity.RoomSlot;
import com.hotel.booking.modules.inventory.enums.RoomSlotStatus;
import com.hotel.booking.modules.inventory.repository.RoomInstanceRepository;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoomConfirmationServiceImpl implements RoomConfirmationService{
    BookingDetailRepository bookingDetailRepository;
    RoomInstanceRepository roomInstanceRepository;
    RoomSlotRepository roomSlotRepository;
    BookingRoomRepository bookingRoomRepository;

    @Transactional
    public void confirmRoom(String bookingNumber, RoomConfirmRequest request) {
        log.info("Bắt đầu chốt phòng cứng. Booking: {}, DetailId: {}, Room: {}",
                bookingNumber, request.getBookingDetailId(), request.getRoomInstanceId());

        BookingDetail detail = bookingDetailRepository.findById(request.getBookingDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        //Kiểm tra đã chốt cứng bao nhiêu phòng
        long confirmedCount = bookingRoomRepository.countByBookingDetailId(detail.getId());
        if (confirmedCount >= detail.getQuantity()) {
            log.error("Cảnh báo gian lận! BookingDetail {} cố gắng chốt vượt quá số lượng {}", detail.getId(), detail.getQuantity());
            throw new AppException(ErrorCode.ROOM_QUANTITY_EXCEEDED);
        }

        RoomInstance room = roomInstanceRepository.findById(request.getRoomInstanceId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.getHotelRoomType().getId().equals(detail.getHotelRoomType().getId())) {
            log.error("Gian lận lúc Confirm! BookingDetail {} không khớp loại phòng với RoomInstance {}", detail.getId(), room.getId());
            throw new AppException(ErrorCode.ROOM_TYPE_MISMATCH);
        }

        // 1. PESSIMISTIC WRITE room_slots
        List<RoomSlot> slots = roomSlotRepository.findAndLockSlotsForConfirmation(room.getId(), detail.getId());

        if (slots.isEmpty()) {
            throw new AppException(ErrorCode.ROOM_NOT_BLOCKED);
        }

        // 2. RECHECK
        boolean isValidStatus = slots.stream()
                .allMatch(slot -> RoomSlotStatus.BLOCKED.equals(slot.getStatus()));

        if (!isValidStatus) {
            log.error("Recheck thất bại. Các slot không ở trạng thái BLOCKED cho Room: {}", room.getId());
            throw new AppException(ErrorCode.INVALID_ROOM_STATUS);
        }

        // 3. Update trạng thái sang RESERVED và set thời gian chốt (reservedAt)
        OffsetDateTime now = OffsetDateTime.now();
        for (RoomSlot slot : slots) {
            slot.setStatus(RoomSlotStatus.RESERVED);
            slot.setReservedAt(now);
        }
        roomSlotRepository.saveAll(slots);

        BookingRoomId bookingRoomId = new BookingRoomId(detail.getId(), room.getId());

        // 4. Ghi nhận lịch sử gán phòng vào bảng `booking_rooms`
        BookingRoom bookingRoom = BookingRoom.builder()
                .id(bookingRoomId)
                .bookingDetail(detail)
                .roomInstance(room)
                .assignedAt(now)
                .build();
        bookingRoomRepository.save(bookingRoom);

        log.info("Chốt phòng {} thành công (RESERVED) cho BookingDetail {}", room.getRoomNumber(), detail.getId());
    }
}
