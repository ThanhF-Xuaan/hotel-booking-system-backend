package com.hotel.booking.modules.iam.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO dùng để cập nhật nhân viên (Staff)")
public class StaffUpdateRequest {

    @Schema(description = "Mật khẩu mới (nếu muốn thay đổi, lưu plain text)", example = "newpassword123")
    String password;

    @Schema(description = "Tên", example = "John")
    String firstName;

    @Schema(description = "Họ", example = "Doe")
    String lastName;

    @Schema(description = "ID Vai trò", example = "1")
    Short roleId;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
