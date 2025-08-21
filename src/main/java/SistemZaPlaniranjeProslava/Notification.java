package SistemZaPlaniranjeProslava;

public class Notification {
    private int id;
    private Venue objekat;
    private String tekst;

    public Notification() {}

    public Notification(int id, Venue objekat, String tekst) {
        this.id = id;
        this.objekat = objekat;
        this.tekst = tekst;
    }

    public int getId() { return id; }
    public Venue getObjekat() { return objekat; }
    public String getTekst() { return tekst; }

    public void setId(int id) { this.id = id; }
    public void setObjekat(Venue objekat) { this.objekat = objekat; }
    public void setTekst(String tekst) { this.tekst = tekst; }

    @Override
    public String toString() {
        return "Notification [id=" + id + ", objekat=" + objekat + ", tekst=" + tekst + "]";
    }
}