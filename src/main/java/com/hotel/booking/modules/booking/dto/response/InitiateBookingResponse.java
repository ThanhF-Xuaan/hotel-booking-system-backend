package com.hotel.booking.modules.booking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InitiateBookingResponse {
    String sessionId;
    Long expiresIn;
}
