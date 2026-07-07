package com.hotel.booking.modules.pos.service;

import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.booking.enums.InvoiceLineType;
import com.hotel.booking.modules.booking.repository.BookingRepository;
import com.hotel.booking.modules.booking.service.BookingFinancialService;
import com.hotel.booking.modules.booking.service.InvoiceService;
import com.hotel.booking.modules.inventory.entity.CatalogItem;
import com.hotel.booking.modules.inventory.repository.CatalogItemRepository;
import com.hotel.booking.modules.inventory.repository.RoomInstanceRepository;
import com.hotel.booking.modules.pos.dto.request.CreateServiceOrderRequest;
import com.hotel.booking.modules.pos.entity.ServiceOrder;
import com.hotel.booking.modules.pos.entity.ServiceOrderDetail;
import com.hotel.booking.modules.pos.enums.ServiceOrderStatus;
import com.hotel.booking.modules.pos.repository.ServiceOrderDetailRepository;
import com.hotel.booking.modules.pos.repository.ServiceOrderRepository;
import com.hotel.booking.modules.pricing.entity.VatRule;
import com.hotel.booking.modules.pricing.repository.VatRuleRepository;
import com.hotel.booking.modules.pricing.service.TaxCalculatorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class POSOrderServiceImpl implements POSOrderService {
    ServiceOrderRepository orderRepository;
    ServiceOrderDetailRepository orderDetailRepository;
    CatalogItemRepository catalogItemRepository;
    BookingRepository bookingRepository;
    RoomInstanceRepository roomInstanceRepository;
    BookingFinancialService financialService;
    TaxCalculatorService taxCalculatorService;
    InvoiceService invoiceService;

    @Transactional
    public void createRoomServiceOrder(Long bookingId, CreateServiceOrderRequest request) {
        // Khởi tạo các biến chứa giá trị
        BigDecimal orderSubTotal = BigDecimal.ZERO;
        BigDecimal orderServiceFeeAmount = BigDecimal.ZERO;
        BigDecimal orderVatAmount = BigDecimal.ZERO;
        BigDecimal orderTotalAmount = BigDecimal.ZERO;

        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        BigDecimal serviceFeeRate = booking.getHotel().getServiceFeePercent();
        if (serviceFeeRate == null) serviceFeeRate = BigDecimal.ZERO;

        List<ServiceOrderDetail> details = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 1. TÍNH TOÁN CHI TIẾT TRƯỚC (Không save vội)
        for (CreateServiceOrderRequest.OrderItem reqItem : request.getItems()) {
            CatalogItem catalogItem = catalogItemRepository.findById(reqItem.getCatalogItemId())
                    .orElseThrow(() -> new RuntimeException("Món hàng không tồn tại"));

            BigDecimal unitPrice = catalogItem.getBasePrice();
            BigDecimal qty = BigDecimal.valueOf(reqItem.getQuantity());

            // A. Subtotal = Base Price x Quantity
            BigDecimal itemSubTotal = unitPrice.multiply(qty);

            // B. Service Fee = Subtotal x Rate
            BigDecimal itemServiceFee = itemSubTotal.multiply(serviceFeeRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // C. VAT = (Subtotal + Service Fee) x VAT Rate
            BigDecimal itemTaxableAmount = itemSubTotal.add(itemServiceFee);
            BigDecimal itemVatAmount = taxCalculatorService.calculateTax(
                    catalogItem.getTaxCategory().getId(),
                    itemTaxableAmount,
                    today
            );

            BigDecimal vatPercent = taxCalculatorService.getTaxRate(catalogItem.getTaxCategory().getId(), today);

            // D. Tổng tiền = Subtotal + Service Fee + VAT
            BigDecimal itemTotal = itemSubTotal.add(itemServiceFee).add(itemVatAmount);

            // Cộng dồn vào tổng order
            orderSubTotal = orderSubTotal.add(itemSubTotal);
            orderServiceFeeAmount = orderServiceFeeAmount.add(itemServiceFee);
            orderVatAmount = orderVatAmount.add(itemVatAmount);
            orderTotalAmount = orderTotalAmount.add(itemTotal);

            invoiceService.addLine(
                    booking.getInvoice().getId(),
                    InvoiceLineType.EXTRA_SERVICE, // Map vào loại Extra Service
                    catalogItem.getName() + " (x" + reqItem.getQuantity() + ")",
                    reqItem.getQuantity(),
                    unitPrice,
                    vatPercent
            );

            details.add(ServiceOrderDetail.builder()
                    .catalogItem(catalogItem)
                    .itemName(catalogItem.getName())
                    .quantity(reqItem.getQuantity())
                    .unitPrice(unitPrice)
                    .subtotal(itemSubTotal)
                    .vatRate(vatPercent)
                    .vatAmount(itemVatAmount)
                    .totalAmount(itemTotal)
                    .build());
        }

        var roomInstance = roomInstanceRepository.findById(request.getRoomInstanceId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_INSTANCE_NOT_FOUND));

        ServiceOrder newOrder = ServiceOrder.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .booking(booking)
                .roomInstance(roomInstance)
                .status(ServiceOrderStatus.COMPLETED)
                .issuedAt(OffsetDateTime.now())
                .serviceFeeRate(serviceFeeRate)
                .subTotal(orderSubTotal)
                .serviceFeeAmount(orderServiceFeeAmount)
                .vatAmount(orderVatAmount)
                .totalAmount(orderTotalAmount)
                .build();

        // Lưu với tên biến mới
        ServiceOrder savedOrder = orderRepository.save(newOrder);

        // 3. Gán Order vừa lưu vào Detail
        details.forEach(d -> d.setServiceOrder(savedOrder)); // Dùng savedOrder ở đây
        orderDetailRepository.saveAll(details);

        // 4. Đẩy số liệu sang Booking
        financialService.addAmountToBooking(bookingId, orderSubTotal, orderServiceFeeAmount, orderVatAmount, orderTotalAmount);
    }
}