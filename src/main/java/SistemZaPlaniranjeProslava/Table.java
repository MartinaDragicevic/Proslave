package SistemZaPlaniranjeProslava;

public class Table {
    private int id;
    private Venue objekat;
    private int broj_mjesta;

    public Table() {}

    public Table(int id, Venue objekat, int broj_mjesta) {
        this.id = id;
        this.objekat = objekat;
        this.broj_mjesta = broj_mjesta;
    }

    public int getId() { return id; }
    public Venue getObjekat() { return objekat; }
    public int getBrojMjesta() { return broj_mjesta; }

    public void setId(int id) { this.id = id; }
    public void setObjekat(Venue objekat) { this.objekat = objekat; }
    public void setBrojMjesta(int broj_mjesta) { this.broj_mjesta = broj_mjesta; }

    @Override
    public String toString() {
        return "Table [id=" + id + ", objekat=" + objekat + ", broj_mjesta=" + broj_mjesta + "]";
    }
}