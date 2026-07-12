package com.hotel.booking.modules.search.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.search.dto.request.PricingRequest;
import com.hotel.booking.modules.search.dto.response.PricingResponse;
import com.hotel.booking.modules.search.service.PriceAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Search - Search Pricing Engine", description = "Các API tính toán báo giá phòng nghỉ")
public class PricingController {

    PriceAggregationService priceAggregationService;

    @PostMapping("/pricing")
    @Operation(summary = "Tính toán báo giá chi tiết", description = "Tính toán toàn bộ chi tiết giá gốc, phụ thu, thuế và tổng tiền thanh toán cuối cùng của một loại phòng trong giai đoạn được yêu cầu.")
    public ApiResponse<PricingResponse> calculatePrice(@Valid @RequestBody PricingRequest request) {
        PricingResponse response = priceAggregationService.calculatePrice(request);

        return ApiResponse.<PricingResponse>builder()
                .result(response)
                .build();
    }
}
