package com.hotel.booking.modules.inventory.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO dùng để cập nhật Khách sạn (Hotel)")
public class HotelUpdateRequest {

    @NotBlank(message = "HOTEL_NAME_NOT_BLANK")
    @Schema(description = "Tên khách sạn", example = "Khách sạn Mường Thanh", requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @NotBlank(message = "HOTEL_ADDRESS_NOT_BLANK")
    @Schema(description = "Địa chỉ khách sạn", example = "12 Thư Khê, Hà Nội", requiredMode = Schema.RequiredMode.REQUIRED)
    String address;

    @Schema(description = "Số điện thoại liên hệ", example = "0243123456")
    String phone;

    @NotNull(message = "HOTEL_CHECKIN_TIME_NOT_NULL")
    @Schema(description = "Giờ nhận phòng", example = "14:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalTime checkInTime;

    @NotNull(message = "HOTEL_CHECKOUT_TIME_NOT_NULL")
    @Schema(description = "Giờ trả phòng", example = "12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalTime checkOutTime;

    @NotNull(message = "HOTEL_SERVICE_FEE_NOT_NULL")
    @DecimalMin(value = "0.0", message = "HOTEL_SERVICE_FEE_MIN")
    @DecimalMax(value = "100.0", message = "HOTEL_SERVICE_FEE_MAX")
    @Schema(description = "Phần trăm phí dịch vụ", example = "5.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal serviceFeePercent;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
