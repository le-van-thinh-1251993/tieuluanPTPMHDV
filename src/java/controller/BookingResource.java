package controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import model.Booking;
import model.BookingRequest;
import model.BookingResponse;
import service.BookingService;

/**
 * REST Resource cho Booking Service.
 * Base URL: /api/bookings
 */
@Path("/bookings")
public class BookingResource {

    /**
     * Tạo booking mới.
     * POST /api/bookings
     * 
     * Body mẫu:
     * {
     * "customerName": "Nguyen Van A",
     * "email": "vana@gmail.com",
     * "roomId": "R01",
     * "checkInDate": "2026-03-10",
     * "paymentInfo": {
     * "cardNumber": "12345678",
     * "amount": 1200000
     * }
     * }
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBooking(BookingRequest request) {
        BookingResponse result = BookingService.createBooking(request);

        if ("confirmed".equals(result.getStatus())) {
            return Response.status(Response.Status.CREATED)
                    .entity(result)
                    .build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(result)
                    .build();
        }
    }

    /**
     * Lấy danh sách tất cả bookings.
     * GET /api/bookings
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> getAllBookings() {
        return BookingService.getAllBookings();
    }
}
