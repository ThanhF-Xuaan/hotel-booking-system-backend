package com.hotel.booking.modules.booking.enums;

public enum InvoiceLineType {
    ROOM,           // Doanh thu tiền phòng thuần
    PACKAGE_ITEM,   // Gói dịch vụ trọn gói (được bán kèm từ lúc đặt phòng)
    EXTRA_SERVICE,  // Dịch vụ lẻ gọi thêm tại quầy (POS, giặt ủi...)
    SURCHARGE,      // Phụ thu (Thêm người, thêm giờ...)
    PAYMENT,
    OTHER           // Phí phát sinh thủ công
}
