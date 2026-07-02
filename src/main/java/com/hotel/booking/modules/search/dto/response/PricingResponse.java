package com.hotel.booking.modules.search.dto.response;

import com.hotel.booking.modules.search.dto.request.RoomOccupancy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Báo giá phòng nghỉ chi tiết dạng danh sách phẳng")
public class PricingResponse {

    @Schema(description = "Danh sách chi tiết báo giá từng phòng")
    List<RoomPricingResult> rooms;

    @Schema(description = "Tổng cộng báo giá toàn bộ các phòng đặt")
    PriceDetail grandTotal;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RoomPricingResult {
        Integer hotelRoomTypeId;
        RoomOccupancy occupancy;
        PriceDetail priceDetail;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PriceDetail {
        BigDecimal basePrice;
        BigDecimal priceAdjustment;
        BigDecimal discountAmount;
        BigDecimal surchargeAmount;
        BigDecimal serviceFeeAmount;
        BigDecimal taxAmount;
        BigDecimal finalPrice;
    }
}
