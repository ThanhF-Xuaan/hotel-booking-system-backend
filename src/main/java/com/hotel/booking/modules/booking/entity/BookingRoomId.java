package com.hotel.booking.modules.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookingRoomId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "booking_detail_id")
    private Long bookingDetailId;

    @Column(name = "room_instance_id")
    private Integer roomInstanceId;
}
