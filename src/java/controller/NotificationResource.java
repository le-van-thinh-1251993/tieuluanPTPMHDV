package controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import model.Notification;
import service.NotificationService;

/**
 * REST Resource cho Notification Service.
 * Base URL: /api/notifications
 */
@Path("/notifications")
public class NotificationResource {

    /**
     * Gửi thông báo.
     * POST /api/notifications
     * 
     * Body mẫu:
     * {
     * "email": "vana@gmail.com",
     * "message": "Đặt phòng thành công!",
     * "type": "booking_confirmation"
     * }
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendNotification(Notification notification) {
        String validation = validate(notification);
        if (validation != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + validation + "\"}")
                    .build();
        }
        NotificationService.sendNotification(notification);
        return Response.ok()
                .entity("{\"message\": \"Thông báo đã được gửi thành công\"}")
                .build();
    }

    private String validate(Notification n) {
        if (n == null) return "Body không được rỗng";
        if (isBlank(n.getEmail()) || !EMAIL_PATTERN.matcher(n.getEmail()).matches()) return "Email không hợp lệ";
        if (isBlank(n.getMessage())) return "Thiếu nội dung thông báo";
        if (isBlank(n.getType()) || !ALLOWED_TYPES.contains(n.getType())) return "Type không hợp lệ. Giá trị hợp lệ: " + ALLOWED_TYPES;
        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "booking_confirmation",
            "payment_success",
            "payment_failed",
            "booking_cancelled");
}
