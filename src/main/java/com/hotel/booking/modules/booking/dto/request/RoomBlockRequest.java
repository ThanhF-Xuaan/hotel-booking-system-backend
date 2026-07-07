package com.hotel.booking.modules.booking.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomBlockRequest {
    @NotNull(message = "ID của chi tiết đơn hàng (Booking Detail) không được để trống")
    Long bookingDetailId;

    @NotNull(message = "ID của phòng vật lý muốn chọn không được để trống")
    Integer newRoomInstanceId;

    Integer oldRoomInstanceId;
}
