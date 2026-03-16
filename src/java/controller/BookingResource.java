package controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.regex.Pattern;
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
        String validationMsg = validateRequest(request);
        if (validationMsg != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new BookingResponse(null, "failed", validationMsg))
                    .build();
        }

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

    /**
     * Xem chi tiết 1 booking theo mã để kiểm tra trạng thái.
     * GET /api/bookings/{id}
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooking(@PathParam("id") String id) {
        Booking booking = BookingService.getBookingById(id);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Không tìm thấy booking " + id + "\"}")
                    .build();
        }
        return Response.ok(booking).build();
    }

    private String validateRequest(BookingRequest req) {
        if (req == null) return "Body không được rỗng";
        if (isBlank(req.getCustomerName())) return "Thiếu họ tên khách hàng";
        if (isBlank(req.getEmail()) || !EMAIL_PATTERN.matcher(req.getEmail()).matches()) return "Email không hợp lệ";
        if (isBlank(req.getRoomId())) return "Thiếu mã phòng";
        if (isBlank(req.getCheckInDate())) return "Thiếu ngày check-in (yyyy-MM-dd)";
        if (req.getNights() <= 0) return "Số đêm phải >= 1";
        BookingRequest.PaymentInfo pi = req.getPaymentInfo();
        if (pi == null) return "Thiếu thông tin thanh toán";
        if (isBlank(pi.getCardNumber()) || pi.getCardNumber().length() < 4) return "Số thẻ phải tối thiểu 4 ký tự";
        if (pi.getAmount() <= 0) return "Số tiền thanh toán phải > 0";
        if (isBlank(pi.getBankName())) return "Thiếu tên ngân hàng";
        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
}
