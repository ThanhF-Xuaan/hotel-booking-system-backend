package com.hotel.booking.modules.operation.dto.request;

import com.hotel.booking.modules.crm.enums.IdentityType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalkInGuestRequest {
    @NotBlank
    String fullName;

    @NotBlank
    String guestType; // "ADULT", "CHILD", "INFANT"

    //Theo schema của mày, nếu guestType = 'ADULT' thì 2 trường dưới BẮT BUỘC phải có!
    IdentityType identityType;   // "CCCD", "PASSPORT"
    String identityNumber; // "00123456789"
    LocalDate birthDate;
}