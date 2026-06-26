package com.hotel.booking.modules.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu đồng bộ danh sách mặt hàng gán cho Loại phòng khách sạn (HotelRoomType)")
public class HotelRoomTypeCatalogItemSyncRequest {

    @NotNull(message = "HOTEL_ROOM_TYPE_CATALOG_ITEMS_NOT_NULL")
    @Schema(description = "Danh sách mặt hàng gán kèm", requiredMode = Schema.RequiredMode.REQUIRED)
    List<@Valid CatalogItemSyncItemRequest> items;
}
