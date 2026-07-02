package com.hotel.booking.modules.booking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmBookingRequest {
    @NotBlank(message = "Session ID không được để trống")
    @Schema(description = "Mã phiên giao dịch (lấy từ bước Initiate)", example = "uuid-string")
    String sessionId;
}
