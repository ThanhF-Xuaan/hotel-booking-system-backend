package com.hotel.booking.modules.inventory.dto.response;

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
@Schema(description = "DTO phản hồi thông tin chi tiết của Loại phòng (RoomType)")
public class RoomTypeResponse {

    @Schema(description = "ID nội bộ của loại phòng", example = "1")
    Short id;

    @Schema(description = "Mã định danh duy nhất của loại phòng", example = "DELUXE")
    String code;

    @Schema(description = "Tên hiển thị của loại phòng", example = "Phòng Deluxe cao cấp")
    String name;

    @Schema(description = "Trạng thái hoạt động của loại phòng", example = "ACTIVE")
    ActiveStatus status;
}
