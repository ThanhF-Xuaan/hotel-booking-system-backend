package com.hotel.booking.modules.iam.dto.request;

import com.hotel.booking.core.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO dùng để cập nhật trạng thái của Quyền (Permission)")
public class PermissionUpdateRequest {

    @NotNull(message = "PERMISSION_STATUS_NOT_NULL")
    @Schema(description = "Trạng thái hoạt động của quyền", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    ActiveStatus status;
}
