package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.HolidayCalendarCreationRequest;
import com.hotel.booking.modules.pricing.dto.request.HolidayCalendarUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.HolidayCalendarResponse;
import com.hotel.booking.modules.pricing.service.HolidayCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel/api/v1/pricing/holiday-calendars")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Holiday Calendar Management", description = "Các API quản lý Ngày lễ (HolidayCalendar)")
public class HolidayCalendarController {

    HolidayCalendarService holidayCalendarService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới ngày lễ", description = "Tạo mới ngày lễ cấu hình trong lịch để phục vụ Pricing Engine.")
    public ApiResponse<HolidayCalendarResponse> createHoliday(@Valid @RequestBody HolidayCalendarCreationRequest request) {
        return ApiResponse.<HolidayCalendarResponse>builder()
                .result(holidayCalendarService.createHoliday(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả ngày lễ", description = "Trả về danh sách ngày lễ chưa bị xóa mềm trong hệ thống.")
    public ApiResponse<List<HolidayCalendarResponse>> getAllHolidays() {
        return ApiResponse.<List<HolidayCalendarResponse>>builder()
                .result(holidayCalendarService.getAllHolidays())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết ngày lễ", description = "Trả về chi tiết ngày lễ theo ID. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<HolidayCalendarResponse> getHolidayById(@PathVariable Integer id) {
        return ApiResponse.<HolidayCalendarResponse>builder()
                .result(holidayCalendarService.getHolidayById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật ngày lễ", description = "Cập nhật thông tin ngày lễ theo ID. Admin có quyền cập nhật toàn bộ trường.")
    public ApiResponse<HolidayCalendarResponse> updateHoliday(
            @PathVariable Integer id,
            @Valid @RequestBody HolidayCalendarUpdateRequest request) {
        return ApiResponse.<HolidayCalendarResponse>builder()
                .result(holidayCalendarService.updateHoliday(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm ngày lễ", description = "Thực hiện xóa mềm ngày lễ bằng cách set isDeleted = true.")
    public ApiResponse<Void> deleteHoliday(@PathVariable Integer id) {
        holidayCalendarService.deleteHoliday(id);
        return ApiResponse.<Void>builder()
                .message("Holiday calendar deleted successfully")
                .build();
    }
}
