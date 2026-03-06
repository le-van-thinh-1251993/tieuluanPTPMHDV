package controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import model.Room;
import service.RoomService;

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
    public List<Room> getRooms() {
        return RoomService.getAllRooms();
    }

    /**
     * Xem chi tiết 1 phòng theo ID.
     * GET /api/rooms/{id}
     */
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
        return Response.ok(room).build();
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

        RoomService.updateRoomStatus(id, status);
        return Response.ok()
                .entity("{\"message\": \"Đã cập nhật trạng thái phòng " + id + " thành " + status + "\"}")
                .build();
    }
}
