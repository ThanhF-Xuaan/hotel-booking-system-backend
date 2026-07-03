package com.hotel.booking.modules.booking.service;


import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.dto.response.RoomSelectionUIResponse;
import com.hotel.booking.modules.booking.entity.Booking;
import com.hotel.booking.modules.booking.entity.BookingDetail;
import com.hotel.booking.modules.booking.repository.BookingRepository;
import com.hotel.booking.modules.inventory.repository.RoomInstanceRepository;
import com.hotel.booking.modules.inventory.repository.RoomSlotRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
@Slf4j
public class RoomAllocationServiceImpl implements RoomAllocationService{
    BookingRepository bookingRepository;
    RoomInstanceRepository roomInstanceRepository;
    RoomSlotRepository roomSlotRepository;

    public RoomSelectionUIResponse getAvailableRoomsForBooking(String bookingNumber) {
        log.info("Get available rooms for booking - RoomAllocationServiceImpl - booking module");

        Booking booking = bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        List<RoomSelectionUIResponse.RoomTypeGroup> groups = new ArrayList<>();

        for (BookingDetail detail : booking.getBookingDetails()) {
            long currentHoldings = roomSlotRepository.countDistinctRoomsByBookingDetail(detail.getId());

            List<RoomSelectionUIResponse.PhysicalRoom> physicalRooms = new ArrayList<>();

            if (currentHoldings >= detail.getQuantity()) {
                log.info("BookingDetail {} đã chọn đủ số lượng phòng ({} / {}). Bỏ qua tìm phòng trống.",
                        detail.getId(), currentHoldings, detail.getQuantity());
            } else {
                // Query DB lấy danh sách phòng chỉ chứa 'READY' và 'BLOCKED'
                List<RoomInstanceRepository.RoomStatusProjection> projections = roomInstanceRepository
                        .findSelectableRooms(
                                detail.getHotelRoomType().getId(),
                                detail.getCheckInDate(),
                                detail.getCheckOutDate()
                        );

                for (RoomInstanceRepository.RoomStatusProjection p : projections) {
                    physicalRooms.add(RoomSelectionUIResponse.PhysicalRoom.builder()
                            .roomInstanceId(p.getRoomInstanceId())
                            .roomNumber(p.getRoomNumber())
                            .status(p.getStatus())
                            .build());
                }
            }

            groups.add(RoomSelectionUIResponse.RoomTypeGroup.builder()
                    .hotelRoomTypeId(detail.getHotelRoomType().getId())
                    .roomTypeName(detail.getRoomTypeName())
                    .requiredQuantity((int) detail.getQuantity())
                    .physicalRooms(physicalRooms)
                    .build());
        }

        log.info("get available rooms for booking successfully");

        return RoomSelectionUIResponse.builder()
                .bookingNumber(booking.getBookingNumber())
                .checkIn(booking.getBookingDetails().get(0).getCheckInDate())
                .checkOut(booking.getBookingDetails().get(0).getCheckOutDate())
                .roomTypeGroups(groups)
                .build();
    }
}

