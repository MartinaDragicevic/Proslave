package SistemZaPlaniranjeProslava;

public class Venue {
    private int id;
    private String name;
    private String address;
    private String place;
    private int capacity;
    private double reservationPrice;
    private int ownerId;
    private String status;
    private int brojStolova;
    private String datumi;
    private double zarada;

    public Venue() {}

    public Venue(int id, int ownerId, String name, double reservationPrice, String place, String address, int capacity, int brojStolova, String datumi, double zarada, String status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.place = place;
        this.capacity = capacity;
        this.reservationPrice = reservationPrice;
        this.ownerId = ownerId;
        this.status = status;
        this.brojStolova = brojStolova;
        this.datumi = datumi;
        this.zarada = zarada;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPlace() { return place; }
    public int getCapacity() { return capacity; }
    public double getReservationPrice() { return reservationPrice; }
    public int getOwnerId() { return ownerId; }
    public String getStatus() { return status; }
    public int getBrojStolova() { return brojStolova; }
    public String getDatumi() { return datumi; }
    public double getZarada() { return zarada; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPlace(String place) { this.place = place; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setReservationPrice(double reservationPrice) { this.reservationPrice = reservationPrice; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public void setStatus(String status) { this.status = status; }
    public void setBrojStolova(int brojStolova) { this.brojStolova = brojStolova; }
    public void setDatumi(String datumi) { this.datumi = datumi; }
    public void setZarada(double zarada) { this.zarada = zarada; }

    @Override
    public String toString() {
        return "Venue [id=" + id + ", name=" + name + ", address=" + address + ", place=" + place +
                ", capacity=" + capacity + ", reservationPrice=" + reservationPrice +
                ", ownerId=" + ownerId + ", status=" + status +
                ", brojStolova=" + brojStolova + ", datumi=" + datumi + ", zarada=" + zarada + "]";
    }
}
