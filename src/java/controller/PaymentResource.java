package controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Payment;
import service.PaymentService;

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
    public Payment processPayment(Payment payment) {
        return PaymentService.processPayment(payment);
    }
}
