package model;

public class Room {

    private String id;
    private String type;
    private double price;
    private String status;

    public Room() {
    }

    public Room(String id, String type, double price, String status) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
