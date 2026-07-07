package com.hotel.booking.modules.pricing.entity.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountCondition {

    Integer minNights;
    Integer maxNights;
    Integer minAdvanceBookingDays;
    String promoCode;
}
