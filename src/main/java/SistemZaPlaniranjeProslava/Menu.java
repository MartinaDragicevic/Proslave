package SistemZaPlaniranjeProslava;

public class Menu {
    private int id;
    private String description;
    private double price;

    public Menu() {}

    public Menu(int id, String description, double price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }

    public void setId(int id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Menu [id=" + id + ", description=" + description + ", price=" + price + "]";
    }
}
