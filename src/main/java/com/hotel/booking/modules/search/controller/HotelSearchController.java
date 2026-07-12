package com.hotel.booking.modules.search.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.search.dto.request.HotelSearchRequest;
import com.hotel.booking.modules.search.dto.response.HotelSearchResultResponse;
import com.hotel.booking.modules.search.service.HotelSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Search - Hotel Results", description = "Public hotel search results before pricing calculation")
public class HotelSearchController {

    HotelSearchService hotelSearchService;

    @GetMapping("/hotels")
    @Operation(summary = "Search hotels", description = "Returns hotels with thumbnail, location, rating, and starting room price. Pricing engine is not called here.")
    public ApiResponse<List<HotelSearchResultResponse>> searchHotels(@ModelAttribute HotelSearchRequest request) {
        return ApiResponse.<List<HotelSearchResultResponse>>builder()
                .result(hotelSearchService.searchHotels(request))
                .build();
    }
}
