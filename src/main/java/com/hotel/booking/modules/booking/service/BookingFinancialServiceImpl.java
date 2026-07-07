package com.hotel.booking.modules.booking.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.entity.Booking;
import com.hotel.booking.modules.booking.enums.InvoiceStatus;
import com.hotel.booking.modules.booking.repository.BookingRepository;
import com.hotel.booking.modules.booking.repository.InvoiceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingFinancialServiceImpl implements BookingFinancialService {

    BookingRepository bookingRepository;
    InvoiceRepository invoiceRepository;

    @Override
    // [ĐÃ SỬA]: TRẢ LẠI SỰ TRONG SÁNG! BỎ REQUIRES_NEW ĐI ĐỂ CHẠY CHUNG TRANSACTION VỚI WALK-IN!
    @Transactional
    public void addAmountToBooking(
            Long bookingId,
            BigDecimal subtotal,
            BigDecimal serviceFeeAmount,
            BigDecimal vatAmount,
            BigDecimal totalAmount
    ) {
        // 1. DÙNG PESSIMISTIC LOCK ĐỂ CHỐNG MẤT TIỀN (RACE CONDITION)
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        BigDecimal currentSubtotal = Optional.ofNullable(booking.getSubtotalAmount()).orElse(BigDecimal.ZERO);
        BigDecimal currentServiceFee = Optional.ofNullable(booking.getServiceFeeAmount()).orElse(BigDecimal.ZERO);
        BigDecimal currentVat = Optional.ofNullable(booking.getTotalVatAmount()).orElse(BigDecimal.ZERO);
        BigDecimal currentTotal = Optional.ofNullable(booking.getTotalAmount()).orElse(BigDecimal.ZERO);

        booking.setSubtotalAmount(currentSubtotal.add(subtotal));
        booking.setServiceFeeAmount(currentServiceFee.add(serviceFeeAmount));
        booking.setTotalVatAmount(currentVat.add(vatAmount));
        booking.setTotalAmount(currentTotal.add(totalAmount));

        // KHÔNG CẦN .save() NHỜ DIRTY CHECKING HOẠT ĐỘNG CHUẨN TRONG CÙNG 1 TRANSACTION

        // 2. TƯƠNG TỰ KHÓA LUÔN INVOICE
        invoiceRepository.findByBookingIdAndStatusForUpdate(bookingId, InvoiceStatus.DRAFT)
                .ifPresent(invoice -> {
                    BigDecimal invSubtotal = Optional.ofNullable(invoice.getSubTotal()).orElse(BigDecimal.ZERO);
                    BigDecimal invServiceFee = Optional.ofNullable(invoice.getServiceFeeAmount()).orElse(BigDecimal.ZERO);
                    BigDecimal invVat = Optional.ofNullable(invoice.getVatAmount()).orElse(BigDecimal.ZERO);
                    BigDecimal invTotal = Optional.ofNullable(invoice.getGrandTotal()).orElse(BigDecimal.ZERO);

                    invoice.setSubTotal(invSubtotal.add(subtotal));
                    invoice.setServiceFeeAmount(invServiceFee.add(serviceFeeAmount));
                    invoice.setVatAmount(invVat.add(vatAmount));
                    invoice.setGrandTotal(invTotal.add(totalAmount));
                });
    }
}