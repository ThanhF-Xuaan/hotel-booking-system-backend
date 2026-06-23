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

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO kết quả trả về của Ngày lễ (HolidayCalendar)")
public class HolidayCalendarResponse {

    @Schema(description = "ID của ngày lễ", example = "1")
    Integer id;

    @Schema(description = "Tên ngày lễ", example = "Tết Dương Lịch")
    String name;

    @Schema(description = "Ngày diễn ra ngày lễ", example = "2026-01-01")
    LocalDate date;

    @Schema(description = "Mô tả thêm về ngày lễ", example = "Nghỉ Tết dương lịch 1 ngày")
    String description;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;

    @Schema(description = "Thời điểm tạo bản ghi")
    OffsetDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật bản ghi cuối cùng")
    OffsetDateTime updatedAt;
}
