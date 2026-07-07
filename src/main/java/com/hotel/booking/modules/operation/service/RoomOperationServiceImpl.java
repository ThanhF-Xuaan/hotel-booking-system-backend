package com.hotel.booking.modules.operation.service;

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
import com.hotel.booking.modules.operation.dto.request.BlockRoomRequest;
import com.hotel.booking.modules.operation.dto.request.UpdateRoomStatusRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoomOperationServiceImpl implements RoomOperationService {
    RoomAvailabilityRepository availabilityRepository;
    RoomInstanceRepository roomInstanceRepository;
    RoomSlotRepository roomSlotRepository;

    @Override
    @Transactional
    public String blockRoomForMaintenance(Integer roomInstanceId, BlockRoomRequest request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        // 0. Validate cơ bản
        if (startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        // 1. LẤY PHÒNG VẬT LÝ
        RoomInstance room = roomInstanceRepository.findById(roomInstanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        // 2. Lấy danh sách các slot ĐÃ TỒN TẠI trong DB
        List<RoomSlot> existingSlots = roomSlotRepository.findByRoomInstanceIdAndSlotDateBetween(
                roomInstanceId, startDate, endDate);

        boolean hasAffectedGuests = false;
        List<LocalDate> datesToLock = new ArrayList<>();
        List<RoomSlot> slotsToSave = new ArrayList<>();

        // 3. QUÉT QUA TỪNG NGÀY
        LocalDate currentDate = startDate;
        List<LocalDate> actualDatesLocked = new ArrayList<>();
        while (!currentDate.isAfter(endDate)) {
            LocalDate loopDate = currentDate; // Bắt buộc gán ra biến mới để dùng trong lambda

            // Tìm xem ngày này đã có slot trong DB chưa
            RoomSlot existingSlot = existingSlots.stream()
                    .filter(s -> s.getSlotDate().equals(loopDate))
                    .findFirst()
                    .orElse(null);

            if (existingSlot != null) {
                // TRƯỜNG HỢP 1: Slot đã có sẵn trong DB
                if (existingSlot.getStatus() != RoomSlotStatus.MAINTENANCE) {
                    if (existingSlot.getBookingDetail() != null) {
                        hasAffectedGuests = true;
                        log.warn("CẢNH BÁO: Block phòng {} dính vào BookingDetail ID: {}",
                                room.getRoomNumber(), existingSlot.getBookingDetail().getId());
                        existingSlot.setBookingDetail(null); // Đá khách ra
                    }
                    existingSlot.setStatus(RoomSlotStatus.MAINTENANCE);
                    existingSlot.setLockedAt(OffsetDateTime.now());

                    slotsToSave.add(existingSlot);
                    actualDatesLocked.add(loopDate);

                }
            } else {
                // TRƯỜNG HỢP 2: "Ghost Data" - Ngày này chưa từng có lịch sử -> TẠO MỚI SLOT
                RoomSlot newSlot = new RoomSlot();
                newSlot.setRoomInstance(room);
                newSlot.setSlotDate(loopDate);
                newSlot.setStatus(RoomSlotStatus.MAINTENANCE);
                newSlot.setLockedAt(OffsetDateTime.now());

                slotsToSave.add(newSlot);
                actualDatesLocked.add(loopDate);
            }

            currentDate = currentDate.plusDays(1);
        }

        if (!slotsToSave.isEmpty()) {
            roomSlotRepository.saveAll(slotsToSave);
        }

        // 4. XỬ LÝ INVENTORY (TRỪ KHO)
        if (!actualDatesLocked.isEmpty()) {
            List<RoomAvailability> availabilities = availabilityRepository.findByHotelRoomTypeIdAndDateIn(
                    room.getHotelRoomType().getId(), actualDatesLocked);

            if (availabilities.isEmpty() || availabilities.size() < actualDatesLocked.size()) {
                log.error("LỖI TỒN KHO: Hệ thống không tìm thấy đủ dữ liệu trong room_availability để trừ! " +
                                "Hãy chắc chắn đã chạy API/Job sinh tồn kho 180 ngày cho loại phòng ID: {}",
                        room.getHotelRoomType().getId());
            }

            for (RoomAvailability avail : availabilities) {
                avail.setLockedRooms(avail.getLockedRooms() + 1);
            }
            if (!availabilities.isEmpty()) {
                availabilityRepository.saveAll(availabilities);
            }
        }

        // 5. CẬP NHẬT TRẠNG THÁI HIỆN TẠI CỦA PHÒNG
        if (!startDate.isAfter(LocalDate.now())) {
            room.setCurrentStatus(RoomInstanceStatus.MAINTENANCE);
            roomInstanceRepository.save(room);
        }

        String resultMsg = "Đã khóa phòng " + room.getRoomNumber() + ".";
        if (hasAffectedGuests) {
            resultMsg += " CẢNH BÁO: Có khách đang lưu trú/đặt trước bị ảnh hưởng. Yêu cầu Lễ tân xếp lại phòng (Room Move).";
        }

        return resultMsg;
    }

    @Override
    @Transactional
    public String updateRoomStatus(Integer roomInstanceId, UpdateRoomStatusRequest request) {
        // 1. Lấy phòng vật lý
        RoomInstance room = roomInstanceRepository.findById(roomInstanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        RoomInstanceStatus oldStatus = room.getCurrentStatus();
        RoomInstanceStatus newStatus = request.getStatus(); // DTO đang dùng Enum nên gọi thẳng

        // 2. Trạng thái truyền vào phải hợp lệ (Dùng List của Enum luôn cho đồng bộ)
        if (!List.of(RoomInstanceStatus.READY, RoomInstanceStatus.CLEANING,
                RoomInstanceStatus.MAINTENANCE, RoomInstanceStatus.OCCUPIED).contains(newStatus)) {
            throw new AppException(ErrorCode.INVALID_ROOM_STATUS);
        }

        // 3. Không cho phép nhảy cóc phi logic
        if (newStatus == RoomInstanceStatus.READY) {
            if (oldStatus == RoomInstanceStatus.OCCUPIED) {
                throw new AppException(ErrorCode.ROOM_MUST_BE_CLEANED_FIRST);
            }
            if (oldStatus == RoomInstanceStatus.MAINTENANCE) {
                log.error("Lỗi: Phòng {} vừa sửa xong, phải chuyển sang CLEANING!", room.getRoomNumber());
                throw new AppException(ErrorCode.ROOM_MAINTENANCE_MUST_CLEAN);
            }
        }

        // NGHIỆP VỤ: NHẢ KHO KHI CHUYỂN TỪ MAINTENANCE SANG CLEANING
        if (oldStatus == RoomInstanceStatus.MAINTENANCE && newStatus == RoomInstanceStatus.CLEANING) {
            updateMaintenanceSlotsToCleaning(room, request.getStartDate(), request.getEndDate());
        }

        if (oldStatus == RoomInstanceStatus.CLEANING && newStatus == RoomInstanceStatus.READY) {
            // Bây giờ mới gọi hàm nhả kho
            releaseMaintenanceSlots(room, request.getStartDate(), request.getEndDate());
        }

        // Cập nhật trạng thái vật lý
        room.setCurrentStatus(newStatus);
        roomInstanceRepository.save(room);

        log.info("Phòng {} chuyển trạng thái từ {} sang {}", room.getRoomNumber(), oldStatus, newStatus);

        return ("Phòng " + room.getRoomNumber() + " hiện đang ở trạng thái " + newStatus);
    }

    // Sửa service: chỉ nhả trong dải [startDate, endDate]
    private void releaseMaintenanceSlots(RoomInstance room, LocalDate startDate, LocalDate endDate) {
        // 1. Lấy tất cả slot đang bị "khóa" trong dải ngày này (MAINTENANCE hoặc CLEANING)
        List<RoomSlot> slotsToRelease = roomSlotRepository
                .findByRoomInstanceIdAndStatusInAndSlotDateBetween(
                        room.getId(),
                        List.of(RoomSlotStatus.MAINTENANCE, RoomSlotStatus.CLEANING),
                        startDate,
                        endDate);

        if (slotsToRelease.isEmpty()) return;

        List<LocalDate> datesToRelease = slotsToRelease.stream()
                .map(RoomSlot::getSlotDate)
                .toList();

        // 2. Chuyển về READY
        for (RoomSlot slot : slotsToRelease) {
            slot.setStatus(RoomSlotStatus.READY);
            slot.setLockedAt(null);
        }
        roomSlotRepository.saveAll(slotsToRelease);

        // 3. Khôi phục Inventory
        List<RoomAvailability> availabilities = availabilityRepository
                .findByHotelRoomTypeIdAndDateIn(room.getHotelRoomType().getId(), datesToRelease);

        for (RoomAvailability avail : availabilities) {
            // Trừ đi số lượng phòng đã bị locked
            avail.setLockedRooms(Math.max(0, avail.getLockedRooms() - 1));
        }
        availabilityRepository.saveAll(availabilities);
    }

    private void updateMaintenanceSlotsToCleaning(RoomInstance room, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        // Lấy các slot đang là MAINTENANCE trong dải ngày đó
        List<RoomSlot> slotsToUpdate = roomSlotRepository
                .findByRoomInstanceIdAndStatusAndSlotDateBetween(
                        room.getId(), RoomSlotStatus.MAINTENANCE, startDate, endDate);

        if (slotsToUpdate.isEmpty()) return;

        // Chuyển trạng thái sang CLEANING để Frontend hiển thị trạng thái mới
        for (RoomSlot slot : slotsToUpdate) {
            slot.setStatus(RoomSlotStatus.CLEANING);
        }

        roomSlotRepository.saveAll(slotsToUpdate);
        log.info("Phòng {} đã chuyển {} slot sang trạng thái CLEANING.", room.getRoomNumber(), slotsToUpdate.size());
    }
}

