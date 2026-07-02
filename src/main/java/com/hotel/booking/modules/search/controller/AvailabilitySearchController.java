package com.hotel.booking.modules.search.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.search.dto.request.AvailabilitySearchRequest;
import com.hotel.booking.modules.search.dto.response.AvailableRoomTypeResponse;
import com.hotel.booking.modules.search.service.InventorySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotel/api/v1/search")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Search - Search Availability Engine", description = "Các API tìm kiếm phòng trống của khách sạn")
public class AvailabilitySearchController {

    InventorySearchService inventorySearchService;

    @GetMapping("/availability")
    @Operation(summary = "Tìm kiếm phòng trống theo bộ lọc", description = "Tìm kiếm toàn bộ các loại phòng trống của một khách sạn cụ thể theo thời gian lưu trú và số lượng phòng cần đặt.")
    public ApiResponse<List<AvailableRoomTypeResponse>> searchAvailability(@Valid @ModelAttribute AvailabilitySearchRequest request) {
        return ApiResponse.<List<AvailableRoomTypeResponse>>builder()
                .result(inventorySearchService.searchAvailableRooms(request))
                .build();
    }
}
