package com.hotel.booking.modules.operation.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.entity.BookingGuest;
import com.hotel.booking.modules.booking.enums.BookingStatus;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.entity.RoomInstance;
import com.hotel.booking.modules.inventory.entity.RoomSlot;
import com.hotel.booking.modules.inventory.enums.RoomSlotStatus;
import com.hotel.booking.modules.inventory.repository.RoomInstanceRepository;
import com.hotel.booking.modules.inventory.repository.RoomSlotRepository;
import com.hotel.booking.modules.operation.dto.response.RoomTimeLineDTO;
import com.hotel.booking.modules.operation.dto.response.RoomTypeTimelineDTO;
import com.hotel.booking.modules.operation.dto.response.SlotDTO;
import com.hotel.booking.modules.operation.dto.response.TimelineResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimelineServiceImpl implements TimelineService {

    RoomInstanceRepository roomInstanceRepository;
    RoomSlotRepository roomSlotRepository;

    @Override
    public TimelineResponse getTimelineMatrix(Integer hotelId, LocalDate startDate, LocalDate endDate) {
        // 1. QUERY 1: Lấy tất cả các phòng vật lý
        List<RoomInstance> allRooms = roomInstanceRepository.findAllByHotelIdWithRoomType(hotelId);
        if (allRooms.isEmpty()) {
            return TimelineResponse.builder().startDate(startDate).endDate(endDate).roomTypes(Collections.emptyList()).build();
        }

        List<Integer> roomInstanceIds = allRooms.stream().map(RoomInstance::getId).toList();

        // 2. QUERY 2: Gọi RAW QUERY để lấy cả Slot lẫn Booking ID, Status, Tên khách
        List<Object[]> rawResults = roomSlotRepository.findTimelineSlotsRaw(roomInstanceIds, startDate, endDate);

        // Mảng lưu dữ liệu xé lẻ
        Map<LocalDate, Map<Integer, Object[]>> slotMatrix = new HashMap<>();

        for (Object[] row : rawResults) {
            RoomSlot slot = (RoomSlot) row[0];
            LocalDate date = slot.getSlotDate();
            Integer roomId = slot.getRoomInstance().getId();

            slotMatrix.computeIfAbsent(date, k -> new HashMap<>()).put(roomId, row);
        }

        // 4. HASHMAP Phòng theo Loại
        Map<HotelRoomType, List<RoomInstance>> roomsGroupedByType = allRooms.stream()
                .collect(Collectors.groupingBy(RoomInstance::getHotelRoomType, LinkedHashMap::new, Collectors.toList()));

        // 5. BUILD RESPONSE
        List<RoomTypeTimelineDTO> roomTypeTimelineList = new ArrayList<>();

        for (Map.Entry<HotelRoomType, List<RoomInstance>> entry : roomsGroupedByType.entrySet()) {
            HotelRoomType hrt = entry.getKey();
            List<RoomTimeLineDTO> roomTimelineList = new ArrayList<>();

            for (RoomInstance room : entry.getValue()) {
                List<SlotDTO> slotDtos = new ArrayList<>();

                LocalDate currentDate = startDate;
                while (!currentDate.isAfter(endDate)) {

                    Object[] rowData = slotMatrix.getOrDefault(currentDate, Collections.emptyMap()).get(room.getId());

                    if (rowData != null) {
                        RoomSlot slotInDb = (RoomSlot) rowData[0];
                        Long bookingId = (Long) rowData[1];
                        BookingStatus bookingStatus = (BookingStatus) rowData[2];
                        String guestName = (String) rowData[3];

                        Long bookingDetailId = slotInDb.getBookingDetail() != null ? slotInDb.getBookingDetail().getId() : null;

                        slotDtos.add(SlotDTO.builder()
                                .date(currentDate)
                                .status(slotInDb.getStatus())
                                .bookingDetailId(bookingDetailId)
                                .bookingId(bookingId) // ĐÃ CÓ CHẮC CHẮN 100%
                                .guestName(guestName)
                                .bookingStatus(bookingStatus)
                                .build());
                    } else {
                        slotDtos.add(SlotDTO.builder()
                                .date(currentDate)
                                .status(RoomSlotStatus.READY)
                                .build());
                    }
                    currentDate = currentDate.plusDays(1);
                }

                roomTimelineList.add(RoomTimeLineDTO.builder()
                        .roomInstanceId(room.getId())
                        .roomNumber(room.getRoomNumber())
                        .slots(slotDtos)
                        .build());
            }

            roomTypeTimelineList.add(RoomTypeTimelineDTO.builder()
                    .hotelRoomTypeId(hrt.getId())
                    .roomTypeName(hrt.getRoomType().getName())
                    .rooms(roomTimelineList)
                    .build());
        }

        return TimelineResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .roomTypes(roomTypeTimelineList)
                .build();
    }
}
