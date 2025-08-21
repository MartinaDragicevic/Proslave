package SistemZaPlaniranjeProslava;

import java.time.LocalDate;
import java.util.List;

public class Venue {
    private int id;
    private Owner vlasnik;
    private String naziv;
    private String adresa;
    private String grad;
    private int broj_mjesta;
    private double cijena_rezervacije;
    private String status;
    private int broj_stolova;
    private List<LocalDate> datumi;
    private double zarada;

    public Venue() {}

    public Venue(int id, Owner vlasnik, String naziv, double cijena_rezervacije, String grad, String adresa,
                 int broj_mjesta, int broj_stolova, List<LocalDate> datumi, double zarada, String status) {
        this.id = id;
        this.vlasnik = vlasnik;
        this.naziv = naziv;
        this.adresa = adresa;
        this.grad = grad;
        this.broj_mjesta = broj_mjesta;
        this.cijena_rezervacije = cijena_rezervacije;
        this.status = status;
        this.broj_stolova = broj_stolova;
        this.datumi = datumi;
        this.zarada = zarada;
    }

    public int getId() { return id; }
    public Owner getVlasnik() { return vlasnik; }
    public String getNaziv() { return naziv; }
    public String getAdresa() { return adresa; }
    public String getGrad() { return grad; }
    public int getBrojMjesta() { return broj_mjesta; }
    public double getCijenaRezervacije() { return cijena_rezervacije; }
    public String getStatus() { return status; }
    public int getBrojStolova() { return broj_stolova; }
    public List<LocalDate> getDatumi() { return datumi; }
    public double getZarada() { return zarada; }

    public void setId(int id) { this.id = id; }
    public void setVlasnik(Owner vlasnik) { this.vlasnik = vlasnik; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
    public void setAdresa(String adresa) { this.adresa = adresa; }
    public void setGrad(String grad) { this.grad = grad; }
    public void setBrojMjesta(int broj_mjesta) { this.broj_mjesta = broj_mjesta; }
    public void setCijenaRezervacije(double cijena_rezervacije) { this.cijena_rezervacije = cijena_rezervacije; }
    public void setStatus(String status) { this.status = status; }
    public void setBrojStolova(int broj_stolova) { this.broj_stolova = broj_stolova; }
    public void setDatumi(List<LocalDate> datumi) { this.datumi = datumi; }
    public void setZarada(double zarada) { this.zarada = zarada; }

    @Override
    public String toString() {
        return "Venue [id=" + id + ", vlasnik=" + vlasnik + ", naziv=" + naziv + ", adresa=" + adresa +
                ", grad=" + grad + ", broj_mjesta=" + broj_mjesta + ", cijena_rezervacije=" + cijena_rezervacije +
                ", status=" + status + ", broj_stolova=" + broj_stolova + ", datumi=" + datumi + ", zarada=" + zarada + "]";
    }
}