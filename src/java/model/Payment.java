package model;

public class Payment {

    private String bookingId;
    private double amount;
    private String cardNumber;
    private String status;

    public Payment() {
    }

    public Payment(String bookingId, double amount, String cardNumber, String status) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
