package com.hotel.booking.modules.search.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.entity.HotelRoomType;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.inventory.repository.HotelRoomTypeRepository;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import com.hotel.booking.modules.search.dto.request.HotelSearchRequest;
import com.hotel.booking.modules.search.dto.response.HotelSearchResultResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class HotelSearchServiceImpl implements HotelSearchService {

    HotelRepository hotelRepository;
    HotelRoomTypeRepository hotelRoomTypeRepository;
    RoomAvailabilityRepository roomAvailabilityRepository;

    @Override
    public List<HotelSearchResultResponse> searchHotels(HotelSearchRequest request) {
        log.info("Searching public hotels with hotelId: {}, checkIn: {}, checkOut: {}, roomCount: {}",
                request.getHotelId(), request.getCheckIn(), request.getCheckOut(), request.getRoomCount());

        validateDateRange(request);

        return hotelRepository.findAllByIsDeletedFalse().stream()
                .filter(hotel -> hotel.getStatus() == ActiveStatus.ACTIVE)
                .filter(hotel -> request.getHotelId() == null || hotel.getId().equals(request.getHotelId()))
                .map(hotel -> buildHotelResult(hotel, request))
                .filter(result -> result.getRecommendedHotelRoomTypeId() != null)
                .sorted(Comparator.comparing(HotelSearchResultResponse::getStartingPrice, Comparator.nullsLast(BigDecimal::compareTo)))
                .toList();
    }

    private HotelSearchResultResponse buildHotelResult(Hotel hotel, HotelSearchRequest request) {
        List<HotelRoomType> candidateRoomTypes = findCandidateRoomTypes(hotel, request);

        return candidateRoomTypes.stream()
                .min(Comparator.comparing(HotelRoomType::getBasePrice))
                .map(roomType -> HotelSearchResultResponse.builder()
                        .hotelId(hotel.getId())
                        .hotelName(hotel.getName())
                        .location(extractLocation(hotel.getAddress()))
                        .address(hotel.getAddress())
                        .starRating(resolveStarRating(hotel.getName()))
                        .thumbnailUrl("/uploads/Capture.PNG")
                        .startingPrice(roomType.getBasePrice())
                        .recommendedHotelRoomTypeId(roomType.getId())
                        .build())
                .orElse(HotelSearchResultResponse.builder()
                        .hotelId(hotel.getId())
                        .hotelName(hotel.getName())
                        .location(extractLocation(hotel.getAddress()))
                        .address(hotel.getAddress())
                        .starRating(resolveStarRating(hotel.getName()))
                        .thumbnailUrl("/uploads/Capture.PNG")
                        .build());
    }

    private List<HotelRoomType> findCandidateRoomTypes(Hotel hotel, HotelSearchRequest request) {
        boolean hasDateWindow = request.getCheckIn() != null && request.getCheckOut() != null;
        if (!hasDateWindow) {
            return hotelRoomTypeRepository.findAllByHotelIdAndIsDeletedFalse(hotel.getId()).stream()
                    .filter(roomType -> roomType.getStatus() == ActiveStatus.ACTIVE)
                    .toList();
        }

        int requestedRooms = request.getRoomCount() != null ? request.getRoomCount() : 1;
        long numberOfNights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());

        return roomAvailabilityRepository.findAvailableRoomTypes(
                        hotel.getId().intValue(),
                        ActiveStatus.ACTIVE,
                        request.getCheckIn(),
                        request.getCheckOut(),
                        requestedRooms,
                        numberOfNights
                )
                .stream()
                .map(row -> (HotelRoomType) row[0])
                .toList();
    }

    private void validateDateRange(HotelSearchRequest request) {
        if (request.getCheckIn() == null && request.getCheckOut() == null) {
            return;
        }
        if (request.getCheckIn() == null || request.getCheckOut() == null || !request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    private String extractLocation(String address) {
        if (address == null || address.isBlank()) {
            return "Vietnam";
        }

        String[] parts = address.split(",");
        return parts[parts.length - 1].trim();
    }

    private Integer resolveStarRating(String hotelName) {
        if (hotelName == null) {
            return 3;
        }

        String normalizedName = hotelName.toLowerCase();
        if (normalizedName.contains("luxury")) {
            return 5;
        }
        if (normalizedName.contains("grand")) {
            return 4;
        }
        if (normalizedName.contains("boutique")) {
            return 3;
        }
        return 3;
    }
}
