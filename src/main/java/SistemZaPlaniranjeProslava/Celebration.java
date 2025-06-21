package SistemZaPlaniranjeProslava;

public class Celebration {
    private int id;
    private String name, date, time;
    private int venueId;

    public Celebration() {}

    public Celebration(int id, String name, String date, String time, int venueId) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.venueId = venueId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getVenueId() { return venueId; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setVenueId(int venueId) { this.venueId = venueId; }

    @Override
    public String toString() {
        return "Celebration [id=" + id + ", name=" + name + ", date=" + date + ", time=" + time + ", venueId=" + venueId + "]";
    }
}
