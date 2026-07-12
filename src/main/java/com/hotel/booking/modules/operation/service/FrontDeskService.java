    package com.hotel.booking.modules.operation.service;

    import com.hotel.booking.modules.operation.dto.request.AddBookingChargeRequest;
    import com.hotel.booking.modules.operation.dto.request.CheckInRequest;
    import com.hotel.booking.modules.operation.dto.request.WalkInBookingRequest;
    import com.hotel.booking.modules.operation.dto.response.WalkInBookingResponse;

    import java.util.Map;

    public interface FrontDeskService {
        void processCheckIn(Long bookingId, CheckInRequest request);

        void addInStayCharge(Long bookingId, AddBookingChargeRequest request);

        void processCheckOut(Long bookingId);

        WalkInBookingResponse processWalkInBooking(Short hotelId, WalkInBookingRequest request);
    }
