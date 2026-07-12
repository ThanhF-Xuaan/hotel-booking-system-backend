package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.VatRuleCreationRequest;
import com.hotel.booking.modules.pricing.dto.request.VatRuleUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.VatRuleResponse;
import com.hotel.booking.modules.pricing.service.VatRuleService;
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
@RequestMapping("/api/v1/pricing/vat-rules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Vat Rule Management", description = "Các API quản lý Cấu hình thuế (VatRule)")
public class VatRuleController {

    VatRuleService vatRuleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới cấu hình thuế", description = "Tạo một cấu hình thuế mới với các thông tin code, name, percent, appliesTo, v.v. Code phải duy nhất.")
    public ApiResponse<VatRuleResponse> createVatRule(@Valid @RequestBody VatRuleCreationRequest request) {
        return ApiResponse.<VatRuleResponse>builder()
                .result(vatRuleService.createVatRule(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách cấu hình thuế", description = "Trả về danh sách tất cả cấu hình thuế đang hoạt động (chưa bị xóa mềm).")
    public ApiResponse<List<VatRuleResponse>> getAllVatRules() {
        return ApiResponse.<List<VatRuleResponse>>builder()
                .result(vatRuleService.getAllVatRules())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết cấu hình thuế theo ID", description = "Trả về thông tin chi tiết của một cấu hình thuế dựa trên ID. Trả về lỗi 404 nếu không tìm thấy.")
    public ApiResponse<VatRuleResponse> getVatRuleById(@PathVariable Integer id) {
        return ApiResponse.<VatRuleResponse>builder()
                .result(vatRuleService.getVatRuleById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật cấu hình thuế", description = "Cập nhật các thông tin của cấu hình thuế theo ID. Không cho phép sửa code.")
    public ApiResponse<VatRuleResponse> updateVatRule(
            @PathVariable Integer id,
            @Valid @RequestBody VatRuleUpdateRequest request) {
        return ApiResponse.<VatRuleResponse>builder()
                .result(vatRuleService.updateVatRule(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm cấu hình thuế", description = "Thực hiện xóa mềm cấu hình thuế bằng cách đặt isDeleted = true.")
    public ApiResponse<Void> deleteVatRule(@PathVariable Integer id) {
        vatRuleService.deleteVatRule(id);
        return ApiResponse.<Void>builder()
                .message("Vat rule deleted successfully")
                .build();
    }
}
