package com.hotel.booking.modules.operation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalkInBookingRequest {
    // 1. THÔNG TIN NGƯỜI ĐẠI DIỆN ĐẶT PHÒNG (Người trả tiền)
    @NotBlank
    String repFirstName;
    @NotBlank
    String repLastName;
    @NotBlank
    String repPhone;
    String repIdentityNumber; // CCCD/Passport của người đại diện

    // 2. DANH SÁCH CÁC PHÒNG ĐƯỢC CHỌN (Đoàn 5 phòng thì add 5 object vào đây)
    @NotEmpty(message = "Phải chọn ít nhất 1 phòng")
    @Valid // Kích hoạt validate các object con bên trong
    List<WalkInRoomRequest> rooms;

    // 3. THÔNG TIN THANH TOÁN (Cho toàn bộ đơn)
    @NotNull
    BigDecimal amountPaid;
    @NotBlank
    String paymentMethod;
}
