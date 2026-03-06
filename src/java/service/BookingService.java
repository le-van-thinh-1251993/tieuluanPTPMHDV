package service;

import java.util.List;
import model.*;
import util.XMLUtil;

/**
 * Booking Service - Dịch vụ trung tâm của hệ thống.
 * 
 * Điều phối các dịch vụ khác để hoàn thành quy trình đặt phòng:
 * 1. Nhận yêu cầu đặt phòng
 * 2. Kiểm tra phòng trống (Room Service)
 * 3. Thực hiện thanh toán (Payment Service)
 * 4. Cập nhật trạng thái phòng (Room Service)
 * 5. Gửi thông báo (Notification Service)
 */
public class BookingService {

    /**
     * Tạo booking mới - điều phối toàn bộ flow.
     * 
     * @param request Thông tin đặt phòng từ client
     * @return BookingResponse chứa kết quả
     */
    public static BookingResponse createBooking(BookingRequest request) {
        System.out.println("[BookingService] Bắt đầu xử lý đặt phòng...");

        // Bước 1: Kiểm tra phòng trống
        Room room = RoomService.getRoomById(request.getRoomId());
        if (room == null) {
            return new BookingResponse(null, "failed", "Không tìm thấy phòng " + request.getRoomId());
        }
        if (!"available".equalsIgnoreCase(room.getStatus())) {
            return new BookingResponse(null, "failed", "Phòng " + request.getRoomId() + " không còn trống");
        }

        System.out.println("[BookingService] Phòng " + room.getId() + " (" + room.getType() + ") - Còn trống ✓");

        // Bước 2: Tạo ID booking mới
        String bookingId = XMLUtil.generateBookingId();
        double amount = (request.getPaymentInfo() != null) ? request.getPaymentInfo().getAmount() : room.getPrice();

        // Bước 3: Xử lý thanh toán
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setCardNumber(request.getPaymentInfo() != null ? request.getPaymentInfo().getCardNumber() : "N/A");

        Payment paymentResult = PaymentService.processPayment(payment);
        if (!"success".equals(paymentResult.getStatus())) {
            return new BookingResponse(bookingId, "failed", "Thanh toán thất bại");
        }

        // Bước 4: Lưu booking vào XML
        Booking booking = new Booking(
                bookingId,
                request.getCustomerName(),
                request.getEmail(),
                request.getRoomId(),
                request.getCheckInDate(),
                amount,
                "confirmed");
        XMLUtil.writeBooking(booking);
        System.out.println("[BookingService] Đã lưu booking " + bookingId + " ✓");

        // Bước 5: Cập nhật trạng thái phòng thành "booked"
        RoomService.updateRoomStatus(request.getRoomId(), "booked");
        System.out.println("[BookingService] Đã cập nhật trạng thái phòng " + request.getRoomId() + " → booked ✓");

        // Bước 6: Gửi thông báo xác nhận
        Notification notification = new Notification(
                request.getEmail(),
                "Đặt phòng thành công! Mã booking: " + bookingId
                        + ", Phòng: " + room.getType()
                        + ", Ngày check-in: " + request.getCheckInDate()
                        + ", Số tiền: " + (long) amount + " VND",
                "booking_confirmation");
        NotificationService.sendNotification(notification);

        System.out.println("[BookingService] Hoàn tất đặt phòng " + bookingId + " ✓");

        return new BookingResponse(bookingId, "confirmed", "Booking successful");
    }

    /**
     * Lấy danh sách tất cả bookings.
     */
    public static List<Booking> getAllBookings() {
        return XMLUtil.readBookings();
    }
}
