package com.hotel.booking.modules.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
@Schema(description = "Thông tin chi tiết cấu hình giường và số lượng để đồng bộ")
public class RoomBedSyncItemRequest {

    @NotNull(message = "HOTEL_ROOM_TYPE_BED_ROOM_BED_ID_NOT_NULL")
    @Schema(description = "ID của loại giường (RoomBed)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Short roomBedId;

    @NotNull(message = "HOTEL_ROOM_TYPE_BED_QUANTITY_NOT_NULL")
    @Min(value = 1, message = "HOTEL_ROOM_TYPE_BED_QUANTITY_MIN_ONE")
    @Schema(description = "Số lượng giường của loại này trong phòng", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    Short quantity;
}
