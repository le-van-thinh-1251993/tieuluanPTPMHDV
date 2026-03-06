package controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
        NotificationService.sendNotification(notification);
        return Response.ok()
                .entity("{\"message\": \"Thông báo đã được gửi thành công\"}")
                .build();
    }
}
