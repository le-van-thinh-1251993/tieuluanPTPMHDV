# 🏨 HotelBookingAPI - Hệ thống đặt phòng khách sạn RESTful

> **Tiểu luận Phát triển Phần mềm Hướng dịch vụ**  
> Xây dựng hệ thống Web Service đặt phòng khách sạn theo kiến trúc RESTful sử dụng JAX-RS trên NetBeans và GlassFish, lưu trữ dữ liệu bằng XML.

---

## 📋 Mục lục

- [Giới thiệu](#-giới-thiệu)
- [Kiến trúc hệ thống](#-kiến-trúc-hệ-thống)
- [Luồng API](#-luồng-api)
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
┌─────────────┐     ┌──────────────────┐     ┌──────────────┐
│   Client     │────▶│  Booking Service │────▶│ Room Service │
│(UI/Postman)  │     │   (Điều phối)    │     │              │
└─────────────┘     └──────────────────┘     └──────────────┘
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

## � Luồng API

### Xem phòng
```
Client → GET /api/rooms → RoomResource → RoomService → XMLUtil (đọc rooms.xml) → JSON
```

### Đặt phòng (luồng chính — Service Composition)
```
Client → POST /api/bookings → BookingResource → BookingService điều phối:
   1️⃣ RoomService.getRoomById()     → Kiểm tra phòng còn trống
   2️⃣ PaymentService.process()      → Xử lý thanh toán → "success"
   3️⃣ RoomService.updateStatus()    → Cập nhật phòng → "booked" (ghi rooms.xml)
   4️⃣ XMLUtil.addBooking()          → Lưu booking vào bookings.xml
   5️⃣ NotificationService.send()    → Gửi thông báo (in console)
   → Trả BookingResponse (201 Created)
```

### Xem bookings
```
Client → GET /api/bookings → BookingResource → XMLUtil (đọc bookings.xml) → JSON
```

> **BookingService** là dịch vụ **trung tâm** — nhận request → gọi lần lượt các service con → trả kết quả. Đây chính là mô hình **Service Composition** trong SOA.

---

## �📁 Cấu trúc project

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
│   ├── index.html                      # Giao diện Web UI
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
   - File → Open Project → chọn thư mục `HotelBookingAPI`

3. **Cấu hình Server:**
   - Right-click project → Properties → Run → chọn **GlassFish Server**

4. **Chạy project:**
   - Right-click project → **Run** (hoặc nhấn **F6**)

5. **Truy cập:**
   - Giao diện Web: `http://localhost:8080/HotelBookingAPI/`
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
1. Tải [Postman](https://www.postman.com/downloads/) và đảm bảo project đang chạy tại `http://localhost:8080`
2. Base URL: `http://localhost:8080/HotelBookingAPI`

---

### Tổng quan các test case

| # | Test | Method | URL | Kết quả mong đợi |
|---|------|--------|-----|-------------------|
| 1 | Danh sách phòng | `GET` | `/api/rooms` | 200 - 5 phòng |
| 2 | Chi tiết phòng | `GET` | `/api/rooms/R01` | 200 - Thông tin R01 |
| 3 | Lọc theo loại | `GET` | `/api/rooms/type/Deluxe` | 200 - Phòng Deluxe |
| **4** | **⭐ Đặt phòng** | **`POST`** | **`/api/bookings`** | **201 - confirmed** |
| 5 | Phòng sau khi đặt | `GET` | `/api/rooms/R01` | 200 - status: `booked` |
| 6 | Đặt phòng đã book | `POST` | `/api/bookings` | 400 - Phòng không trống |
| 7 | Danh sách booking | `GET` | `/api/bookings` | 200 - Danh sách bookings |
| 8 | Thanh toán | `POST` | `/api/payments` | 200 - status: `success` |
| 9 | Gửi thông báo | `POST` | `/api/notifications` | 200 - Gửi thành công |
| 10 | Cập nhật trạng thái | `PUT` | `/api/rooms/R01/status` | 200 - Đã cập nhật |

---

### ⭐ Test đặt phòng (Test chính)

**`POST`** `http://localhost:8080/HotelBookingAPI/api/bookings`

**Headers:** `Content-Type: application/json`

**Body:**
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

**Response (201 Created):**
```json
{
  "bookingId": "BK1006",
  "status": "confirmed",
  "message": "Booking successful"
}
```

> Sau khi đặt, gửi `GET /api/rooms/R01` sẽ thấy `status` chuyển thành `"booked"`. Đặt lại cùng phòng sẽ nhận `400 Bad Request`.

---

### Test thanh toán riêng lẻ

**`POST`** `http://localhost:8080/HotelBookingAPI/api/payments` | Headers: `Content-Type: application/json`

```json
{ "bookingId": "BK1001", "amount": 1200000, "cardNumber": "12345678" }
```
→ Response: `{ "status": "success", ... }`

---

### Test gửi thông báo

**`POST`** `http://localhost:8080/HotelBookingAPI/api/notifications` | Headers: `Content-Type: application/json`

```json
{ "email": "vana@gmail.com", "message": "Đặt phòng thành công!", "type": "booking_confirmation" }
```
→ Response: `{ "message": "Thông báo đã được gửi thành công" }`

---

### Test cập nhật trạng thái phòng

**`PUT`** `http://localhost:8080/HotelBookingAPI/api/rooms/R01/status` | Headers: `Content-Type: text/plain` | Body: `available`

→ Response: `{ "message": "Đã cập nhật trạng thái phòng R01 thành available" }`

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
