package com.hotel.booking.modules.search.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu tính toán báo giá phòng")
public class PricingRequest {

    @NotNull(message = "PRICING_HOTEL_ID_NOT_NULL")
    @Schema(description = "ID của khách sạn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer hotelId;

    @NotNull(message = "PRICING_CHECKIN_NOT_NULL")
    @FutureOrPresent(message = "PRICING_CHECKIN_FUTURE_OR_PRESENT")
    @Schema(description = "Ngày nhận phòng", example = "2026-07-15", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate checkIn;

    @NotNull(message = "PRICING_CHECKOUT_NOT_NULL")
    @Schema(description = "Ngày trả phòng", example = "2026-07-20", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate checkOut;

    @jakarta.validation.constraints.NotEmpty(message = "PRICING_ROOMS_NOT_EMPTY")
    @jakarta.validation.Valid
    @Schema(description = "Danh sách phòng yêu cầu", requiredMode = Schema.RequiredMode.REQUIRED)
    java.util.List<RoomRequest> rooms;
}
