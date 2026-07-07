package com.hotel.booking.modules.inventory.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.inventory.enums.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO dùng để cập nhật mặt hàng trong danh mục (CatalogItem)")
public class CatalogItemUpdateRequest {

    @NotBlank(message = "CATALOG_ITEM_NAME_NOT_BLANK")
    @Schema(description = "Tên mặt hàng/dịch vụ", example = "Dịch vụ giặt ủi", requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @NotNull(message = "CATALOG_ITEM_TYPE_NOT_NULL")
    @Schema(description = "Loại danh mục mặt hàng", example = "ROOM_SERVICE", requiredMode = Schema.RequiredMode.REQUIRED)
    ItemType itemType;

    @NotNull(message = "CATALOG_ITEM_TAX_CATEGORY_ID_NOT_NULL")
    @Schema(description = "ID của danh mục thuế áp dụng", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer taxCategoryId;

    @NotNull(message = "CATALOG_ITEM_BASE_PRICE_NOT_NULL")
    @DecimalMin(value = "0.0", message = "CATALOG_ITEM_BASE_PRICE_MIN")
    @Schema(description = "Giá cơ bản của mặt hàng", example = "150000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal basePrice;

    @NotNull(message = "CATALOG_ITEM_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
