package com.hotel.booking.modules.booking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoicePrintResponse {

    // 1. THÔNG TIN CHUNG (Header)
    String hotelName;
    String hotelAddress;
    String invoiceNumber;
    String status; // DRAFT, ISSUED, CANCELLED
    OffsetDateTime issuedAt;

    // 2. THÔNG TIN KHÁCH & BOOKING
    String bookingNumber;
    String guestFullName;
    String guestPhone;
    LocalDate checkInDate;
    LocalDate checkOutDate;

    // 3. CHI TIẾT CÁC DÒNG (Body)
    List<InvoiceLineResponse> lineItems;

    // 4. LỊCH SỬ THANH TOÁN
    List<PaymentHistoryResponse> paymentHistories;

    // 5. TỔNG KẾT TÀI CHÍNH (Footer) - CHUẨN NGHIỆP VỤ

    // 5.1 Doanh thu Lưu trú (ROOM + SURCHARGE)
    BigDecimal accommodationSubTotal;
    BigDecimal accommodationVatAmount;

    // 5.2 Doanh thu Dịch vụ (PACKAGE_ITEM + EXTRA_SERVICE)
    BigDecimal serviceSubTotal;
    BigDecimal serviceVatAmount;

    // 5.3 Doanh thu Khác (OTHER)
    BigDecimal otherSubTotal;
    BigDecimal otherVatAmount;

    // 5.4 BẢNG KÊ THUẾ (Bóc tách từng loại VAT 8%, 10%, 0%...)
    List<VatBreakdownResponse> vatBreakdowns;

    BigDecimal serviceFeeAmount;
    BigDecimal grandTotal;
    BigDecimal totalPaid;
    BigDecimal remainingBalance;

    @Getter
    @Setter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class InvoiceLineResponse {
        Long id;
        String lineType;
        String description;
        Integer quantity;
        BigDecimal unitPrice;
        BigDecimal subTotal;
        BigDecimal vatRate;
        BigDecimal vatAmount;
        BigDecimal totalAmount;
    }

    @Getter
    @Setter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentHistoryResponse {
        String paymentMethod;
        String transactionReference;
        OffsetDateTime paidAt;
        BigDecimal amount;
    }

    @Getter
    @Setter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VatBreakdownResponse {
        BigDecimal vatRate;         // Mức thuế (vd: 8.00, 10.00, 0.00)
        BigDecimal taxableAmount;   // Tổng tiền trước thuế chịu mức thuế này
        BigDecimal vatAmount;       // Tiền thuế
    }
}