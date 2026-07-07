package com.hotel.booking.modules.search.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Public hotel search result")
public class HotelSearchResultResponse {

    @Schema(description = "Hotel ID", example = "1")
    Short hotelId;

    @Schema(description = "Hotel display name", example = "Viettel Luxury Ha Noi")
    String hotelName;

    @Schema(description = "Short location label", example = "Ha Noi")
    String location;

    @Schema(description = "Full hotel address")
    String address;

    @Schema(description = "Hotel star rating", example = "5")
    Integer starRating;

    @Schema(description = "Thumbnail URL from the uploads folder", example = "/uploads/Capture.PNG")
    String thumbnailUrl;

    @Schema(description = "Lowest available base price per night", example = "1200000.00")
    BigDecimal startingPrice;

    @Schema(description = "Recommended room type configuration ID for the next pricing step", example = "10")
    Integer recommendedHotelRoomTypeId;
}
