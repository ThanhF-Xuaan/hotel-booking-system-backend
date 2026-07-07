package com.hotel.booking.modules.operation.dto.request;

import com.hotel.booking.modules.crm.enums.IdentityType;
import jakarta.validation.constraints.NotBlank;
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
public class CheckInRequest {
    @NotEmpty(message = "ROOM_GUEST_DECLARATION_NOT_EMPTY")
    List<RoomGuestDeclaration> rooms;

    @Data
    public static class RoomGuestDeclaration {
        @NotNull
        private Long bookingDetailId;

        @NotEmpty(message = "GUEST_INFO_NOT_EMPTY")
        private List<GuestInfo> guests;
    }

    @Data
    public static class GuestInfo {
        private Long guestId;

        @NotBlank(message = "GUEST_FULL_NAME_NOT_BLANK")
        private String fullName;

        @NotBlank
        private String guestType; // Bắt buộc truyền ADULT, CHILD, hoặc INFANT

        private LocalDate birthDate; // Tùy chọn

        private IdentityType identityType;
        private String identityNumber;
    }
}
