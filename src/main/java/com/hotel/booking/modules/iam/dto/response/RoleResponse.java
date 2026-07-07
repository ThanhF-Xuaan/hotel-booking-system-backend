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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi thông tin chi tiết của Nhóm Quyền (Role)")
public class RoleResponse {

    @Schema(description = "ID định danh nội bộ của nhóm quyền", example = "1")
    Short id;

    @Schema(description = "Tên hiển thị của nhóm quyền", example = "Quản lý")
    String name;

    @Schema(description = "Mã định danh duy nhất của nhóm quyền", example = "MANAGER")
    String code;

    @Schema(description = "Trạng thái hoạt động của nhóm quyền", example = "ACTIVE")
    ActiveStatus status;
}
