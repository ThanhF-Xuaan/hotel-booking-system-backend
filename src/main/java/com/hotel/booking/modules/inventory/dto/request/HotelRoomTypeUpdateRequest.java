package com.hotel.booking.modules.inventory.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "DTO dùng để cấu hình loại phòng cho khách sạn (HotelRoomType) khi cập nhật")
public class HotelRoomTypeUpdateRequest {

    @NotNull(message = "HOTEL_ROOM_TYPE_STANDARD_ADULTS_NOT_NULL")
    @Min(value = 0, message = "HOTEL_ROOM_TYPE_STANDARD_ADULTS_MIN_ZERO")
    @Schema(description = "Số lượng người lớn tiêu chuẩn", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer standardAdults;

    @NotNull(message = "HOTEL_ROOM_TYPE_STANDARD_CHILDREN_NOT_NULL")
    @Min(value = 0, message = "HOTEL_ROOM_TYPE_STANDARD_CHILDREN_MIN_ZERO")
    @Schema(description = "Số lượng trẻ em tiêu chuẩn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer standardChildren;

    @NotNull(message = "HOTEL_ROOM_TYPE_MAX_ADULTS_NOT_NULL")
    @Min(value = 0, message = "HOTEL_ROOM_TYPE_MAX_ADULTS_MIN_ZERO")
    @Schema(description = "Số lượng người lớn tối đa", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer maxAdults;

    @NotNull(message = "HOTEL_ROOM_TYPE_MAX_CHILDREN_NOT_NULL")
    @Min(value = 0, message = "HOTEL_ROOM_TYPE_MAX_CHILDREN_MIN_ZERO")
    @Schema(description = "Số lượng trẻ em tối đa", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer maxChildren;

    @NotNull(message = "HOTEL_ROOM_TYPE_MAX_INFANTS_NOT_NULL")
    @Min(value = 0, message = "HOTEL_ROOM_TYPE_MAX_INFANTS_MIN_ZERO")
    @Schema(description = "Số lượng trẻ sơ sinh tối đa", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer maxInfants;

    @NotNull(message = "HOTEL_ROOM_TYPE_MAX_TOTAL_GUESTS_NOT_NULL")
    @Min(value = 0, message = "HOTEL_ROOM_TYPE_MAX_TOTAL_GUESTS_MIN_ZERO")
    @Schema(description = "Tổng số lượng khách tối đa", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer maxTotalGuests;

    @NotNull(message = "HOTEL_ROOM_TYPE_MAX_EXTRA_BEDS_NOT_NULL")
    @Min(value = 0, message = "HOTEL_ROOM_TYPE_MAX_EXTRA_BEDS_MIN_ZERO")
    @Schema(description = "Số lượng giường phụ tối đa", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer maxExtraBeds;

    @NotNull(message = "HOTEL_ROOM_TYPE_MAX_BEDS_NOT_NULL")
    @Min(value = 0, message = "HOTEL_ROOM_TYPE_MAX_BEDS_MIN_ZERO")
    @Schema(description = "Số lượng giường vật lý tối đa", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer maxBeds;

    @NotNull(message = "HOTEL_ROOM_TYPE_BASE_PRICE_NOT_NULL")
    @DecimalMin(value = "0.0", message = "HOTEL_ROOM_TYPE_BASE_PRICE_MIN_ZERO")
    @Schema(description = "Giá cơ bản của loại phòng", example = "1200000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal basePrice;

    @NotNull(message = "HOTEL_ROOM_TYPE_TOTAL_QUANTITY_NOT_NULL")
    @Min(value = 0, message = "HOTEL_ROOM_TYPE_TOTAL_QUANTITY_MIN_ZERO")
    @Schema(description = "Tổng số lượng phòng loại này", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer totalQuantity;

    @NotNull(message = "HOTEL_ROOM_TYPE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
