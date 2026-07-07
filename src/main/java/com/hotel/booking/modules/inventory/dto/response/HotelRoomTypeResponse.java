package com.hotel.booking.modules.inventory.dto.response;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi chi tiết cấu hình loại phòng cho khách sạn (HotelRoomType)")
public class HotelRoomTypeResponse {

    @Schema(description = "ID của cấu hình", example = "1")
    Integer id;

    @Schema(description = "ID của khách sạn sở hữu", example = "1")
    Short hotelId;

    @Schema(description = "ID của loại phòng", example = "1")
    Short roomTypeId;

    @Schema(description = "Số lượng người lớn tiêu chuẩn", example = "2")
    Integer standardAdults;

    @Schema(description = "Số lượng trẻ em tiêu chuẩn", example = "1")
    Integer standardChildren;

    @Schema(description = "Số lượng người lớn tối đa", example = "4")
    Integer maxAdults;

    @Schema(description = "Số lượng trẻ em tối đa", example = "2")
    Integer maxChildren;

    @Schema(description = "Số lượng trẻ sơ sinh tối đa", example = "1")
    Integer maxInfants;

    @Schema(description = "Tổng số lượng khách tối đa", example = "5")
    Integer maxTotalGuests;

    @Schema(description = "Số lượng giường phụ tối đa", example = "1")
    Integer maxExtraBeds;

    @Schema(description = "Số lượng giường vật lý tối đa", example = "3")
    Integer maxBeds;

    @Schema(description = "Giá cơ bản của loại phòng", example = "1200000.00")
    BigDecimal basePrice;

    @Schema(description = "Tổng số lượng phòng loại này", example = "10")
    Integer totalQuantity;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
