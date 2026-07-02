package com.hotel.booking.modules.search.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
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
@Schema(description = "Yêu cầu tìm kiếm phòng trống")
public class AvailabilitySearchRequest {

    @NotNull(message = "SEARCH_AVAILABILITY_HOTEL_ID_NOT_NULL")
    @Schema(description = "ID của khách sạn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer hotelId;

    @NotNull(message = "SEARCH_AVAILABILITY_CHECKIN_NOT_NULL")
    @FutureOrPresent(message = "SEARCH_AVAILABILITY_CHECKIN_FUTURE_OR_PRESENT")
    @Schema(description = "Ngày nhận phòng", example = "2026-07-15", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate checkIn;

    @NotNull(message = "SEARCH_AVAILABILITY_CHECKOUT_NOT_NULL")
    @Schema(description = "Ngày trả phòng", example = "2026-07-20", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate checkOut;

    @Schema(description = "Số lượng phòng cần đặt", example = "1")
    Integer roomCount;
}
