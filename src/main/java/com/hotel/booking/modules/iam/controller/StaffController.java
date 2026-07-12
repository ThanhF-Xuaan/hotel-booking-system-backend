package com.hotel.booking.modules.iam.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.iam.dto.request.StaffCreationRequest;
import com.hotel.booking.modules.iam.dto.request.StaffUpdateRequest;
import com.hotel.booking.modules.iam.dto.response.StaffResponse;
import com.hotel.booking.modules.iam.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/iam/staffs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "IAM - Staff Management", description = "Các API quản lý nhân viên (Staff)")
public class StaffController {

    StaffService staffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới nhân viên", description = "Tạo mới một nhân viên với thông tin vai trò, khách sạn, v.v. Mật khẩu sẽ được mã hóa an toàn bằng BCrypt trước khi lưu xuống database.")
    public ApiResponse<StaffResponse> createStaff(@Valid @RequestBody StaffCreationRequest request) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.createStaff(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả nhân viên", description = "Trả về danh sách nhân viên chưa bị xóa mềm trong hệ thống.")
    public ApiResponse<List<StaffResponse>> getAllStaffs() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<StaffResponse>>builder()
                .result(staffService.getAllStaffs())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết nhân viên theo ID", description = "Trả về thông tin chi tiết của một nhân viên hoạt động dựa trên ID.")
    public ApiResponse<StaffResponse> getStaffById(@PathVariable Integer id) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.getStaffById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật nhân viên", description = "Cập nhật thông tin nhân viên theo ID. Không cho phép sửa username. Mật khẩu mới (nếu truyền) sẽ được mã hóa an toàn bằng BCrypt.")
    public ApiResponse<StaffResponse> updateStaff(
            @PathVariable Integer id,
            @Valid @RequestBody StaffUpdateRequest request) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.updateStaff(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm nhân viên", description = "Thực hiện xóa mềm nhân viên bằng cách đặt isDeleted = true.")
    public ApiResponse<String> deleteStaff(@PathVariable Integer id) {
        staffService.deleteStaff(id);
        return ApiResponse.<String>builder()
                .result("Staff deleted successfully")
                .build();
    }
}
