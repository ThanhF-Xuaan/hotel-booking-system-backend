package com.hotel.booking.modules.pricing.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.pricing.dto.request.CampaignCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.CampaignUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.CampaignResponse;
import com.hotel.booking.modules.pricing.service.CampaignService;
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
@RequestMapping("/api/v1/pricing/campaigns")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Pricing - Campaign Management", description = "Các API quản lý Chiến dịch Marketing (Campaign)")
public class CampaignController {

    CampaignService campaignService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới chiến dịch", description = "Tạo mới chiến dịch marketing cho một khách sạn cụ thể.")
    public ApiResponse<CampaignResponse> createCampaign(@Valid @RequestBody CampaignCreateRequest request) {
        return ApiResponse.<CampaignResponse>builder()
                .result(campaignService.createCampaign(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách chiến dịch", description = "Lấy toàn bộ các chiến dịch đang hoạt động. Có thể lọc theo hotelId.")
    public ApiResponse<List<CampaignResponse>> getCampaigns(
            @RequestParam(required = false) Short hotelId) {
        return ApiResponse.<List<CampaignResponse>>builder()
                .result(campaignService.getCampaigns(hotelId))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết chiến dịch", description = "Trả về thông tin chi tiết của chiến dịch theo ID.")
    public ApiResponse<CampaignResponse> getCampaignById(@PathVariable Integer id) {
        return ApiResponse.<CampaignResponse>builder()
                .result(campaignService.getCampaignById(id))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật chiến dịch", description = "Cập nhật thông tin chiến dịch hiện có.")
    public ApiResponse<CampaignResponse> updateCampaign(
            @PathVariable Integer id,
            @Valid @RequestBody CampaignUpdateRequest request) {
        return ApiResponse.<CampaignResponse>builder()
                .result(campaignService.updateCampaign(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mềm chiến dịch", description = "Set isDeleted = true cho chiến dịch.")
    public ApiResponse<Void> deleteCampaign(@PathVariable Integer id) {
        campaignService.deleteCampaign(id);
        return ApiResponse.<Void>builder()
                .message("Campaign deleted successfully")
                .build();
    }
}
