package controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import model.Room;
import service.RoomService;
import util.XMLUtil;

/**
 * REST Resource cho Room Service.
 * Base URL: /api/rooms
 */
@Path("/rooms")
public class RoomResource {

    /**
     * Lấy danh sách tất cả phòng.
     * GET /api/rooms
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRooms(@QueryParam("checkInDate") String checkInDate,
            @QueryParam("checkOutDate") String checkOutDate) {
        if (checkInDate == null || checkInDate.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Tham số checkInDate là bắt buộc\"}")
                    .build();
        }
        List<Room> rooms = RoomService.getAvailableRooms(checkInDate, checkOutDate);
        return Response.ok(rooms).build();
    }
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("id") String id) {
        Room room = RoomService.getRoomById(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Không tìm thấy phòng " + id + "\"}")
                    .build();
        }
        
        // Enhance response with payment status from the latest confirmed booking
        String paymentStatus = "none";
        java.util.List<model.Booking> allBookings = XMLUtil.readBookings();
        for (model.Booking b : allBookings) {
            if (b.getRoomId().equalsIgnoreCase(id) && "confirmed".equalsIgnoreCase(b.getStatus())) {
                paymentStatus = b.getPaymentStatus();
            }
        }
        
        // Return a customized JSON including payment status
        String jsonResponse = String.format(
            "{\"id\":\"%s\", \"type\":\"%s\", \"price\":%d, \"status\":\"%s\", \"paymentStatus\":\"%s\"}",
            room.getId(), room.getType(), (long)room.getPrice(), room.getStatus(), paymentStatus
        );
        
        return Response.ok(jsonResponse).build();
    }

    /**
     * Lọc phòng theo loại (Deluxe, Standard, Suite, VIP, Family).
     * GET /api/rooms/type/{type}
     */
    @GET
    @Path("/type/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getRoomsByType(@PathParam("type") String type) {
        return RoomService.getRoomsByType(type);
    }

    /**
     * Cập nhật trạng thái phòng.
     * PUT /api/rooms/{id}/status
     * Body: "available" hoặc "booked" hoặc "maintenance"
     */
    @PUT
    @Path("/{id}/status")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoomStatus(@PathParam("id") String id, String status) {
        Room room = RoomService.getRoomById(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Không tìm thấy phòng " + id + "\"}")
                    .build();
        }

        try {
            RoomService.updateRoomStatus(id, status);
            return Response.ok()
                    .entity("{\"message\": \"Đã cập nhật trạng thái phòng " + id + " thành " + status + "\"}")
                    .build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
}
