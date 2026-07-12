package com.hotel.booking.modules.booking.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.dto.request.RoomBlockRequest;
import com.hotel.booking.modules.booking.entity.BookingDetail;
import com.hotel.booking.modules.booking.repository.BookingDetailRepository;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoomAssignmentServiceImpl implements RoomAssignmentService{
    BookingDetailRepository bookingDetailRepository;
    RoomInstanceRepository roomInstanceRepository;
    RoomSlotRepository roomSlotRepository;

    @Transactional
    public void blockRoom(String bookingNumber, RoomBlockRequest request) {
        log.info("Bắt đầu xử lý block phòng. Booking: {}, DetailId: {}, NewRoom: {}, OldRoom: {}",
                bookingNumber, request.getBookingDetailId(), request.getNewRoomInstanceId(),
                request.getOldRoomInstanceId());

        BookingDetail detail = bookingDetailRepository.findById(request.getBookingDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // Kiểm tra giới hạn số lượng phòng
        if (request.getOldRoomInstanceId() == null) {
            // Nếu là thao tác chọn phòng mới
            long currentHoldings = roomSlotRepository.countDistinctRoomsByBookingDetail(detail.getId());
            if (currentHoldings >= detail.getQuantity()) {
                log.warn("BookingDetail {} đã giữ tối đa {} phòng. Không thể chọn thêm.", detail.getId(), detail.getQuantity());
                throw new AppException(ErrorCode.ROOM_QUANTITY_EXCEEDED);
            }
        } else {
            boolean isAlreadyReserved = roomSlotRepository.hasReservedSlots(
                    request.getOldRoomInstanceId(), detail.getId()
            );

            if (isAlreadyReserved) {
                log.error("Gian lận API! BookingDetail {} cố gắng đổi phòng {} đã được RESERVED",
                        detail.getId(), request.getOldRoomInstanceId());
                throw new AppException(ErrorCode.ROOM_ALREADY_CONFIRMED);
            }

            roomSlotRepository.releaseBlockedSlots(request.getOldRoomInstanceId(), detail.getId());
            log.info("Đã nhả các slot BLOCKED của phòng cũ {}", request.getOldRoomInstanceId());
        }

        // 2. Kích hoạt PESSIMISTIC WRITE lên phòng mới
        RoomInstance newRoom = roomInstanceRepository.findByIdWithPessimisticLock(request.getNewRoomInstanceId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        //Kiểm tra loại phòng có khớp nhau không
        if (!newRoom.getHotelRoomType().getId().equals(detail.getHotelRoomType().getId())) {
            log.error("Cảnh báo: BookingDetail {} (Type ID: {}) cố tình chọn sai RoomInstance {} (Type ID: {})",
                    detail.getId(), detail.getHotelRoomType().getId(),
                    newRoom.getId(), newRoom.getHotelRoomType().getId());
            throw new AppException(ErrorCode.ROOM_TYPE_MISMATCH);
        }

        // 3. Kiểm tra đụng độ
        boolean hasConflict = roomSlotRepository.existsConflictingSlots(
                newRoom.getId(),
                detail.getCheckInDate(),
                detail.getCheckOutDate()
        );

        if (hasConflict) {
            log.warn("Phòng {} đã bị vướng lịch từ {} đến {}", newRoom.getRoomNumber(), detail.getCheckInDate(), detail.getCheckOutDate());
            throw new AppException(ErrorCode.ROOM_ALREADY_BLOCKED);
        }

        // 4. Lấy các slots hiện có (những dòng mang trạng thái READY đang nằm sẵn trong DB)
        List<RoomSlot> existingSlots = roomSlotRepository.findByRoomInstanceIdAndDateRange(
                newRoom.getId(), detail.getCheckInDate(), detail.getCheckOutDate()
        );

        // Chuyển List thành Map theo SlotDate để tra cứu
        Map<LocalDate, RoomSlot> existingSlotMap = existingSlots.stream()
                .collect(Collectors.toMap(RoomSlot::getSlotDate, slot -> slot));

        // 5. Tiến hành UPSERT (Cập nhật nếu có, Tạo mới nếu không)
        List<RoomSlot> slotsToSave = new ArrayList<>();
        LocalDate currentDate = detail.getCheckInDate();
        OffsetDateTime now = OffsetDateTime.now();

        while (currentDate.isBefore(detail.getCheckOutDate())) {
            RoomSlot slot = existingSlotMap.get(currentDate);

            if (slot == null) {
                // Trạng thái Sparse Data: Chưa có dòng nào -> Tạo mới (INSERT)
                slot = RoomSlot.builder()
                        .roomInstance(newRoom)
                        .bookingDetail(detail)
                        .slotDate(currentDate)
                        .status(RoomSlotStatus.BLOCKED)
                        .lockedAt(now)
                        .build();
            } else {
                // Đã có dòng (chắc chắn là READY vì đã qua được vòng check conflict) -> Cập nhật (UPDATE)
                slot.setBookingDetail(detail);
                slot.setStatus(RoomSlotStatus.BLOCKED);
                slot.setLockedAt(now);
                slot.setReservedAt(null);
            }

            slotsToSave.add(slot);
            currentDate = currentDate.plusDays(1);
        }

        roomSlotRepository.saveAll(slotsToSave);
        log.info("Block phòng {} thành công cho BookingDetail {}", newRoom.getRoomNumber(), detail.getId());
    }
}
