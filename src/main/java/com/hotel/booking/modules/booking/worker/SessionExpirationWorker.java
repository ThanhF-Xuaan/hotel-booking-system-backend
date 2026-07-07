package com.hotel.booking.modules.booking.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.modules.booking.dto.request.InitiateBookingRequest;
import com.hotel.booking.modules.inventory.entity.RoomAvailability;
import com.hotel.booking.modules.inventory.repository.RoomAvailabilityRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionExpirationWorker extends KeyExpirationEventMessageListener {

    StringRedisTemplate redisTemplate;
    ObjectMapper objectMapper;
    RoomAvailabilityRepository availabilityRepository;

    // Kế thừa class có sẵn của Spring Data Redis để làm Worker
    public SessionExpirationWorker(RedisMessageListenerContainer listenerContainer,
                                   StringRedisTemplate redisTemplate,
                                   ObjectMapper objectMapper,
                                   RoomAvailabilityRepository availabilityRepository) {
        super(listenerContainer);
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith("payment:expire:")) {
            String sessionId = expiredKey.replace("payment:expire:", "");
            log.info("Phát hiện Session [{}] hết hạn. Tiến hành giải phóng phòng...", sessionId);

            releaseLockedRooms(sessionId);
        }
    }

    private void releaseLockedRooms(String sessionId) {
        String dataKey = "payment:data:" + sessionId;

        try {
            String bookingDraftJson = redisTemplate.opsForValue().get(dataKey);
            if (bookingDraftJson == null) {
                log.warn("Không tìm thấy dữ liệu giỏ hàng cho Session [{}]. Có thể đã được xử lý.", sessionId);
                return;
            }

            //Chuyển JSON ngược lại thành DTO
            InitiateBookingRequest request = objectMapper.readValue(bookingDraftJson, InitiateBookingRequest.class);

            //Rollback Database
            for (InitiateBookingRequest.RoomSelection room : request.getRooms()) {

                List<RoomAvailability> availabilities = availabilityRepository
                        .findByHotelRoomTypeIdAndDateBetween(
                                room.getHotelRoomTypeId(),
                                request.getCheckIn(),
                                request.getCheckOut().minusDays(1)
                        );

                for (RoomAvailability avail : availabilities) {
                    avail.setLockedRooms(Math.max(0, avail.getLockedRooms() - room.getQuantity()));
                }

                availabilityRepository.saveAll(availabilities);
                log.info("Đã nhả {} phòng cho RoomType ID {} thành công.", room.getQuantity(), room.getHotelRoomTypeId());
            }

            redisTemplate.delete(dataKey);
            log.info("✅ Hoàn tất dọn dẹp Session [{}].", sessionId);

        } catch (Exception e) {
            log.error("Lỗi nghiêm trọng khi giải phóng phòng cho Session [{}]: {}", sessionId, e.getMessage(), e);
        }
    }
}