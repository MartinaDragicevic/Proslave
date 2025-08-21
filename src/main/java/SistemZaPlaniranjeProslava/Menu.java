package SistemZaPlaniranjeProslava;

public class Menu {
    private int id;
    private Venue objekat;
    private String opis;
    private double cijena_po_osobi;

    public Menu() {}

    public Menu(int id, Venue objekat, String opis, double cijena_po_osobi) {
        this.id = id;
        this.objekat = objekat;
        this.opis = opis;
        this.cijena_po_osobi = cijena_po_osobi;
    }

    public int getId() { return id; }
    public Venue getObjekat() { return objekat; }
    public String getOpis() { return opis; }
    public double getCijenaPoOsobi() { return cijena_po_osobi; }

    public void setId(int id) { this.id = id; }
    public void setObjekat(Venue objekat) { this.objekat = objekat; }
    public void setOpis(String opis) { this.opis = opis; }
    public void setCijenaPoOsobi(double cijena_po_osobi) { this.cijena_po_osobi = cijena_po_osobi; }

    @Override
    public String toString() {
        return "Menu [id=" + id + ", objekat=" + objekat + ", opis=" + opis + ", cijena_po_osobi=" + cijena_po_osobi + "]";
    }
}