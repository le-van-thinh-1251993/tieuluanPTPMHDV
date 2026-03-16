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
        if (payment == null) {
            Payment p = new Payment();
            p.setStatus("failed");
            return p;
        }
        if (payment.getAmount() <= 0 || payment.getCardNumber() == null || payment.getCardNumber().length() < 4) {
            payment.setStatus("failed");
            return payment;
        }

        System.out.println("[PaymentService] Đang xử lý thanh toán...");
        System.out.println("  - Booking ID: " + payment.getBookingId());
        System.out.println("  - Số tiền: " + (long) payment.getAmount() + " VND");
        String card = payment.getCardNumber();
        String last4 = card.substring(card.length() - 4);
        System.out.println("  - Số thẻ: ****" + last4);
        if (payment.getBankName() != null) {
            System.out.println("  - Ngân hàng: " + payment.getBankName());
        }

        // Giả lập thanh toán thành công
        payment.setStatus("success");

        System.out.println("[PaymentService] Thanh toán thành công!");
        return payment;
    }
}
