package com.hotel.booking.modules.iam.dto.request;

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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO dùng để tạo mới Nhóm Quyền (Role)")
public class RoleCreationRequest {

    @NotBlank(message = "ROLE_NAME_NOT_BLANK")
    @Size(max = 100, message = "ROLE_NAME_MAX_LENGTH")
    @Schema(description = "Tên hiển thị của nhóm quyền", example = "Quản lý", requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @NotBlank(message = "ROLE_CODE_NOT_BLANK")
    @Size(max = 50, message = "ROLE_CODE_MAX_LENGTH")
    @Schema(description = "Mã định danh duy nhất của nhóm quyền (viết hoa, không dấu)", example = "MANAGER", requiredMode = Schema.RequiredMode.REQUIRED)
    String code;

    @NotNull(message = "ROLE_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động của nhóm quyền", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
