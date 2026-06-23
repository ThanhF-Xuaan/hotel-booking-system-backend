package com.hotel.booking.modules.pricing.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.pricing.enums.AppliesTo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO dùng để tạo mới Cấu hình thuế (VatRule)")
public class VatRuleCreationRequest {

    @NotBlank(message = "VAT_RULE_CODE_NOT_BLANK")
    @Size(max = 50, message = "VAT_RULE_CODE_MAX_LENGTH")
    @Schema(description = "Mã định danh duy nhất của thuế", example = "VAT10", requiredMode = Schema.RequiredMode.REQUIRED)
    String vatCode;

    @NotBlank(message = "VAT_RULE_NAME_NOT_BLANK")
    @Size(max = 150, message = "VAT_RULE_NAME_MAX_LENGTH")
    @Schema(description = "Tên hiển thị của thuế", example = "Thuế giá trị gia tăng 10%", requiredMode = Schema.RequiredMode.REQUIRED)
    String vatName;

    @NotNull(message = "VAT_RULE_PERCENT_NOT_NULL")
    @DecimalMin(value = "0.00", message = "VAT_RULE_PERCENT_MIN")
    @DecimalMax(value = "100.00", message = "VAT_RULE_PERCENT_MAX")
    @Schema(description = "Phần trăm thuế", example = "10.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal vatPercent;

    @NotNull(message = "VAT_RULE_APPLIES_TO_NOT_NULL")
    @Schema(description = "Đối tượng áp dụng thuế", example = "ROOM", requiredMode = Schema.RequiredMode.REQUIRED)
    AppliesTo appliesTo;

    @Schema(description = "Ngày bắt đầu hiệu lực", example = "2026-01-01")
    LocalDate startDate;

    @Schema(description = "Ngày kết thúc hiệu lực", example = "2026-12-31")
    LocalDate endDate;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
