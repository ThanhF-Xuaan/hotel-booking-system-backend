package com.hotel.booking.modules.search.dto.request;

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
@Schema(description = "Sức chứa/Khách đặt cho một phòng")
public class RoomOccupancy {

    @NotNull(message = "OCCUPANCY_ADULTS_NOT_NULL")
    @Min(value = 0, message = "OCCUPANCY_ADULTS_MIN_ZERO")
    @Schema(description = "Số lượng người lớn", example = "2")
    Integer adults;

    @NotNull(message = "OCCUPANCY_CHILDREN_NOT_NULL")
    @Min(value = 0, message = "OCCUPANCY_CHILDREN_MIN_ZERO")
    @Schema(description = "Số lượng trẻ em", example = "0")
    Integer children;

    @NotNull(message = "OCCUPANCY_INFANTS_NOT_NULL")
    @Min(value = 0, message = "OCCUPANCY_INFANTS_MIN_ZERO")
    @Schema(description = "Số lượng em bé", example = "0")
    Integer infants;
}
