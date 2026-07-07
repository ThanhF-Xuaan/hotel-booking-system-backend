package com.hotel.booking.modules.iam.dto.response;

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
@Schema(description = "DTO trả về thông tin Quyền được gán cho Vai trò")
public class AssignedPermissionResponse {

    @Schema(description = "ID của quyền", example = "1")
    Short permissionId;

    @Schema(description = "Tên hiển thị của quyền", example = "CREATE ROOM")
    String name;

    @Schema(description = "Mã định danh của quyền", example = "CREATE_ROOM")
    String code;
}
