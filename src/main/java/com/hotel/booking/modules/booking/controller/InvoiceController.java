package com.hotel.booking.modules.booking.controller;

import com.hotel.booking.core.dto.ApiResponse;
import com.hotel.booking.modules.booking.dto.response.InvoicePrintResponse;
import com.hotel.booking.modules.booking.service.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hotel/api/v1/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceController {

    InvoiceService invoiceService;

    @GetMapping("/{bookingId}/invoice")
    public ApiResponse<InvoicePrintResponse> getInvoiceForPrint(@PathVariable Long bookingId) {
        InvoicePrintResponse response = invoiceService.getInvoiceForPrint(bookingId);

        return ApiResponse.<InvoicePrintResponse>builder()
                .message("Lấy hoá đơn thành công")
                .result(response)
                .build();
    }
}
