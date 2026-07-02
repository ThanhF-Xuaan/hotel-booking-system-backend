package com.hotel.booking.modules.search.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Hotel search request for the public booking result page")
public class HotelSearchRequest {

    @Schema(description = "Optional hotel ID filter", example = "1")
    Short hotelId;

    @Schema(description = "Check-in date", example = "2026-07-15")
    LocalDate checkIn;

    @Schema(description = "Check-out date", example = "2026-07-20")
    LocalDate checkOut;

    @Schema(description = "Number of requested room units", example = "1")
    Integer roomCount;
}
