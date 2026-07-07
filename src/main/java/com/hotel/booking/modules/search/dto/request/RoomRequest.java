package com.hotel.booking.modules.search.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu đặt phòng chi tiết sức chứa")
public class RoomRequest {

    @NotNull(message = "ROOM_HOTEL_ROOM_TYPE_ID_NOT_NULL")
    @Schema(description = "ID của cấu hình loại phòng", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer hotelRoomTypeId;

    @NotNull(message = "ROOM_OCCUPANCY_NOT_NULL")
    @Valid
    @Schema(description = "Chi tiết sức chứa/khách đặt phòng", requiredMode = Schema.RequiredMode.REQUIRED)
    RoomOccupancy occupancy;

    @Valid
    @Schema(description = "Danh sách dịch vụ mua thêm (Tùy chọn)")
    List<SelectedAddOn> addOns;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SelectedAddOn {
        @NotNull
        private Integer catalogItemId;
        @Min(1)
        private Integer quantity;
    }
}
