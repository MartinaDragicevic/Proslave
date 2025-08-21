package SistemZaPlaniranjeProslava;

import java.time.LocalDate;

public class Celebration {
    private int id;
    private Venue objekat;
    private Client klijent;
    private Menu meni;
    private String proslavacol;
    private LocalDate datum;
    private int brojGostiju;
    private double ukupnaCijena;
    private double uplacenIznos;

    public Celebration() {}

    public Celebration(int id, Venue objekat, Client klijent, Menu meni, String proslavacol, LocalDate datum, int brojGostiju, double ukupnaCijena, double uplacenIznos) {
        this.id = id;
        this.objekat = objekat;
        this.klijent = klijent;
        this.meni = meni;
        this.proslavacol = proslavacol;
        this.datum = datum;
        this.brojGostiju = brojGostiju;
        this.ukupnaCijena = ukupnaCijena;
        this.uplacenIznos = uplacenIznos;
    }

    public int getId() { return id; }
    public Venue getObjekat() { return objekat; }
    public Client getKlijent() { return klijent; }
    public Menu getMeni() { return meni; }
    public String getProslavacol() { return proslavacol; }
    public LocalDate getDatum() { return datum; }
    public int getBrojGostiju() { return brojGostiju; }
    public double getUkupnaCijena() { return ukupnaCijena; }
    public double getUplacenIznos() { return uplacenIznos; }

    public void setId(int id) { this.id = id; }
    public void setObjekat(Venue objekat) { this.objekat = objekat; }
    public void setKlijent(Client klijent) { this.klijent = klijent; }
    public void setMeni(Menu meni) { this.meni = meni; }
    public void setProslavacol(String proslavacol) { this.proslavacol = proslavacol; }
    public void setDatum(LocalDate datum) { this.datum = datum; }
    public void setBrojGostiju(int brojGostiju) { this.brojGostiju = brojGostiju; }
    public void setUkupnaCijena(double ukupnaCijena) { this.ukupnaCijena = ukupnaCijena; }
    public void setUplacenIznos(double uplacenIznos) { this.uplacenIznos = uplacenIznos; }

    @Override
    public String toString() {
        return "Celebration [id=" + id + ", objekat=" + objekat + ", klijent=" + klijent +
                ", meni=" + meni + ", proslavacol=" + proslavacol + ", datum=" + datum +
                ", brojGostiju=" + brojGostiju + ", ukupnaCijena=" + ukupnaCijena +
                ", uplacenIznos=" + uplacenIznos + "]";
    }
}