package SistemZaPlaniranjeProslava;

public class Table {
    private int id;
    private int capacity;
    private String position;

    public Table() {}

    public Table(int id, int capacity, String position) {
        this.id = id;
        this.capacity = capacity;
        this.position = position;
    }

    public int getId() { return id; }
    public int getCapacity() { return capacity; }
    public String getPosition() { return position; }

    public void setId(int id) { this.id = id; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setPosition(String position) { this.position = position; }

    @Override
    public String toString() {
        return "Table [id=" + id + ", capacity=" + capacity + ", position=" + position + "]";
    }
}
