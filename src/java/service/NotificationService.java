package service;

import model.Notification;

/**
 * Notification Service - Gửi thông báo xác nhận.
 * 
 * Trong phiên bản demo, thông báo được in ra console.
 * Trong thực tế, sẽ gửi email thực qua SMTP.
 */
public class NotificationService {

    /**
     * Gửi thông báo (giả lập qua console).
     */
    public static void sendNotification(Notification notification) {
        System.out.println("========================================");
        System.out.println("[NotificationService] GỬI THÔNG BÁO");
        System.out.println("  - Email: " + notification.getEmail());
        System.out.println("  - Loại: " + notification.getType());
        System.out.println("  - Nội dung: " + notification.getMessage());
        System.out.println("========================================");
    }
}
