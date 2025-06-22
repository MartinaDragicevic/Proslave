package SistemZaPlaniranjeProslava;

public class Menu {
    private int id;
    private String description;
    private double price;
    private int objekatId;

    public Menu() {}

    public Menu(int id, String description, double price) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.objekatId = 0;
    }

    public Menu(int id, String description, double price, int objekatId) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.objekatId = objekatId;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getObjekatId() { return objekatId; }

    public void setId(int id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setObjekatId(int objekatId) { this.objekatId = objekatId; }

    @Override
    public String toString() {
        return "Menu [id=" + id + ", description=" + description + ", price=" + price + ", objekatId=" + objekatId + "]";
    }
}