package com.hotel.booking.modules.booking.service;

import com.hotel.booking.modules.booking.dto.response.InvoicePrintResponse;
import com.hotel.booking.modules.booking.enums.InvoiceLineType;

import java.math.BigDecimal;

public interface InvoiceService {
    void addLine(Long invoiceId, InvoiceLineType type, String description,
                 Integer qty, BigDecimal unitPrice, BigDecimal vatRate);

    InvoicePrintResponse getInvoiceForPrint(Long bookingId);

    InvoiceLineType mapToLineType(Object entity);
}
