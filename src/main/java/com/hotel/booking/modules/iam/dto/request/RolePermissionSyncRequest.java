package com.hotel.booking.modules.iam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO dùng để đồng bộ danh sách Quyền cho Vai trò (Role)")
public class RolePermissionSyncRequest {

    @NotNull(message = "ROLE_PERMISSION_IDS_NOT_NULL")
    @Schema(description = "Danh sách ID của các quyền cần gán cho Vai trò", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    Set<Short> permissionIds;
}
