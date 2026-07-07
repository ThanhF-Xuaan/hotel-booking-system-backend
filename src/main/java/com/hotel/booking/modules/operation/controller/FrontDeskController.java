package com.hotel.booking.modules.operation.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.operation.dto.request.AddBookingChargeRequest;
import com.hotel.booking.modules.operation.dto.request.CheckInRequest;
import com.hotel.booking.modules.operation.dto.request.WalkInBookingRequest;
import com.hotel.booking.modules.operation.dto.response.WalkInBookingResponse;
import com.hotel.booking.modules.operation.service.FrontDeskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("hotel/api/v1/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FrontDeskController {

    FrontDeskService frontDeskService;

    @PostMapping("/{id}/check-in")
    public ApiResponse<String> checkInBooking(
            @PathVariable("id") Long bookingId,
            @Valid @RequestBody CheckInRequest request) {

        frontDeskService.processCheckIn(bookingId, request);

        return ApiResponse.<String>builder()
                .message("Check-in và lưu hồ sơ khách thành công")
                .build();
    }

    @PostMapping("/{id}/charges")
    public ApiResponse<String> addInStayCharge(
            @PathVariable("id") Long bookingId,
            @RequestBody AddBookingChargeRequest request) {

        frontDeskService.addInStayCharge(bookingId, request);
        return ApiResponse.<String>builder()
                .message("Thêm phụ phí thành công")
                .build();
    }

    @PostMapping("/{id}/check-out")
    @Operation(summary = "Check-out & Chốt hóa đơn", description = "Giải phóng phòng vật lý, nhả Inventory nếu trả phòng sớm, gom chi phí và chốt hóa đơn.")
    public ApiResponse<String> processCheckOut(@PathVariable("id") Long bookingId) {

        frontDeskService.processCheckOut(bookingId);

        return ApiResponse.<String>builder()
                .result("Check-out thành công. Hóa đơn đã được chốt (ISSUED)!")
                .build();
    }

    @PostMapping("/walk-in")
    @Operation(summary = "Đặt phòng khách vãng lai tại quầy (Walk-in Booking)",
            description = "Gom 3 bước (Khóa DB tồn kho + Chạy Pricing Engine tính toán bóc tách giá kèm thuế + Check-in tự động) vào 1 Transaction.")
    public ApiResponse<WalkInBookingResponse> createWalkInBooking(
            @RequestParam Short hotelId,
            @Valid @RequestBody WalkInBookingRequest request) {

        // Gọi sang FrontDeskService để xử lý cục Transaction khổng lồ
        WalkInBookingResponse response = frontDeskService.processWalkInBooking(hotelId, request);

        return ApiResponse.<WalkInBookingResponse>builder()
                .message("Tạo đơn hàng Walk-in và Check-in thành công!")
                .result(response)
                .build();
    }
}