package com.hotel.booking.modules.crm.dto.response;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.modules.crm.enums.IdentityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi thông tin chi tiết của Khách hàng (Guest)")
public class GuestResponse {

    @Schema(description = "ID nội bộ của khách hàng", example = "1")
    Long id;

    @Schema(description = "ID công khai của khách hàng", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID publicId;

    @Schema(description = "Tên", example = "An")
    String firstName;

    @Schema(description = "Họ và tên đệm", example = "Nguyễn Văn")
    String lastName;

    @Schema(description = "Họ và tên đầy đủ", example = "Nguyễn Văn An")
    String fullName;

    @Schema(description = "Số điện thoại", example = "0987654321")
    String phone;

    @Schema(description = "Địa chỉ email", example = "an.nguyen@example.com")
    String email;

    @Schema(description = "Loại giấy tờ tùy thân", example = "CCCD")
    IdentityType identityType;

    @Schema(description = "Số giấy tờ tùy thân", example = "012345678901")
    String identityNumber;

    @Schema(description = "Ngày sinh", example = "1990-01-01")
    LocalDate birthDate;

    @Schema(description = "Quốc tịch", example = "Vietnam")
    String nationality;

    @Schema(description = "Trạng thái hoạt động", example = "ACTIVE")
    ActiveStatus status;
}
