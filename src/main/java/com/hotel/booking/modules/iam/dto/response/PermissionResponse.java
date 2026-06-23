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
@Schema(description = "DTO phản hồi thông tin chi tiết của Quyền (Permission)")
public class PermissionResponse {

    @Schema(description = "ID định danh của quyền", example = "1")
    Short id;

    @Schema(description = "Hành động của quyền (ví dụ: READ, CREATE, UPDATE, DELETE)", example = "CREATE")
    String action;

    @Schema(description = "Tài nguyên được gán quyền (ví dụ: USER, ROLE, BOOKING)", example = "ROOM")
    String resource;

    @Schema(description = "Trạng thái hoạt động của quyền", example = "ACTIVE")
    ActiveStatus status;
}
