package com.hotel.booking.modules.iam.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "DTO dùng để tạo mới Quyền (Permission)")
public class PermissionCreationRequest {

    @NotBlank(message = "PERMISSION_ACTION_NOT_BLANK")
    @Schema(description = "Hành động của quyền (ví dụ: READ, CREATE, UPDATE, DELETE)", example = "CREATE", requiredMode = Schema.RequiredMode.REQUIRED)
    String action;

    @NotBlank(message = "PERMISSION_RESOURCE_NOT_BLANK")
    @Schema(description = "Tài nguyên được gán quyền (ví dụ: USER, ROLE, BOOKING)", example = "ROOM", requiredMode = Schema.RequiredMode.REQUIRED)
    String resource;

    @NotNull(message = "PERMISSION_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động của quyền", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
