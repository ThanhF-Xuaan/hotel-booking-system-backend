package com.hotel.booking.modules.inventory.dto.request;

import com.hotel.booking.modules.inventory.enums.ItemUsage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
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
@Schema(description = "Yêu cầu cấu hình cho từng mặt hàng trong danh mục gán cho loại phòng")
public class CatalogItemSyncItemRequest {

    @NotNull(message = "HOTEL_ROOM_TYPE_CATALOG_ITEM_ID_NOT_NULL")
    @Schema(description = "ID của mặt hàng trong danh mục (CatalogItem)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer catalogItemId;

    @NotNull(message = "HOTEL_ROOM_TYPE_CATALOG_ITEM_USAGE_NOT_NULL")
    @Schema(description = "Cách sử dụng mặt hàng (MANDATORY, OPTIONAL)", example = "MANDATORY", requiredMode = Schema.RequiredMode.REQUIRED)
    ItemUsage itemUsage;

    @NotNull(message = "HOTEL_ROOM_TYPE_CATALOG_ITEM_PRICE_NOT_NULL")
    @DecimalMin(value = "0.0", message = "HOTEL_ROOM_TYPE_CATALOG_ITEM_PRICE_MIN_ZERO")
    @Schema(description = "Giá của mặt hàng", example = "50000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal price;
}
