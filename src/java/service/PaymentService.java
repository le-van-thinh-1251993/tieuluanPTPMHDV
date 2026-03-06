package service;

import model.Payment;

/**
 * Payment Service - Xử lý thanh toán giả lập.
 * 
 * Trong phiên bản demo, thanh toán luôn trả về thành công.
 * Trong thực tế, sẽ tích hợp với cổng thanh toán thật.
 */
public class PaymentService {

    /**
     * Xử lý thanh toán (giả lập).
     * Luôn trả về trạng thái "success".
     */
    public static Payment processPayment(Payment payment) {
        System.out.println("[PaymentService] Đang xử lý thanh toán...");
        System.out.println("  - Booking ID: " + payment.getBookingId());
        System.out.println("  - Số tiền: " + (long) payment.getAmount() + " VND");
        System.out.println("  - Số thẻ: ****" + payment.getCardNumber().substring(
                Math.max(0, payment.getCardNumber().length() - 4)));

        // Giả lập thanh toán thành công
        payment.setStatus("success");

        System.out.println("[PaymentService] Thanh toán thành công!");
        return payment;
    }
}
