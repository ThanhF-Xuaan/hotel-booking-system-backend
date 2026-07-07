package com.hotel.booking.modules.booking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Payload yêu cầu khởi tạo phiên đặt phòng (Initiate Booking) và giữ chỗ Inventory")
public class InitiateBookingRequest {

    @Schema(description = "ID của khách sạn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Short hotelId;

    @Schema(description = "Ngày nhận phòng dự kiến", example = "2024-12-01", type = "string", format = "date", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate checkIn;

    @Schema(description = "Ngày trả phòng dự kiến", example = "2024-12-05", type = "string", format = "date", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate checkOut;

    @Schema(description = "Thông tin người đại diện đặt phòng", requiredMode = Schema.RequiredMode.REQUIRED)
    GuestInfo guestInfo;

    @Schema(description = "Danh sách các loại phòng và số lượng khách chọn", requiredMode = Schema.RequiredMode.REQUIRED)
    List<RoomSelection> rooms;

    @Data
    @Schema(name = "InitiateGuestInfo", description = "Thông tin chi tiết của người đại diện đặt phòng")
    public static class GuestInfo {

        @Schema(description = "Tên", example = "Anh", requiredMode = Schema.RequiredMode.REQUIRED)
        private String firstName;

        @Schema(description = "Họ và tên đệm", example = "Nguyễn Tuấn", requiredMode = Schema.RequiredMode.REQUIRED)
        private String lastName;

        @Schema(description = "Họ và tên đầy đủ", example = "Nguyễn Tuấn Anh", requiredMode = Schema.RequiredMode.REQUIRED)
        private String fullName;

        @Schema(description = "Ngày sinh", example = "1995-08-20", type = "string", format = "date")
        LocalDate birthDate;

        @Schema(description = "Quốc tịch", example = "VN")
        String nationality;

        @Schema(description = "Địa chỉ Email liên hệ", example = "tuananh@example.com", type = "string", format = "email")
        private String email;

        @Schema(description = "Số điện thoại liên hệ", example = "0901234567", requiredMode = Schema.RequiredMode.REQUIRED)
        private String phone;
    }

    @Data
    @Schema(description = "Thông tin loại phòng khách hàng muốn đặt")
    public static class RoomSelection {

        @Schema(description = "ID của loại phòng thuộc khách sạn (Bảng HotelRoomType)", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer hotelRoomTypeId;

        @Schema(description = "Số lượng phòng khách muốn đặt cho loại này", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer quantity;

        @Schema(description = "Chi tiết số lượng người lưu trú cho từng phòng (Mảng này phải có độ dài bằng với quantity)", requiredMode = Schema.RequiredMode.REQUIRED)
        @Valid
        private List<RoomOccupancy> occupancies;

        @Schema(description = "Danh sách dịch vụ mua thêm (OPTIONAL - Ví dụ: Xe đưa đón, Giường phụ). Bỏ trống nếu không mua.")
        @Valid
        private List<SelectedAddOn> addOns;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Chi tiết khai báo số lượng khách lưu trú trong 1 căn phòng")
    public static class RoomOccupancy {

        @Schema(description = "Số lượng người lớn", example = "2", defaultValue = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer adults;

        @Schema(description = "Số lượng trẻ em (Tuổi từ cấu hình Policy)", example = "1", defaultValue = "0")
        private Integer children;

        @Schema(description = "Số lượng em bé (Tuổi từ cấu hình Policy)", example = "0", defaultValue = "0")
        private Integer infants;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Chi tiết dịch vụ mua kèm")
    public static class SelectedAddOn {

        @Schema(description = "ID của hàng hóa/dịch vụ (Bảng CatalogItem)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private Integer catalogItemId;

        @Schema(description = "Số lượng mua", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(1)
        private Integer quantity;
    }
}