package SistemZaPlaniranjeProslava;

public class Notification {
    private int id;
    private String message, date;

    public Notification() {}

    public Notification(int id, String message, String date) {
        this.id = id;
        this.message = message;
        this.date = date;
    }

    public int getId() { return id; }
    public String getMessage() { return message; }
    public String getDate() { return date; }

    public void setId(int id) { this.id = id; }
    public void setMessage(String message) { this.message = message; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "Notification [id=" + id + ", message=" + message + ", date=" + date + "]";
    }
}
