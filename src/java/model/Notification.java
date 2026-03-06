package model;

public class Notification {

    private String email;
    private String message;
    private String type;

    public Notification() {
    }

    public Notification(String email, String message, String type) {
        this.email = email;
        this.message = message;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
