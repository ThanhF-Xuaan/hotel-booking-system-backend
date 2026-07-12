package com.hotel.booking.modules.pos.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pos.dto.request.CreateServiceOrderRequest;
import com.hotel.booking.modules.pos.service.POSOrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pos") // Thuộc route riêng của POS
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class POSOrderController {
    POSOrderService posOrderService;

    // Phải truyền bookingId trên URL để biết order này cộng tiền vào đơn lưu trú nào
    @PostMapping("/{id}/orders")
    public ApiResponse<String> createOrder(
            @PathVariable("id") Long bookingId,
            @Valid @RequestBody CreateServiceOrderRequest request) {

        posOrderService.createRoomServiceOrder(bookingId, request);

        return ApiResponse.<String>builder()
                .message("Tạo Order Dịch Vụ thành công và đã cộng tiền vào Hóa đơn khách hàng")
                .build();
    }
}
