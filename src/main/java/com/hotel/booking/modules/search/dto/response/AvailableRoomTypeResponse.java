package com.hotel.booking.modules.search.dto.response;

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
@Schema(description = "Thông tin loại phòng trống tìm thấy")
public class AvailableRoomTypeResponse {

    @Schema(description = "ID của cấu hình loại phòng khách sạn", example = "1")
    Integer hotelRoomTypeId;

    @Schema(description = "Tên loại phòng", example = "Deluxe Double")
    String roomTypeName;

    @Schema(description = "Số lượng người lớn tối đa", example = "2")
    Integer maxAdults;

    @Schema(description = "Số lượng trẻ em tối đa", example = "1")
    Integer maxChildren;

    @Schema(description = "Số lượng phòng trống tối thiểu tìm thấy trong giai đoạn", example = "5")
    Integer minAvailableCount;
}
