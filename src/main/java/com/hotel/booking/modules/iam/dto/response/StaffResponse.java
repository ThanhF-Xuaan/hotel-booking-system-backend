package com.hotel.booking.modules.iam.dto.response;

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
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO kết quả trả về của nhân viên (Staff)")
public class StaffResponse {

    @Schema(description = "ID của nhân viên", example = "1")
    Integer id;

    @Schema(description = "ID công khai duy nhất của nhân viên", example = "d9b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2")
    UUID publicId;

    @Schema(description = "Tên đăng nhập", example = "staff_john")
    String username;

    @Schema(description = "Tên", example = "John")
    String firstName;

    @Schema(description = "Họ", example = "Doe")
    String lastName;

    @Schema(description = "Họ và tên đầy đủ", example = "John Doe")
    String fullName;

    @Schema(description = "ID Khách sạn quản lý", example = "1")
    Short hotelId;

    @Schema(description = "Tên Khách sạn", example = "Grand Plaza Hotel")
    String hotelName;

    @Schema(description = "ID Vai trò", example = "1")
    Short roleId;

    @Schema(description = "Tên Vai trò", example = "Receptionist")
    String roleName;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;

    @Schema(description = "Thời điểm tạo bản ghi")
    OffsetDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật bản ghi cuối cùng")
    OffsetDateTime updatedAt;
}
