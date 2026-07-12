package com.hotel.booking.modules.booking.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.dto.response.InvoicePrintResponse;
import com.hotel.booking.modules.booking.entity.*;
import com.hotel.booking.modules.booking.enums.ChargeType;
import com.hotel.booking.modules.booking.enums.InvoiceLineType;
import com.hotel.booking.modules.booking.enums.PaymentStatus;
import com.hotel.booking.modules.booking.repository.BookingRepository;
import com.hotel.booking.modules.booking.repository.InvoiceDetailRepository;
import com.hotel.booking.modules.booking.repository.InvoiceRepository;
import com.hotel.booking.modules.booking.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InvoiceServiceImpl implements InvoiceService{
    InvoiceDetailRepository invoiceDetailRepository;
    InvoiceRepository invoiceRepository;
    BookingRepository bookingRepository;
    PaymentRepository paymentRepository;

    /**
     * Hàm dùng chung để thêm 1 dòng vào hóa đơn
     */
    @Transactional
    public void addLine(Long invoiceId, InvoiceLineType type, String description,
                        Integer qty, BigDecimal unitPrice, BigDecimal vatRate) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(qty));

        BigDecimal serviceFeeRate = BigDecimal.ZERO;
        if (type != InvoiceLineType.PAYMENT) {
            serviceFeeRate = invoice.getServiceFeeRate() != null ? invoice.getServiceFeeRate() : BigDecimal.ZERO;
        }

        BigDecimal lineServiceFee = subtotal.multiply(serviceFeeRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal taxableAmount = subtotal.add(lineServiceFee);

        BigDecimal vatAmount = taxableAmount.multiply(vatRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal total = taxableAmount.add(vatAmount);

        InvoiceDetail detail = InvoiceDetail.builder()
                .invoice(invoice)
                .lineType(InvoiceLineType.valueOf(type.name()))
                .description(description)
                .quantity(qty)
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .vatRate(vatRate)
                .vatAmount(vatAmount)
                .totalAmount(total)
                .build();

        // CHỈ SAVE DETAIL
        invoiceDetailRepository.save(detail);

        log.info("Đã thêm dòng hóa đơn [{}] vào Invoice [{}]. Total: {}", type, invoiceId, total);
    }

    public InvoiceLineType mapToLineType(Object entity) {
        // Nếu là BookingDetail (Tiền phòng)
        if (entity instanceof BookingDetail) {
            return InvoiceLineType.ROOM;
        }

        // Nếu là ChargeType (Phụ thu, Dịch vụ, POS...)
        if (entity instanceof ChargeType type) {
            return switch (type) {
                case PACKAGE_ITEM -> InvoiceLineType.PACKAGE_ITEM;
                case EXTRA_SERVICE -> InvoiceLineType.EXTRA_SERVICE;
                case EXTRA_PERSON, EXTRA_BED, EARLY_CHECKIN, LATE_CHECKOUT -> InvoiceLineType.SURCHARGE;
                case PRICE_ADJUSTMENT, OTHER -> InvoiceLineType.OTHER;
            };
        }

        return InvoiceLineType.OTHER;
    }

    @Override
    @Transactional(readOnly = true)
    public InvoicePrintResponse getInvoiceForPrint(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Invoice invoice = invoiceRepository.findTopByBookingIdOrderByCreatedAtDesc(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        List<InvoiceDetail> details = invoiceDetailRepository.findByInvoiceId(invoice.getId());

        // 1. Map DTO chi tiết
        List<InvoicePrintResponse.InvoiceLineResponse> lineItems = details.stream()
                .map(detail -> InvoicePrintResponse.InvoiceLineResponse.builder()
                        .id(detail.getId())
                        .lineType(detail.getLineType().toString()) // Nếu là String. Nếu Entity là Enum thì .name()
                        .description(detail.getDescription())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .subTotal(detail.getSubtotal())
                        .vatRate(detail.getVatRate())
                        .vatAmount(detail.getVatAmount())
                        .totalAmount(detail.getTotalAmount())
                        .build())
                .collect(Collectors.toList());

        // 2. Kéo lịch sử thanh toán
        List<Payment> payments = paymentRepository.findByBookingIdAndStatus(bookingId, PaymentStatus.SUCCESS);
        List<InvoicePrintResponse.PaymentHistoryResponse> paymentHistories = payments.stream()
                .map(p -> InvoicePrintResponse.PaymentHistoryResponse.builder()
                        .paymentMethod(p.getPaymentMethod().name())
                        .transactionReference(p.getTransactionReference())
                        .paidAt(p.getPaidAt())
                        .amount(p.getTotalAmount())
                        .build())
                .collect(Collectors.toList());

        // ==========================================================
        // 3. TÍNH TOÁN THEO ĐÚNG NGHIỆP VỤ (LƯU TRÚ - DỊCH VỤ - KHÁC)
        // ==========================================================

        // Nhóm 1: Lưu trú (ROOM + SURCHARGE)
        BigDecimal accommodationSubTotal = details.stream()
                .filter(d -> "ROOM".equals(d.getLineType()) || "SURCHARGE".equals(d.getLineType()))
                .map(InvoiceDetail::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal accommodationVatAmount = details.stream()
                .filter(d -> "ROOM".equals(d.getLineType()) || "SURCHARGE".equals(d.getLineType()))
                .map(InvoiceDetail::getVatAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Nhóm 2: Dịch vụ (PACKAGE_ITEM + EXTRA_SERVICE)
        BigDecimal serviceSubTotal = details.stream()
                .filter(d -> "PACKAGE_ITEM".equals(d.getLineType()) || "EXTRA_SERVICE".equals(d.getLineType()))
                .map(InvoiceDetail::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal serviceVatAmount = details.stream()
                .filter(d -> "PACKAGE_ITEM".equals(d.getLineType()) || "EXTRA_SERVICE".equals(d.getLineType()))
                .map(InvoiceDetail::getVatAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Nhóm 3: Khác (OTHER)
        BigDecimal otherSubTotal = details.stream()
                .filter(d -> "OTHER".equals(d.getLineType()))
                .map(InvoiceDetail::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal otherVatAmount = details.stream()
                .filter(d -> "OTHER".equals(d.getLineType()))
                .map(InvoiceDetail::getVatAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // ==========================================================
        // 4. BẢNG KÊ THUẾ (BÓC TÁCH VAT 8%, 10%, 0%...) ĐỈNH CAO
        // ==========================================================
        List<InvoicePrintResponse.VatBreakdownResponse> vatBreakdowns = details.stream()
                // Bỏ qua dòng cấn trừ thanh toán (PAYMENT)
                .filter(d -> !"PAYMENT".equals(d.getLineType()))
                // Group theo VatRate
                .collect(Collectors.groupingBy(InvoiceDetail::getVatRate))
                .entrySet().stream()
                .map(entry -> {
                    BigDecimal rate = entry.getKey();
                    List<InvoiceDetail> items = entry.getValue();

                    BigDecimal taxable = items.stream().map(InvoiceDetail::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal tax = items.stream().map(InvoiceDetail::getVatAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

                    return InvoicePrintResponse.VatBreakdownResponse.builder()
                            .vatRate(rate)
                            .taxableAmount(taxable)
                            .vatAmount(tax)
                            .build();
                })
                .collect(Collectors.toList());

        // Tính cấn trừ / còn lại
        BigDecimal totalPaid = payments.stream()
                .map(Payment::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingBalance = invoice.getGrandTotal().subtract(totalPaid);

        // 5. Build DTO
        return InvoicePrintResponse.builder()
                .hotelName(booking.getHotel().getName())
                .hotelAddress(booking.getHotel().getAddress())
                .invoiceNumber(invoice.getInvoiceNumber())
                .status(invoice.getStatus().name())
                .issuedAt(invoice.getIssuedAt())

                .bookingNumber(booking.getBookingNumber())
                .guestFullName(booking.getGuest().getFullName())
                .guestPhone(booking.getGuest().getPhone())
                .checkInDate(booking.getBookingDetails().isEmpty() ? null : booking.getBookingDetails().get(0).getCheckInDate())
                .checkOutDate(booking.getBookingDetails().isEmpty() ? null : booking.getBookingDetails().get(0).getCheckOutDate())

                .lineItems(lineItems)
                .paymentHistories(paymentHistories)

                // DATA TÀI CHÍNH
                .accommodationSubTotal(accommodationSubTotal)
                .accommodationVatAmount(accommodationVatAmount)
                .serviceSubTotal(serviceSubTotal)
                .serviceVatAmount(serviceVatAmount)
                .otherSubTotal(otherSubTotal)
                .otherVatAmount(otherVatAmount)
                .vatBreakdowns(vatBreakdowns) // Kế toán nhìn cái list này là ưng ngay

                .serviceFeeAmount(invoice.getServiceFeeAmount())
                .grandTotal(invoice.getGrandTotal())
                .totalPaid(totalPaid)
                .remainingBalance(remainingBalance)
                .build();
    }
}
