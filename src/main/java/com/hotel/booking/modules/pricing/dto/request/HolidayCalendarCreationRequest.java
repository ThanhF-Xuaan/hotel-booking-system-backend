package com.hotel.booking.modules.pricing.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO dùng để tạo mới Ngày lễ (HolidayCalendar)")
public class HolidayCalendarCreationRequest {

    @NotBlank(message = "HOLIDAY_NAME_NOT_BLANK")
    @Size(max = 150, message = "HOLIDAY_NAME_MAX_LENGTH")
    @Schema(description = "Tên ngày lễ", example = "Tết Dương Lịch", requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @NotNull(message = "HOLIDAY_DATE_NOT_NULL")
    @Schema(description = "Ngày diễn ra ngày lễ", example = "2026-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate date;

    @Schema(description = "Mô tả thêm về ngày lễ", example = "Nghỉ Tết dương lịch 1 ngày")
    String description;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
