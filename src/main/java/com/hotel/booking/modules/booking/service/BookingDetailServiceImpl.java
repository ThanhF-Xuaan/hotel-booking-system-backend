package com.hotel.booking.modules.booking.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.entity.BookingDetail;
import com.hotel.booking.modules.booking.repository.BookingDetailRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingDetailServiceImpl implements BookingDetailService{
    BookingDetailRepository bookingDetailRepository;

    @Override
    public Map<String, Object> getBookingDetailOccupancy(Long bookingDetailId) {
        BookingDetail detail = bookingDetailRepository.findById(bookingDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Map<String, Object> result = new HashMap<>();
        result.put("bookingDetailId", detail.getId());
        result.put("adultCount", detail.getAdultCount() != null ? (int) detail.getAdultCount() : 0);
        result.put("childCount", detail.getChildCount() != null ? (int) detail.getChildCount() : 0);
        result.put("infantCount", detail.getInfantCount() != null ? (int) detail.getInfantCount() : 0);
        result.put("guestCount", detail.getGuestCount() != null ? (int) detail.getGuestCount() : 0);
        result.put("roomTypeName", detail.getRoomTypeName());
        return result;
    }
}
