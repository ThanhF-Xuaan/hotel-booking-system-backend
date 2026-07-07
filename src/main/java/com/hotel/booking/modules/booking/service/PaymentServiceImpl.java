package com.hotel.booking.modules.booking.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.dto.request.PaymentRequest;
import com.hotel.booking.modules.booking.entity.Payment;
import com.hotel.booking.modules.booking.enums.InvoiceLineType;
import com.hotel.booking.modules.booking.enums.PaymentStatus;
import com.hotel.booking.modules.booking.repository.BookingRepository;
import com.hotel.booking.modules.booking.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {
    PaymentRepository paymentRepository;
    BookingRepository bookingRepository;
    InvoiceService invoiceService;

    @Override
    @Transactional
    public void addPayment(Long bookingId, PaymentRequest request) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Payment payment = Payment.builder()
                .booking(booking)
                .totalAmount(request.getAmount()) // Sửa field name từ amount -> totalAmount theo Entity
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.SUCCESS)
                .paidAt(OffsetDateTime.now())
                .transactionReference(request.getTransactionReference())
                .build();

        invoiceService.addLine(
                booking.getInvoice().getId(),
                InvoiceLineType.PAYMENT,
                "Thanh toán qua " + request.getPaymentMethod() + " (Ref: " + request.getTransactionReference() + ")",
                1,
                request.getAmount().negate(), // Số tiền âm để thể hiện cấn trừ
                BigDecimal.ZERO // Payment không có VAT
        );

        paymentRepository.save(payment);
    }
}
