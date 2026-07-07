package com.hotel.booking.modules.inventory.dto.response;

import com.hotel.booking.modules.inventory.enums.ItemUsage;
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
@Schema(description = "Thông tin chi tiết về mặt hàng gán kèm của loại phòng")
public class HotelRoomTypeCatalogItemResponse {

    @Schema(description = "ID của mặt hàng trong danh mục (CatalogItem)", example = "1")
    Integer catalogItemId;

    @Schema(description = "Tên mặt hàng", example = "Bữa sáng Buffet")
    String itemName;

    @Schema(description = "Cách sử dụng (MANDATORY, OPTIONAL)", example = "MANDATORY")
    ItemUsage itemUsage;

    @Schema(description = "Giá áp dụng", example = "50000.00")
    BigDecimal price;
}
