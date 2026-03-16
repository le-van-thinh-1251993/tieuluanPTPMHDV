package model;

public class Payment {

    private String bookingId;
    private double amount;
    private String cardNumber;
    private String status;
    private String bankName;

    public Payment() {
    }

    public Payment(String bookingId, double amount, String cardNumber, String status, String bankName) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.status = status;
        this.bankName = bankName;
    }

    public Payment(String bookingId, double amount, String cardNumber, String status) {
        this(bookingId, amount, cardNumber, status, "");
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
