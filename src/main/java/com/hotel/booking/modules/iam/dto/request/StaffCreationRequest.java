package com.hotel.booking.modules.iam.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "DTO dùng để tạo mới nhân viên (Staff)")
public class StaffCreationRequest {

    @NotBlank(message = "STAFF_USERNAME_NOT_BLANK")
    @Schema(description = "Tên đăng nhập", example = "staff_john", requiredMode = Schema.RequiredMode.REQUIRED)
    String username;

    @NotBlank(message = "STAFF_PASSWORD_NOT_BLANK")
    @Schema(description = "Mật khẩu (lưu dạng plain text)", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    String password;

    @Schema(description = "Tên", example = "John")
    String firstName;

    @Schema(description = "Họ", example = "Doe")
    String lastName;

    @Schema(description = "ID Khách sạn quản lý", example = "1")
    Short hotelId;

    @Schema(description = "ID Vai trò", example = "1")
    Short roleId;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
