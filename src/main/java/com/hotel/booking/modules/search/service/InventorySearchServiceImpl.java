package com.hotel.booking.modules.search.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import com.hotel.booking.modules.search.dto.request.AvailabilitySearchRequest;
import com.hotel.booking.modules.search.dto.response.AvailableRoomTypeResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class InventorySearchServiceImpl implements InventorySearchService {

    RoomAvailabilityRepository roomAvailabilityRepository;

    @Override
    public List<AvailableRoomTypeResponse> searchAvailableRooms(AvailabilitySearchRequest request) {
        log.info("Searching available rooms for hotel ID: {}, checkIn: {}, checkOut: {}, roomCount: {}",
                request.getHotelId(), request.getCheckIn(), request.getCheckOut(), request.getRoomCount());

        if (request.getCheckIn() == null || request.getCheckOut() == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new AppException(ErrorCode.SURCHARGE_RULE_INVALID_DATE_RANGE);
        }

        int requestedRooms = request.getRoomCount() != null ? request.getRoomCount() : 1;
        long numberOfNights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());

        List<Object[]> availableRoomTypes = roomAvailabilityRepository.findAvailableRoomTypes(
                request.getHotelId(),
                ActiveStatus.ACTIVE,
                request.getCheckIn(),
                request.getCheckOut(),
                requestedRooms,
                numberOfNights
            );

        return availableRoomTypes.stream()
                .map(row -> {
                    HotelRoomType roomType = (HotelRoomType) row[0];
                    Number minAvailable = (Number) row[1];
                    return AvailableRoomTypeResponse.builder()
                            .hotelRoomTypeId(roomType.getId())
                            .roomTypeName(roomType.getRoomType().getName())
                            .maxAdults(roomType.getMaxAdults())
                            .maxChildren(roomType.getMaxChildren())
                            .minAvailableCount(minAvailable != null ? minAvailable.intValue() : 0)
                            .build();
                })
                .toList();
    }
}
