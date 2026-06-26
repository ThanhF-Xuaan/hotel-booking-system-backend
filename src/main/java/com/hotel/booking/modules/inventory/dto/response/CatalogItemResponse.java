package com.hotel.booking.modules.inventory.dto.response;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.inventory.enums.ItemType;
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
@Schema(description = "DTO phản hồi thông tin mặt hàng trong danh mục (CatalogItem)")
public class CatalogItemResponse {

    @Schema(description = "ID của mặt hàng", example = "1")
    Integer id;

    @Schema(description = "ID của khách sạn sở hữu", example = "1")
    Short hotelId;

    @Schema(description = "Tên mặt hàng/dịch vụ", example = "Dịch vụ giặt ủi")
    String name;

    @Schema(description = "Loại danh mục mặt hàng", example = "ROOM_SERVICE")
    ItemType itemType;

    @Schema(description = "ID của cấu hình thuế VAT áp dụng", example = "1")
    Integer vatRuleId;

    @Schema(description = "Giá cơ bản của mặt hàng", example = "150000.00")
    BigDecimal basePrice;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
