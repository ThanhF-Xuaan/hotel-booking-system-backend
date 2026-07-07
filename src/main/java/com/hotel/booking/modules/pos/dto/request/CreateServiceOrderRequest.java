package com.hotel.booking.modules.pos.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class CreateServiceOrderRequest {
    @NotNull(message = "ROOM_INSTANCE_ID_NOT_NULL")
    private Integer roomInstanceId; // Order giao lên phòng vật lý nào (VD: P.101)?

    @NotEmpty(message = "ORDER_ITEM_NOT_EMPTY")
    private List<OrderItem> items;

    @Data
    public static class OrderItem {
        @NotNull(message = "CATALOG_ITEM_ID_NOT_NULL")
        private Integer catalogItemId; // Mã hàng hóa (Mì xào, Bò húc...)
        @Min(value = 1, message = "MIN_ORDER_ITEN_QUANTITY")
        private Integer quantity;
    }
}
