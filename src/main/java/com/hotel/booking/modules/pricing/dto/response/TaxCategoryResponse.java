package com.hotel.booking.modules.pricing.dto.response;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Thông tin chi tiết danh mục thuế")
public class TaxCategoryResponse {

    @Schema(description = "ID của danh mục thuế", example = "1")
    Integer id;

    @Schema(description = "Mã danh mục thuế", example = "ROOM")
    String categoryCode;

    @Schema(description = "Tên danh mục thuế", example = "Dịch vụ lưu trú phòng")
    String categoryName;

    @Schema(description = "Mô tả chi tiết", example = "Áp dụng cho các giao dịch thuê phòng khách sạn")
    String description;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;

    @Schema(description = "Thời gian tạo")
    OffsetDateTime createdAt;

    @Schema(description = "Thời gian cập nhật")
    OffsetDateTime updatedAt;
}
