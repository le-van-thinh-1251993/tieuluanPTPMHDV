package model;

public class Booking {

    private String id;
    private String customerName;
    private String email;
    private String roomId;
    private String checkInDate;
    private double amount;
    private String status;

    public Booking() {
    }

    public Booking(String id, String customerName, String email, String roomId,
            String checkInDate, double amount, String status) {
        this.id = id;
        this.customerName = customerName;
        this.email = email;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.amount = amount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
