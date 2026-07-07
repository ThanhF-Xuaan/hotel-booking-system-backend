package com.hotel.booking.modules.booking.service;

import com.hotel.booking.modules.booking.entity.BookingDetail;
import com.hotel.booking.modules.booking.entity.BookingRoom;
import com.hotel.booking.modules.booking.entity.BookingRoomId;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
public class AutoAssignServiceImpl implements AutoAssignService{
    RoomInstanceRepository roomInstanceRepository;
    RoomSlotRepository roomSlotRepository;
    BookingRoomRepository bookingRoomRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAutoAssignmentForDetail(BookingDetail detail) {
        log.info("Bắt đầu xử lý Auto-Assign cho BookingDetail ID: {}", detail.getId());

        long currentConfirmed = bookingRoomRepository.countByBookingDetailId(detail.getId());
        long deficit = detail.getQuantity() - currentConfirmed;

        if (deficit <= 0) {
            log.info("BookingDetail ID: {} đã đủ số lượng phòng. Bỏ qua.", detail.getId());
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();

        // -------------------------------------------------------------------
        // PHA 1: VỚT CÁC PHÒNG MÀ KHÁCH ĐÃ "BLOCKED" NHƯNG QUÊN "CONFIRM"
        // -------------------------------------------------------------------
        List<RoomSlot> myBlockedSlots = roomSlotRepository.findByBookingDetailIdAndStatus(detail.getId(), RoomSlotStatus.BLOCKED);

        Map<Integer, List<RoomSlot>> slotsByRoom = myBlockedSlots.stream()
                .collect(Collectors.groupingBy(slot -> slot.getRoomInstance().getId()));

        for (Map.Entry<Integer, List<RoomSlot>> entry : slotsByRoom.entrySet()) {
            if (deficit <= 0) break;

            RoomInstance room = entry.getValue().get(0).getRoomInstance();
            List<RoomSlot> slots = entry.getValue();

            // Đổi trạng thái từ BLOCKED sang RESERVED
            for (RoomSlot slot : slots) {
                slot.setStatus(RoomSlotStatus.RESERVED);
                slot.setReservedAt(now);
            }
            roomSlotRepository.saveAll(slots);

            // LƯU KẾT QUẢ VÀO BẢNG BOOKING_ROOMS
            saveBookingRoom(detail, room, now);
            deficit--;
            log.info("Auto-Assign: Vớt thành công phòng {} từ trạng thái BLOCKED cho Detail {}", room.getRoomNumber(), detail.getId());
        }

        // -------------------------------------------------------------------
        // PHA 2: TÌM PHÒNG TRỐNG MỚI ĐỂ BÙ VÀO (NẾU VẪN THIẾU)
        // -------------------------------------------------------------------
        if (deficit > 0) {
            List<RoomInstance> freeRooms = roomInstanceRepository.findCompletelyFreeRooms(
                    detail.getHotelRoomType().getId(), detail.getCheckInDate(), detail.getCheckOutDate()
            );

            for (RoomInstance room : freeRooms) {
                if (deficit <= 0) break;

                // Double-check đụng độ trước khi khóa chết
                boolean hasConflict = roomSlotRepository.existsConflictingSlots(
                        room.getId(), detail.getCheckInDate(), detail.getCheckOutDate()
                );

                if (!hasConflict) {
                    insertReservedSlots(room, detail, now);
                    // LƯU KẾT QUẢ VÀO BẢNG BOOKING_ROOMS
                    saveBookingRoom(detail, room, now);
                    deficit--;
                    log.info("Auto-Assign: Random gán phòng {} cho Detail {}", room.getRoomNumber(), detail.getId());
                }
            }
        }

        if (deficit > 0) {
            log.error("NGHIÊM TRỌNG: Kho phòng hết sạch, không đủ để Auto-Assign cho Detail ID {}.", detail.getId());
        }
    }

    private void saveBookingRoom(BookingDetail detail, RoomInstance room, OffsetDateTime now) {
        BookingRoomId bookingRoomId = new BookingRoomId(detail.getId(), room.getId());
        BookingRoom bookingRoom = BookingRoom.builder()
                .id(bookingRoomId)
                .bookingDetail(detail)
                .roomInstance(room)
                .assignedAt(now)
                .build();
        bookingRoomRepository.save(bookingRoom);
    }

    private void insertReservedSlots(RoomInstance room, BookingDetail detail, OffsetDateTime now) {
        List<RoomSlot> slotsToSave = new ArrayList<>();
        LocalDate currentDate = detail.getCheckInDate();

        while (currentDate.isBefore(detail.getCheckOutDate())) {
            slotsToSave.add(RoomSlot.builder()
                    .roomInstance(room)
                    .bookingDetail(detail)
                    .slotDate(currentDate)
                    .status(RoomSlotStatus.RESERVED)
                    .reservedAt(now)
                    .build());
            currentDate = currentDate.plusDays(1);
        }
        roomSlotRepository.saveAll(slotsToSave);
    }
}
