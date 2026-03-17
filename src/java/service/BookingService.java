package service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import model.Booking;
import model.BookingRequest;
import model.BookingResponse;
import model.Notification;
import model.Payment;
import model.Room;
import util.XMLUtil;

/**
 * Booking Service - điều phối luồng đặt phòng.
 */
public class BookingService {

    /**
     * Tạo booking mới - điều phối toàn bộ flow.
     *
     * @param request Thông tin đặt phòng từ client
     * @return BookingResponse chứa kết quả
     */
    public static BookingResponse createBooking(BookingRequest request) {
        if (request == null) {
            return new BookingResponse(null, "failed", "Request body rỗng");
        }

        LocalDate checkIn;
        LocalDate checkOut;
        try {
            checkIn = LocalDate.parse(request.getCheckInDate());
            if (request.getCheckOutDate() != null && !request.getCheckOutDate().isEmpty()) {
                checkOut = LocalDate.parse(request.getCheckOutDate());
            } else {
                int nightsReq = request.getNights() > 0 ? request.getNights() : 1;
                checkOut = checkIn.plusDays(nightsReq);
            }
        } catch (DateTimeParseException ex) {
            return new BookingResponse(null, "failed", "Ngày check-in/check-out không đúng định dạng yyyy-MM-dd");
        }

        if (!checkOut.isAfter(checkIn)) {
            return new BookingResponse(null, "failed", "Ngày check-out phải sau check-in");
        }

        int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        System.out.println("[BookingService] Bắt đầu xử lý đặt phòng...");

        // Bước 1: Kiểm tra phòng tồn tại
        Room room = RoomService.getRoomById(request.getRoomId());
        if (room == null) {
            return new BookingResponse(null, "failed", "Không tìm thấy phòng " + request.getRoomId());
        }

        // Bước 1b: Kiểm tra trùng ngày (overlap)
        List<Booking> existingBookings = XMLUtil.readBookings();
        for (Booking existing : existingBookings) {
            if (existing.getRoomId().equals(request.getRoomId()) && "confirmed".equalsIgnoreCase(existing.getStatus())) {
                LocalDate exIn = parseDateSafe(existing.getCheckInDate(), checkIn);
                LocalDate exOut = parseDateSafe(existing.getCheckOutDate(), exIn.plusDays(existing.getNights()));
                if (isOverlap(checkIn, checkOut, exIn, exOut)) {
                    return new BookingResponse(null, "failed",
                            "Phòng " + request.getRoomId() + " đã được đặt từ " + exIn + " đến " + exOut);
                }
            }
        }

        System.out.println("[BookingService] Phòng " + room.getId() + " (" + room.getType() + ") - Còn trống ✓");

        // Bước 2: Tạo ID booking mới
        String bookingId = XMLUtil.generateBookingId();

        // Bước 3: Tính và kiểm tra số tiền + thông tin thanh toán
        if (request.getPaymentInfo() == null) {
            return new BookingResponse(bookingId, "failed", "Thiếu thông tin thanh toán");
        }
        double expectedAmount = room.getPrice() * nights;
        double requestedAmount = request.getPaymentInfo().getAmount();
        if (request.getPaymentInfo().getCardNumber() == null
                || request.getPaymentInfo().getCardNumber().trim().length() < 4) {
            return new BookingResponse(bookingId, "failed", "Số thẻ phải tối thiểu 4 ký tự");
        }
        if (request.getPaymentInfo().getBankName() == null || request.getPaymentInfo().getBankName().trim().isEmpty()) {
            return new BookingResponse(bookingId, "failed", "Thiếu tên ngân hàng");
        }
        if (requestedAmount <= 0) {
            return new BookingResponse(bookingId, "failed", "Số tiền thanh toán phải > 0");
        }
        if (Math.abs(expectedAmount - requestedAmount) > 0.0001) {
            return new BookingResponse(bookingId, "failed",
                    "Số tiền phải bằng giá phòng x số đêm (" + (long) expectedAmount + " VND)");
        }

        // Bước 4: Xử lý thanh toán
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(expectedAmount);
        payment.setCardNumber(request.getPaymentInfo().getCardNumber());
        payment.setBankName(request.getPaymentInfo().getBankName());

        Payment paymentResult = PaymentService.processPayment(payment);
        if (!"success".equals(paymentResult.getStatus())) {
            return new BookingResponse(bookingId, "failed", "Thanh toán thất bại");
        }

        // Bước 5: Lưu booking vào XML kèm trạng thái thanh toán
        Booking booking = new Booking(
                bookingId,
                request.getCustomerName(),
                request.getEmail(),
                request.getRoomId(),
                checkIn.toString(),
                checkOut.toString(),
                nights,
                expectedAmount,
                "confirmed",
                paymentResult.getStatus(),
                paymentResult.getAmount(),
                paymentResult.getBankName());
        XMLUtil.writeBooking(booking);
        System.out.println("[BookingService] Đã lưu booking " + bookingId + " ✓");

        // Bước 6: Không cập nhật trạng thái phòng thành "booked" nữa để giữ trạng thái gốc là "available",
        // việc kiểm tra trống phòng sẽ dựa trên ngày trong bookings.xml
        
        // Bước 7: Gửi thông báo xác nhận
        Notification notification = new Notification(
                request.getEmail(),
                "Đặt phòng thành công! Mã booking: " + bookingId
                        + ", Phòng: " + room.getType()
                        + ", Check-in: " + checkIn
                        + ", Check-out: " + checkOut
                        + ", Số tiền: " + (long) expectedAmount + " VND"
                        + ", Thanh toán: " + paymentResult.getStatus(),
                "booking_confirmation");
        NotificationService.sendNotification(notification);

        System.out.println("[BookingService] Hoàn tất đặt phòng " + bookingId + " ✓");

        return new BookingResponse(bookingId, "confirmed", "Booking successful");
    }

    /**
     * Lấy danh sách tất cả bookings theo các bộ lọc, dùng cho chức năng Check-in.
     */
    public static List<Booking> getAllBookings(String checkInDate, String checkOutDate, String customerName, String roomId, String status) {
        List<Booking> all = XMLUtil.readBookings();
        return all.stream()
                .filter(b -> checkInDate == null || checkInDate.trim().isEmpty() || b.getCheckInDate().equals(checkInDate))
                .filter(b -> checkOutDate == null || checkOutDate.trim().isEmpty() || b.getCheckOutDate().equals(checkOutDate))
                .filter(b -> customerName == null || customerName.trim().isEmpty() || b.getCustomerName().toLowerCase().contains(customerName.trim().toLowerCase()))
                .filter(b -> roomId == null || roomId.trim().isEmpty() || b.getRoomId().equalsIgnoreCase(roomId.trim()))
                .filter(b -> status == null || status.trim().isEmpty() || b.getStatus().equalsIgnoreCase(status.trim()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Lấy chi tiết 1 booking.
     */
    public static Booking getBookingById(String id) {
        return XMLUtil.readBookings().stream()
                .filter(b -> b.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    private static boolean isOverlap(LocalDate newIn, LocalDate newOut, LocalDate exIn, LocalDate exOut) {
        // Overlap nếu newIn < exOut và newOut > exIn
        return newIn.isBefore(exOut) && newOut.isAfter(exIn);
    }

    private static LocalDate parseDateSafe(String dateStr, LocalDate fallback) {
        try {
            if (dateStr != null && !dateStr.isEmpty()) {
                return LocalDate.parse(dateStr);
            }
        } catch (DateTimeParseException ignored) {
        }
        return fallback;
    }
}
