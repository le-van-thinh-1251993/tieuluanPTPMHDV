package controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import model.Payment;
import model.Booking;
import service.PaymentService;
import util.XMLUtil;

/**
 * REST Resource cho Payment Service.
 * Base URL: /api/payments
 */
@Path("/payments")
public class PaymentResource {

    /**
     * Xử lý thanh toán.
     * POST /api/payments
     * 
     * Body mẫu:
     * {
     * "bookingId": "BK1001",
     * "amount": 1200000,
     * "cardNumber": "12345678"
     * }
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processPayment(Payment payment) {
        String validation = validate(payment);
        if (validation != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + validation + "\"}")
                    .build();
        }
        Payment result = PaymentService.processPayment(payment);
        if (!"success".equals(result.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    private String validate(Payment p) {
        if (p == null) return "Body không được rỗng";
        if (isBlank(p.getBookingId())) return "Thiếu bookingId";
        if (p.getAmount() <= 0) return "Số tiền phải > 0";
        if (isBlank(p.getCardNumber()) || p.getCardNumber().length() < 4) return "Số thẻ phải tối thiểu 4 ký tự";
        List<Booking> bookings = XMLUtil.readBookings();
        boolean exists = bookings.stream().anyMatch(b -> p.getBookingId().equalsIgnoreCase(b.getId()));
        if (!exists) return "bookingId không tồn tại";
        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
