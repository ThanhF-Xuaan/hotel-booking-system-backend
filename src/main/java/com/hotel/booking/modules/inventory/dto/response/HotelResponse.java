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
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi thông tin chi tiết của Khách sạn (Hotel)")
public class HotelResponse {

    @Schema(description = "ID nội bộ của khách sạn", example = "1")
    Short id;

    @Schema(description = "Tên khách sạn", example = "Khách sạn Mường Thanh")
    String name;

    @Schema(description = "Địa chỉ khách sạn", example = "12 Thư Khê, Hà Nội")
    String address;

    @Schema(description = "Số điện thoại liên hệ", example = "0243123456")
    String phone;

    @Schema(description = "Giờ nhận phòng", example = "14:00:00")
    LocalTime checkInTime;

    @Schema(description = "Giờ trả phòng", example = "12:00:00")
    LocalTime checkOutTime;

    @Schema(description = "Phần trăm phí dịch vụ", example = "5.00")
    BigDecimal serviceFeePercent;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
