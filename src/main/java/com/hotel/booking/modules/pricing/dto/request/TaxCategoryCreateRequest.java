package com.hotel.booking.modules.pricing.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu tạo mới danh mục thuế")
public class TaxCategoryCreateRequest {

    @NotBlank(message = "TAX_CATEGORY_CODE_NOT_BLANK")
    @Size(max = 50, message = "TAX_CATEGORY_CODE_MAX_SIZE")
    @Schema(description = "Mã danh mục thuế", example = "ROOM", requiredMode = Schema.RequiredMode.REQUIRED)
    String categoryCode;

    @NotBlank(message = "TAX_CATEGORY_NAME_NOT_BLANK")
    @Size(max = 150, message = "TAX_CATEGORY_NAME_MAX_SIZE")
    @Schema(description = "Tên danh mục thuế", example = "Dịch vụ lưu trú phòng", requiredMode = Schema.RequiredMode.REQUIRED)
    String categoryName;

    @Schema(description = "Mô tả chi tiết", example = "Áp dụng cho các giao dịch thuê phòng khách sạn")
    String description;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
