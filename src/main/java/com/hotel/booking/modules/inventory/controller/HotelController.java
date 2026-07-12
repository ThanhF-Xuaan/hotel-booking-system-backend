package com.hotel.booking.modules.inventory.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.inventory.dto.request.HotelCreationRequest;
import com.hotel.booking.modules.inventory.dto.request.HotelUpdateRequest;
import com.hotel.booking.modules.inventory.dto.response.HotelResponse;
import com.hotel.booking.modules.inventory.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory/hotels")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Inventory - Hotel Management", description = "Các API quản lý thông tin khách sạn (Hotel)")
public class HotelController {

    HotelService hotelService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới khách sạn", description = "Tạo một khách sạn mới với các thông tin chi tiết. Tên khách sạn phải là duy nhất trong hệ thống.")
    public ApiResponse<HotelResponse> createHotel(@Valid @RequestBody HotelCreationRequest request) {
        return ApiResponse.<HotelResponse>builder()
                .result(hotelService.createHotel(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả khách sạn", description = "Trả về danh sách tất cả các khách sạn đang hoạt động (chưa bị xóa mềm).")
    public ApiResponse<List<HotelResponse>> getAllHotels() {
        return ApiResponse.<List<HotelResponse>>builder()
                .result(hotelService.getAllHotels())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết khách sạn theo ID", description = "Trả về thông tin chi tiết của một khách sạn dựa trên ID được cung cấp. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<HotelResponse> getHotelById(@PathVariable Short id) {
        return ApiResponse.<HotelResponse>builder()
                .result(hotelService.getHotelById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin khách sạn", description = "Cập nhật thông tin của một khách sạn dựa trên ID cung cấp. Tránh đổi tên trùng với khách sạn khác. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<HotelResponse> updateHotel(
            @PathVariable Short id,
            @Valid @RequestBody HotelUpdateRequest request) {
        return ApiResponse.<HotelResponse>builder()
                .result(hotelService.updateHotel(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm khách sạn", description = "Thực hiện xóa mềm (soft-delete) một khách sạn bằng cách đánh dấu isDeleted = true. Khách sạn sẽ không còn xuất hiện trong các danh sách.")
    public ApiResponse<Void> deleteHotel(@PathVariable Short id) {
        hotelService.deleteHotel(id);
        return ApiResponse.<Void>builder()
                .message("Hotel deleted successfully")
                .build();
    }
}
