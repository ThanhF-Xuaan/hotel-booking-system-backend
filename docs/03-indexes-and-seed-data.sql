-- ==============================================================================
-- 1. ĐÁNH INDEX (TỐI ƯU HIỆU NĂNG TRUY VẤN)
-- ==============================================================================
-- Đánh Index cho các khóa ngoại (Foreign Keys) thường xuyên được dùng trong lệnh JOIN
CREATE INDEX idx_staffs_hotel_id ON staffs(hotel_id);
CREATE INDEX idx_catalog_items_hotel_id ON catalog_items(hotel_id);
CREATE INDEX idx_hotel_room_types_hotel_id ON hotel_room_types(hotel_id);
CREATE INDEX idx_room_instances_hotel_room_type ON room_instances(hotel_id, hotel_room_type_id);
CREATE INDEX idx_booking_details_booking_id ON booking_details(booking_id);
CREATE INDEX idx_payments_booking_id ON payments(booking_id);

-- Đánh Index cho các cột thường xuyên được dùng trong mệnh đề WHERE (Tìm kiếm, Lọc theo khoảng thời gian)
CREATE INDEX idx_room_availability_date ON room_availability(date);
CREATE INDEX idx_room_slots_date ON room_slots(slot_date);
CREATE INDEX idx_bookings_guest_id ON bookings(guest_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_booking_details_dates ON booking_details(check_in_date, check_out_date);


-- ==============================================================================
-- 2. DỮ LIỆU MẪU (SEED DATA) ĐỂ TEST API
-- ==============================================================================

-- 2.1. Tạo 1 Khách sạn mẫu
INSERT INTO hotels (name, address, phone, check_in_time, check_out_time, service_fee_percent) 
VALUES ('Grand Viettel Hotel', '1 Trần Hữu Dực, Nam Từ Liêm, Hà Nội', '0987654321', '14:00:00', '12:00:00', 5.00);

-- 2.2. Khởi tạo Roles (Phân quyền)
INSERT INTO roles (name, code) VALUES 
('Quản trị viên', 'ADMIN'),
('Lễ tân', 'RECEPTIONIST');

-- 2.3. Khởi tạo Nhân viên (Lưu ý: Mật khẩu mặc định là '123456' đã được mã hóa chuẩn BCrypt cho Spring Security)
INSERT INTO staffs (hotel_id, role_id, username, password, first_name, last_name, full_name) VALUES 
(1, 1, 'admin', '$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.', 'Admin', 'System', 'Admin System'),
(1, 2, 'letan1', '$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.', 'Hoa', 'Nguyễn', 'Nguyễn Thị Hoa');

-- 2.4. Khởi tạo Quy tắc Thuế (VAT)
INSERT INTO vat_rules (vat_code, vat_name, vat_percent, applies_to) VALUES 
('VAT_8', 'Thuế GTGT 8%', 8.00, 'ROOM'),
('VAT_10', 'Thuế GTGT 10%', 10.00, 'OTHER');

-- 2.5. Khởi tạo Loại phòng chung (Master Data)
INSERT INTO room_types (code, name, capacity) VALUES 
('STD', 'Standard Room', 2),
('DLX', 'Deluxe Room', 2),
('SUI', 'Suite Room', 4);

-- 2.6. Khởi tạo Loại phòng cụ thể của Khách sạn (Kèm giá và số lượng)
INSERT INTO hotel_room_types (hotel_id, room_type_id, base_price, total_quantity) VALUES 
(1, 1, 1000000, 10),
(1, 2, 2000000, 5),
(1, 3, 5000000, 2);

-- 2.7. Khởi tạo Tiện ích phòng (Room Features)
INSERT INTO room_features (name, code, category, icon) VALUES 
('Wi-Fi miễn phí', 'WIFI', 'INTERNET', 'wifi-icon'),
('Tivi màn hình phẳng', 'TV', 'ENTERTAINMENT', 'tv-icon'),
('Điều hòa nhiệt độ', 'AC', 'COMFORT', 'ac-icon'),
('Ban công view thành phố', 'BALCONY_CITY', 'VIEW', 'balcony-icon');

-- 2.8. Gán Tiện ích cho Loại phòng cụ thể
-- Phòng STD có Wi-Fi và Điều hòa (ID: 1 và 3)
INSERT INTO hotel_room_type_features (hotel_room_type_id, room_feature_id) VALUES 
(1, 1), (1, 3);
-- Phòng SUI có đủ 4 tiện ích
INSERT INTO hotel_room_type_features (hotel_room_type_id, room_feature_id) VALUES 
(3, 1), (3, 2), (3, 3), (3, 4);