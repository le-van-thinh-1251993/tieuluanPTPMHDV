# 🏨 HotelBookingAPI - Hệ thống đặt phòng khách sạn RESTful

> **Tiểu luận Phát triển Phần mềm Hướng dịch vụ**  
> Xây dựng hệ thống Web Service đặt phòng khách sạn theo kiến trúc RESTful sử dụng JAX-RS trên NetBeans và GlassFish, lưu trữ dữ liệu bằng XML.

---

## 📋 Mục lục

- [Giới thiệu](#-giới-thiệu)
- [Kiến trúc hệ thống](#-kiến-trúc-hệ-thống)
- [Cấu trúc project](#-cấu-trúc-project)
- [Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [Cài đặt và chạy](#-cài-đặt-và-chạy)
- [API Endpoints](#-api-endpoints)
- [Hướng dẫn kiểm thử bằng Postman](#-hướng-dẫn-kiểm-thử-bằng-postman)
- [Thành viên nhóm](#-thành-viên-nhóm)

---

## 📖 Giới thiệu

Hệ thống cho phép thực hiện các chức năng cơ bản của một hệ thống đặt phòng khách sạn:
- Xem danh sách phòng
- Kiểm tra trạng thái phòng
- Đặt phòng
- Thanh toán
- Gửi thông báo xác nhận

Hệ thống được thiết kế theo mô hình **Composable Services**, trong đó **Booking Service** đóng vai trò dịch vụ trung tâm điều phối các dịch vụ thành phần khác.

---

## 🏗 Kiến trúc hệ thống

```
┌─────────┐     ┌──────────────────┐     ┌──────────────┐
│  Client  │────▶│  Booking Service │────▶│ Room Service │
│(Postman) │     │   (Điều phối)    │     │              │
└─────────┘     └──────────────────┘     └──────────────┘
                        │
                        ├────────────▶ Payment Service
                        │
                        └────────────▶ Notification Service
```

**Quy trình đặt phòng:**
1. Client gửi yêu cầu đặt phòng → Booking Service
2. Booking Service kiểm tra phòng trống → Room Service
3. Booking Service xử lý thanh toán → Payment Service
4. Booking Service cập nhật trạng thái phòng → Room Service
5. Booking Service gửi thông báo xác nhận → Notification Service

---

## 📁 Cấu trúc project

```
HotelBookingAPI/
├── src/java/
│   ├── RestApplication.java            # Cấu hình JAX-RS (@ApplicationPath("/api"))
│   ├── controller/                     # REST API endpoints
│   │   ├── RoomResource.java
│   │   ├── BookingResource.java
│   │   ├── PaymentResource.java
│   │   └── NotificationResource.java
│   ├── service/                        # Logic nghiệp vụ
│   │   ├── RoomService.java
│   │   ├── BookingService.java         # ★ Dịch vụ điều phối trung tâm
│   │   ├── PaymentService.java
│   │   └── NotificationService.java
│   ├── model/                          # Các đối tượng dữ liệu
│   │   ├── Room.java
│   │   ├── Booking.java
│   │   ├── BookingRequest.java
│   │   ├── BookingResponse.java
│   │   ├── Payment.java
│   │   └── Notification.java
│   ├── util/
│   │   └── XMLUtil.java                # Đọc/ghi dữ liệu XML (DOM Parser)
│   └── data/
│       ├── rooms.xml                   # Dữ liệu phòng (5 phòng mẫu)
│       └── bookings.xml                # Dữ liệu đặt phòng
├── web/
│   ├── index.html                      # Trang chủ
│   └── WEB-INF/
│       ├── web.xml
│       └── glassfish-web.xml
└── nbproject/                          # Cấu hình NetBeans
```

---

## 🔧 Công nghệ sử dụng

| Công nghệ | Mô tả |
|-----------|--------|
| **Java EE / Jakarta EE** | Nền tảng phát triển ứng dụng web |
| **JAX-RS (Jersey)** | API xây dựng RESTful Web Services |
| **NetBeans IDE** | Môi trường phát triển tích hợp |
| **GlassFish Server** | Application Server triển khai Web Service |
| **DOM Parser** | Thư viện Java đọc/ghi dữ liệu XML |
| **Postman** | Công cụ kiểm thử API |

---

## 🚀 Cài đặt và chạy

### Yêu cầu
- **NetBeans IDE** (phiên bản 8.2 trở lên hoặc NetBeans 21)
- **GlassFish Server 5** (đi kèm với NetBeans hoặc cài riêng)
- **JDK 8** trở lên

### Các bước

1. **Clone repository:**
   ```bash
   git clone https://github.com/le-van-thinh-1251993/tieuluanPTPMHDV.git
   ```

2. **Mở project trong NetBeans:**
   - File → Open Project
   - Chọn thư mục `HotelBookingAPI`

3. **Cấu hình Server:**
   - Right-click project → Properties → Run
   - Chọn **GlassFish Server** làm Server

4. **Chạy project:**
   - Right-click project → **Run** (hoặc nhấn F6)
   - NetBeans sẽ tự động deploy lên GlassFish

5. **Truy cập:**
   - Trang chủ: `http://localhost:8080/HotelBookingAPI/`
   - API base URL: `http://localhost:8080/HotelBookingAPI/api/`

---

## 📡 API Endpoints

| Method | URL | Mô tả |
|--------|-----|--------|
| `GET` | `/api/rooms` | Lấy danh sách tất cả phòng |
| `GET` | `/api/rooms/{id}` | Xem chi tiết 1 phòng |
| `GET` | `/api/rooms/type/{type}` | Lọc phòng theo loại |
| `PUT` | `/api/rooms/{id}/status` | Cập nhật trạng thái phòng |
| `POST` | `/api/bookings` | Tạo đặt phòng mới |
| `GET` | `/api/bookings` | Xem danh sách booking |
| `POST` | `/api/payments` | Xử lý thanh toán |
| `POST` | `/api/notifications` | Gửi thông báo |

---

## 🧪 Hướng dẫn kiểm thử bằng Postman

### Chuẩn bị
1. Tải và cài đặt [Postman](https://www.postman.com/downloads/)
2. Đảm bảo project đã được deploy lên GlassFish và đang chạy tại `http://localhost:8080`

> **Lưu ý:** Tất cả URL bên dưới đều sử dụng base URL: `http://localhost:8080/HotelBookingAPI`

---

### Test 1: Lấy danh sách tất cả phòng

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/rooms` |
| **Headers** | Không cần |
| **Body** | Không cần |

**Các bước thực hiện:**
1. Mở Postman, tạo request mới
2. Chọn method **GET**
3. Nhập URL: `http://localhost:8080/HotelBookingAPI/api/rooms`
4. Nhấn **Send**

**Kết quả mong đợi (Status: 200 OK):**
```json
[
  {
    "id": "R01",
    "type": "Deluxe",
    "price": 1200000,
    "status": "available"
  },
  {
    "id": "R02",
    "type": "Standard",
    "price": 800000,
    "status": "available"
  },
  {
    "id": "R03",
    "type": "Suite",
    "price": 2500000,
    "status": "available"
  },
  {
    "id": "R04",
    "type": "VIP",
    "price": 3000000,
    "status": "available"
  },
  {
    "id": "R05",
    "type": "Family",
    "price": 1500000,
    "status": "available"
  }
]
```

---

### Test 2: Xem chi tiết 1 phòng

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/rooms/R01` |

**Các bước thực hiện:**
1. Chọn method **GET**
2. Nhập URL: `http://localhost:8080/HotelBookingAPI/api/rooms/R01`
3. Nhấn **Send**

**Kết quả mong đợi (Status: 200 OK):**
```json
{
  "id": "R01",
  "type": "Deluxe",
  "price": 1200000,
  "status": "available"
}
```

---

### Test 3: Lọc phòng theo loại

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/rooms/type/Deluxe` |

**Các bước thực hiện:**
1. Chọn method **GET**
2. Nhập URL: `http://localhost:8080/HotelBookingAPI/api/rooms/type/Deluxe`
3. Nhấn **Send**

**Kết quả mong đợi (Status: 200 OK):**
```json
[
  {
    "id": "R01",
    "type": "Deluxe",
    "price": 1200000,
    "status": "available"
  }
]
```

> **Thử thêm:** Thay `Deluxe` bằng `Standard`, `Suite`, `VIP`, hoặc `Family` để lọc phòng theo loại khác.

---

### Test 4: Đặt phòng (⭐ Test chính)

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `POST` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/bookings` |
| **Headers** | `Content-Type: application/json` |

**Các bước thực hiện:**
1. Chọn method **POST**
2. Nhập URL: `http://localhost:8080/HotelBookingAPI/api/bookings`
3. Chọn tab **Headers**, thêm:
   - Key: `Content-Type`
   - Value: `application/json`
4. Chọn tab **Body** → chọn **raw** → chọn **JSON**
5. Nhập nội dung Body:

```json
{
  "customerName": "Nguyen Van A",
  "email": "vana@gmail.com",
  "roomId": "R01",
  "checkInDate": "2026-03-10",
  "paymentInfo": {
    "cardNumber": "12345678",
    "amount": 1200000
  }
}
```

6. Nhấn **Send**

**Kết quả mong đợi (Status: 201 Created):**
```json
{
  "bookingId": "BK1002",
  "status": "confirmed",
  "message": "Booking successful"
}
```

> **Lưu ý:** `bookingId` sẽ tự động tăng (BK1002, BK1003, ...) mỗi lần đặt phòng mới.

---

### Test 5: Kiểm tra trạng thái phòng sau khi đặt

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/rooms/R01` |

**Các bước thực hiện:**
1. Sau khi Test 4 thành công, gửi lại request GET phòng R01
2. Chọn method **GET**
3. Nhập URL: `http://localhost:8080/HotelBookingAPI/api/rooms/R01`
4. Nhấn **Send**

**Kết quả mong đợi (Status: 200 OK):**
```json
{
  "id": "R01",
  "type": "Deluxe",
  "price": 1200000,
  "status": "booked"
}
```

> ✅ Trạng thái phòng R01 đã chuyển từ `"available"` → `"booked"`.

---

### Test 6: Đặt phòng đã được đặt (test lỗi)

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `POST` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/bookings` |
| **Body** | Giống Test 4 (cùng roomId R01) |

**Kết quả mong đợi (Status: 400 Bad Request):**
```json
{
  "bookingId": null,
  "status": "failed",
  "message": "Phòng R01 không còn trống"
}
```

> ✅ Hệ thống từ chối đặt phòng đã được book.

---

### Test 7: Xem danh sách tất cả booking

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/bookings` |

**Các bước thực hiện:**
1. Chọn method **GET**
2. Nhập URL: `http://localhost:8080/HotelBookingAPI/api/bookings`
3. Nhấn **Send**

**Kết quả mong đợi (Status: 200 OK):**
```json
[
  {
    "id": "BK1001",
    "customerName": "Nguyen Van A",
    "email": "vana@gmail.com",
    "roomId": "R01",
    "checkInDate": "2026-03-10",
    "amount": 1200000,
    "status": "confirmed"
  },
  {
    "id": "BK1002",
    "customerName": "Nguyen Van A",
    "email": "vana@gmail.com",
    "roomId": "R01",
    "checkInDate": "2026-03-10",
    "amount": 1200000,
    "status": "confirmed"
  }
]
```

---

### Test 8: Thanh toán riêng lẻ

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `POST` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/payments` |
| **Headers** | `Content-Type: application/json` |

**Body:**
```json
{
  "bookingId": "BK1001",
  "amount": 1200000,
  "cardNumber": "12345678"
}
```

**Kết quả mong đợi (Status: 200 OK):**
```json
{
  "bookingId": "BK1001",
  "amount": 1200000,
  "cardNumber": "12345678",
  "status": "success"
}
```

---

### Test 9: Gửi thông báo riêng lẻ

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `POST` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/notifications` |
| **Headers** | `Content-Type: application/json` |

**Body:**
```json
{
  "email": "vana@gmail.com",
  "message": "Đặt phòng thành công! Mã booking: BK1001",
  "type": "booking_confirmation"
}
```

**Kết quả mong đợi (Status: 200 OK):**
```json
{
  "message": "Thông báo đã được gửi thành công"
}
```

> **Lưu ý:** Thông báo sẽ được in ra console (log) của GlassFish Server.

---

### Test 10: Cập nhật trạng thái phòng

| Thông tin | Giá trị |
|-----------|---------|
| **Method** | `PUT` |
| **URL** | `http://localhost:8080/HotelBookingAPI/api/rooms/R01/status` |
| **Headers** | `Content-Type: text/plain` |

**Body (raw - Text):**
```
available
```

**Các bước thực hiện:**
1. Chọn method **PUT**
2. Nhập URL: `http://localhost:8080/HotelBookingAPI/api/rooms/R01/status`
3. Chọn tab **Headers**, thêm:
   - Key: `Content-Type`
   - Value: `text/plain`
4. Chọn tab **Body** → chọn **raw** → chọn **Text**
5. Nhập: `available`
6. Nhấn **Send**

**Kết quả mong đợi (Status: 200 OK):**
```json
{
  "message": "Đã cập nhật trạng thái phòng R01 thành available"
}
```

---

### Tóm tắt kịch bản test

| # | Test | Method | Kết quả mong đợi |
|---|------|--------|-------------------|
| 1 | Lấy danh sách phòng | `GET /api/rooms` | 200 - 5 phòng |
| 2 | Xem chi tiết phòng | `GET /api/rooms/R01` | 200 - Thông tin R01 |
| 3 | Lọc phòng theo loại | `GET /api/rooms/type/Deluxe` | 200 - Phòng Deluxe |
| 4 | **Đặt phòng** | `POST /api/bookings` | **201 - Booking confirmed** |
| 5 | Kiểm tra phòng sau đặt | `GET /api/rooms/R01` | 200 - status: "booked" |
| 6 | Đặt phòng đã book | `POST /api/bookings` | 400 - Phòng không trống |
| 7 | Danh sách booking | `GET /api/bookings` | 200 - Danh sách bookings |
| 8 | Thanh toán | `POST /api/payments` | 200 - status: "success" |
| 9 | Gửi thông báo | `POST /api/notifications` | 200 - Gửi thành công |
| 10 | Cập nhật trạng thái phòng | `PUT /api/rooms/R01/status` | 200 - Đã cập nhật |

---

## 👥 Thành viên nhóm

| STT | Thành viên | Mã số | Lớp | Nhiệm vụ |
|-----|-----------|-------|-----|----------|
| 1 | Lê Văn Thịnh (Trưởng nhóm) | B24DTCN494 | D24TXCN11-B | Thiết kế kiến trúc hệ thống |
| 2 | Nguyễn Thị Vinh | K23DTCN471 | D23TXCN05-K | Room Service |
| 3 | Nguyễn Thị Ngọc Anh | K24DTCN110 | D24TXCN04-K | Booking Service |
| 4 | Nguyễn Thị Hoan | K23DTCN452 | D23TXCN05-K | Payment & Notification Service |
| 5 | Nguyễn Thị Tuyết | K23DTCN468 | D23TXCN05-K | Testing & Documentation |

---

## 📄 License

Project này được thực hiện phục vụ mục đích học tập - Tiểu luận môn **Phát triển Phần mềm Hướng dịch vụ**.
