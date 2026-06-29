package com.hotel.booking.modules.pricing.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Yêu cầu cấu hình các điều kiện áp dụng cho quy tắc giảm giá")
public class DiscountConditionRequest {

    @Schema(description = "Số đêm tối thiểu (chỉ dùng cho LONG_STAY)", example = "3")
    Integer minNights;

    @Schema(description = "Số đêm tối đa (nếu có)", example = "10")
    Integer maxNights;

    @Schema(description = "Số ngày đặt trước tối thiểu (nếu có)", example = "7")
    Integer minAdvanceBookingDays;

    @Schema(description = "Mã khuyến mãi áp dụng (nếu có)", example = "PROMO10")
    String promoCode;
}
