package com.hotel.booking.modules.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.dto.request.InitiateBookingRequest;
import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import com.hotel.booking.modules.inventory.service.RoomAvailabilityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class BookingInitiationServiceImpl implements BookingInitiationService{
    RoomAvailabilityRepository roomAvailabilityRepository;
    StringRedisTemplate redisTemplate;
    ObjectMapper objectMapper;
    RoomAvailabilityService roomAvailabilityService;

    @Override
    @Transactional
    public String initiate(InitiateBookingRequest request) {
        log.info("Begin initiate booking");

        OffsetDateTime expiration = OffsetDateTime.now().plusMinutes(15);

        //Re-validation
        for(InitiateBookingRequest.RoomSelection room : request.getRooms()){
            List<RoomAvailability> availabilities = roomAvailabilityRepository
                    .findByHotelRoomTypeIdAndDateBetween(
                        room.getHotelRoomTypeId(),
                        request.getCheckIn(),
                        request.getCheckOut().minusDays(1)
            );

            if(availabilities.isEmpty()){
                throw new AppException(ErrorCode.ROOM_AVAILABILITY_NOT_FOUND);
            }

            for(RoomAvailability availability : availabilities){
                int availableCount = availability.getTotalRooms() - availability.getLockedRooms() - availability.getBookedRooms();

                if(availableCount < room.getQuantity()){
                    log.warn("Hết phòng! HotelRoomType: {}, Date: {}, Available: {}, Requested: {}",
                            room.getHotelRoomTypeId(), availability.getDate(), availableCount, room.getQuantity());
                    throw new AppException(ErrorCode.ROOM_NOT_ENOUGH_QUANTITY);
                }

                availability.setLockedRooms(availability.getLockedRooms() + room.getQuantity());
                availability.setLockedUntil(expiration);
            }


            roomAvailabilityRepository.saveAll(availabilities);
        }

        String sessionId = UUID.randomUUID().toString();
        String dataKey = "payment:data:" + sessionId;
        String expireKey = "payment:expire:" + sessionId;

        try {
            String bookingDraftJson = objectMapper.writeValueAsString(request);

            redisTemplate.opsForValue().set(dataKey, bookingDraftJson, 15, TimeUnit.MINUTES);

            redisTemplate.opsForValue().set(expireKey, "dummy_value", 10, TimeUnit.MINUTES);

            log.info("Khởi tạo Session [{}] thành công trên Redis (TTL 10 mins).", sessionId);

        } catch (JsonProcessingException e) {
            log.error("Lỗi khi parse InitiateBookingRequest sang JSON", e);
            throw new RuntimeException("Lỗi hệ thống khi khởi tạo phiên giao dịch");
        }

        return sessionId;
    }
}
