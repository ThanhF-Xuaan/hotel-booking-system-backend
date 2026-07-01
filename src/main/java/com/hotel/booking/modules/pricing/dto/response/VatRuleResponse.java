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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO kết quả trả về của Cấu hình thuế (VatRule)")
public class VatRuleResponse {

    @Schema(description = "ID của cấu hình thuế", example = "1")
    Integer id;

    @Schema(description = "Mã định danh duy nhất của thuế", example = "VAT10")
    String vatCode;

    @Schema(description = "Tên hiển thị của thuế", example = "Thuế giá trị gia tăng 10%")
    String vatName;

    @Schema(description = "Phần trăm thuế", example = "10.00")
    BigDecimal vatPercent;

    @Schema(description = "ID của danh mục thuế", example = "1")
    Integer taxCategoryId;

    @Schema(description = "Ngày bắt đầu hiệu lực", example = "2026-01-01")
    LocalDate startDate;

    @Schema(description = "Ngày kết thúc hiệu lực", example = "2026-12-31")
    LocalDate endDate;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;

    @Schema(description = "Thời điểm tạo bản ghi")
    OffsetDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật bản ghi cuối cùng")
    OffsetDateTime updatedAt;
}
