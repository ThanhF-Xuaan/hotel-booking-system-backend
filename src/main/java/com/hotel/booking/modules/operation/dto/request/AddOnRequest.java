package com.hotel.booking.modules.operation.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddOnRequest {
    Integer catalogItemId;
    @Min(1) Integer quantity;
}
