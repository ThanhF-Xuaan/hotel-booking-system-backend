# 🏨 Hotel Booking - Core Backend & API

[![Java Version](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot Version](https://img.shields.io/badge/Spring%20Boot-3.5.15-brightgreen?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red?logo=redis&logoColor=white)](https://redis.io/)
[![Build Tool](https://img.shields.io/badge/Build-Maven-blue?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Hệ thống lõi quản lý đặt phòng khách sạn (**Hotel Booking System - Core Backend & API**). Dự án này chịu trách nhiệm xử lý toàn bộ vòng đời của giao dịch đặt phòng, điều phối tồn kho hạng phòng thực tế theo thời gian thực, quản lý và áp dụng chính sách giá động, đồng thời kiểm soát tính nhất quán dữ liệu tối ưu dưới tải cao. Thiết kế hệ thống được xây dựng theo mô hình **Modular Monolith** nhằm đảm bảo tính cô lập nghiệp vụ, dễ bảo trì và sẵn sàng tách thành các Microservices độc lập khi quy mô mở rộng.

---

## ⚡ Kiến trúc & Tính năng nổi bật

### 🧱 1. Kiến trúc Modular Monolith (Đơn khối mô-đun)
*   Phân tách cấu trúc mã nguồn thành các module nghiệp vụ độc lập nằm dưới gói `modules` bao gồm: `booking`, `inventory`, `pricing`, `search`, `iam`, `crm`, `operation`, `pos`, và `report`. Gói `core` đóng vai trò là lõi hệ thống chứa cấu hình dùng chung và các xử lý hạ tầng.
*   Mỗi module quản lý một ranh giới nghiệp vụ riêng biệt (**Bounded Context**), giao tiếp qua các Service API được định nghĩa chặt chẽ. Cấu trúc này giúp triệt tiêu sự phụ thuộc chéo lộn xộn, dễ bảo trì mã nguồn và kiểm thử độc lập.

### 🔒 2. Cơ chế kiểm soát đồng thời Hybrid Lock (Optimistic/Pessimistic)
Hệ thống kết hợp linh hoạt hai cơ chế khóa để giải quyết triệt để bài toán Race Condition và Overbooking (đặt trùng phòng) khi nhiều khách hàng cùng thực hiện thao tác tại một thời điểm:
*   **Optimistic Locking (Khóa lạc quan)**: Sử dụng thuộc tính `@Version` trên các thực thể JPA (như `RoomAvailability` đại diện tồn kho hạng phòng theo ngày) để phát hiện và ngăn chặn các giao dịch sửa đổi xung đột mà không cần khóa cứng bảng ghi ở tầng DB, giúp nâng cao thông lượng của hệ thống trong điều kiện tải bình thường.
*   **Pessimistic Locking (Khóa bi quan)**: Sử dụng `@Lock(LockModeType.PESSIMISTIC_WRITE)` trong `RoomInstanceRepository` và `RoomSlotRepository` kết hợp với cấu hình thời gian chờ khóa (`lock timeout` ở mức 3000ms - 5000ms) để ngăn ngừa tuyệt đối tình trạng deadlock. Khi khách hàng xác nhận thanh toán đặt phòng, các hàng chi tiết phòng (`RoomSlot`) từng đêm tương ứng sẽ bị khóa cứng trên Database cho tới khi giao dịch hoàn tất, đảm bảo phòng chỉ được bán cho duy nhất một người.

### ⏱️ 3. Nghiệp vụ giữ phòng tạm thời (Logical Hold) với Redis TTL (10 phút)
*   Khi khách hàng bắt đầu luồng thanh toán (`BookingInitiationServiceImpl`), hệ thống sẽ tạo một phiên đặt phòng ảo bằng cách tăng số lượng phòng bị giữ tạm thời (`locked_rooms`) trên cơ sở dữ liệu PostgreSQL kèm thời gian hết hạn (`locked_until`).
*   Đồng thời, thông tin chi tiết của phiên đặt phòng được lưu tạm vào Redis dưới dạng JSON qua 2 key:
    *   `payment:data:{sessionId}` chứa dữ liệu giỏ hàng nháp (TTL 15 phút).
    *   `payment:expire:{sessionId}` đóng vai trò là trigger hết hạn (TTL 10 phút).
*   Một Worker chạy nền là `SessionExpirationWorker` kế thừa `KeyExpirationEventMessageListener` của Spring Data Redis lắng nghe sự kiện hết hạn key. Khi key hết hạn (sau 10 phút mà khách hàng chưa thanh toán), Worker sẽ tự động giải phóng (release) số phòng bị khóa ảo trên Postgres về trạng thái ban đầu, dọn dẹp bộ nhớ đệm và mở lại cơ hội đặt phòng cho các khách hàng khác.

### 📈 4. Thuật toán tính giá động (Pricing Engine) tối ưu hiệu năng $O(N)$
*   Quá trình tính toán chi tiết giá phòng được thực hiện thông qua một đường ống xử lý (`PriceProcessor` chain) trong `PriceAggregationServiceImpl` (Pipeline Pattern) tính toán tuần tự cho từng đêm nghỉ:
    1.  **Thiết lập giá gốc**: Lấy giá cơ sở của hạng phòng (`base_price`).
    2.  **Điều chỉnh theo lịch**: Sử dụng `PricingEngineService` để phân tích các quy tắc giá lễ tết, cuối tuần hoặc sự kiện đặc biệt hoạt động trong ngày và chọn ra quy tắc thắng cuộc có mức điều chỉnh/mức ưu tiên tốt nhất.
    3.  **Áp dụng giảm giá**: Tính toán chiết khấu (Discounts) thỏa mãn các điều kiện về số đêm tối thiểu, số ngày đặt trước hoặc mã khuyến mại.
    4.  **Tính toán phụ thu**: Phụ thu thêm người lớn và trẻ em dựa trên cấu hình chính sách độ tuổi (`Age Policy`) vượt quá số lượng tiêu chuẩn của phòng.
    5.  **Tích hợp dịch vụ**: Cộng dồn chi phí dịch vụ đi kèm (Add-ons / Package Items) bắt buộc hoặc tự chọn.
    6.  **Tính phí dịch vụ**: Áp dụng phần trăm phí dịch vụ của khách sạn (`service_fee_percent`).
    7.  **Tính thuế**: Sử dụng `TaxCalculatorService` để tính toán thuế VAT dựa trên danh mục thuế phù hợp cho từng mặt hàng và phòng nghỉ.
*   Thuật toán duyệt qua toàn bộ số ngày nghỉ trong một lượt xử lý duy nhất giúp độ phức tạp đạt mức tối ưu $O(N)$ (với $N$ là số đêm nghỉ), giảm thiểu số lần truy vấn cơ sở dữ liệu và đảm bảo tốc độ phản hồi cực nhanh khi tìm kiếm phòng.

---

## 🛠️ Công nghệ sử dụng (Tech Stack)

*   **Ngôn ngữ**: Java 21 (OpenJDK 21)
*   **Framework chính**: Spring Boot 3.5.15
    *   *Spring Web* (REST APIs)
    *   *Spring Data JPA* (ORM & Data Access)
    *   *Spring Security & OAuth2 Resource Server* (Bảo mật & Quản lý Token)
*   **Cơ sở dữ liệu**: PostgreSQL 18
*   **Bộ nhớ đệm & Phiên làm việc**: Redis 7-alpine (Spring Data Redis)
*   **Tài liệu API**: Springdoc OpenAPI v3 (Swagger UI 2.8.8)
*   **Thư viện tiện ích**: MapStruct 1.5.5, Lombok 1.18.30, Jackson ObjectMapper
*   **Công cụ xây dựng**: Maven 3.9

---

## 🚀 Hướng dẫn chạy dự án (Getting Started)

### 📋 Yêu cầu chuẩn bị
*   Đã cài đặt **JDK 21** trở lên.
*   Đã cài đặt **Docker** và **Docker Compose**.
*   Công cụ quản trị database như **DBeaver** hoặc **pgAdmin** (tùy chọn).

### 🐳 Bước 1: Khởi động cơ sở hạ tầng qua Docker Compose
Hạ tầng cơ sở dữ liệu và cache được định nghĩa tập trung ở file `docker-compose.yml` tại thư mục gốc của dự án. Di chuyển ra thư mục gốc và chạy lệnh:
```bash
docker compose up -d postgres-db redis-cache
```

> [!IMPORTANT]
> Redis container được thiết lập chạy kèm lệnh `--notify-keyspace-events Ex` nhằm kích hoạt cơ chế phát sự kiện khi key hết hạn. Đây là cấu hình bắt buộc để tính năng giải phóng phòng tự động hoạt động.

### 💻 Bước 2: Khởi chạy ứng dụng Backend ở môi trường Local
Di chuyển vào thư mục `/backend` của dự án. Hệ thống đã tích hợp sẵn Maven Wrapper giúp bạn chạy trực tiếp mà không cần cài đặt Maven toàn cục.

*   **Trên hệ điều hành Windows (PowerShell/CMD)**:
    ```powershell
    .\mvnw spring-boot:run
    ```
*   **Trên hệ điều hành Linux / MacOS**:
    ```bash
    chmod +x mvnw
    ./mvnw spring-boot:run
    ```

Ứng dụng sẽ khởi chạy tại cổng mặc định `8080` với context path là `/hotel`.

---

## ⚙️ Cấu hình & Biến môi trường

Ứng dụng đọc cấu hình từ file `application.yml`, `application-dev.yml` (local) và `application-prod.yml` (production). Bạn có thể cấu hình các thông số này thông qua các biến môi trường dưới đây:

| Biến môi trường | Giá trị mặc định | Mô tả |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/hotel_db` | URL kết nối PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `root` | Tài khoản đăng nhập database |
| `SPRING_DATASOURCE_PASSWORD` | `123456` | Mật khẩu đăng nhập database |
| `SPRING_DATA_REDIS_HOST` | `localhost` | Địa chỉ host của Redis Server |
| `SPRING_DATA_REDIS_PORT` | `6379` | Cổng kết nối Redis |
| `JWT_SECRET` | `8e18c9e3f2cab32b039df13a3...dd62e5a` | Khóa ký số bảo mật cho token (HS256) |
| `JWT_EXPIRATION_MS` | `86400000` | Thời hạn sử dụng của JWT Token (24 giờ) |
| `UPLOAD_DIR` | `./uploads` | Đường dẫn lưu trữ tệp hình ảnh tải lên |

---

## 📂 Cấu trúc thư mục (Project Structure)

Dưới đây là sơ đồ cây thư mục của gói ứng dụng chính `com.hotel.booking`:

```text
src/main/java/com/hotel/booking/
├── core/                        # Nhân lõi và cấu hình dùng chung toàn hệ thống
│   ├── config/                  # Các lớp cấu hình Spring (Security, Redis, Swagger, Web)
│   ├── dto/                     # Data Transfer Objects dùng chung
│   ├── entity/                  # Thực thể JPA dùng chung toàn cục
│   ├── enums/                   # Các tập hợp hằng số dùng chung (ActiveStatus, v.v.)
│   ├── exception/               # Bộ quản lý ngoại lệ tập trung (Global Exception Handler & ErrorCode)
│   ├── security/                # Cấu hình chi tiết xác thực và giải mã JWT token
│   └── utils/                   # Các class tiện ích hỗ trợ tính toán, xử lý chuỗi
└── modules/                     # Các nghiệp vụ được module hóa (Modular Monolith)
    ├── booking/                 # Nghiệp vụ khởi tạo đặt phòng, thanh toán, giải phóng phòng hết hạn
    ├── crm/                     # Quản lý hồ sơ và thông tin tương tác của khách hàng
    ├── iam/                     # Quản lý định danh (Tài khoản, Vai trò, Phân quyền RBAC)
    ├── inventory/               # Quản lý phòng thực tế, hạng phòng và số lượng tồn kho từng đêm
    ├── operation/               # Nghiệp vụ vận hành khách sạn (Check-in, Check-out, Lễ tân, Bảo trì phòng)
    ├── pos/                     # Điểm bán lẻ, tích hợp thanh toán các dịch vụ gia tăng tại khách sạn
    ├── pricing/                 # Cấu hình chính sách giá động, giá theo ngày lễ, giảm giá và phụ thu
    ├── report/                  # Tạo các báo cáo doanh thu, công suất phòng cho quản trị viên
    └── search/                  # Engine tìm kiếm phòng trống và tổng hợp bảng phân rã chi tiết giá phòng
```

---

## 📖 Tài liệu API (API Docs)

Hệ thống cung cấp sẵn tài liệu trực quan và công cụ thử nghiệm API tích hợp qua OpenAPI:

*   **Đường dẫn giao diện Swagger UI**: [http://localhost:8080/hotel/swagger-ui.html](http://localhost:8080/hotel/swagger-ui.html)
*   **Đường dẫn API Docs JSON Spec**: [http://localhost:8080/hotel/v3/api-docs](http://localhost:8080/hotel/v3/api-docs)

> [!TIP]
> Bạn có thể copy đường dẫn của **API Docs JSON Spec** ở trên và chọn tính năng **Import** vào công cụ **Postman** để tự động khởi tạo toàn bộ bộ sưu tập các API (Postman Collection) của hệ thống chỉ trong vài giây.
