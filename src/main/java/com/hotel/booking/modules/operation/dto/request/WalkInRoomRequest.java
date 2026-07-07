package com.hotel.booking.modules.operation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class WalkInRoomRequest {
    @NotNull
    Integer roomInstanceId;

    @NotNull
    LocalDate checkInDate;

    @NotNull
    LocalDate checkOutDate;

    @Min(1) int adultCount;
    @Min(0) int childCount;
    @Min(0) int infantCount;

    List<AddOnRequest> addOns;

    // 4. DANH SÁCH KHÁCH NGỦ TRONG PHÒNG NÀY
    @NotEmpty(message = "GUEST_QUANTITY_INVALID")
    @Valid
    List<WalkInGuestRequest> roomGuests;
}

