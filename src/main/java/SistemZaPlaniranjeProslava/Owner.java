package SistemZaPlaniranjeProslava;

public class Owner {
    private int id;
    private String ime;
    private String prezime;
    private String jmbg;
    private String broj_racuna;
    private String korisnicko_ime;
    private String lozinka;

    public Owner() {}

    public Owner(int id, String ime, String prezime, String jmbg, String broj_racuna, String korisnicko_ime, String lozinka) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.jmbg = jmbg;
        this.broj_racuna = broj_racuna;
        this.korisnicko_ime = korisnicko_ime;
        this.lozinka = lozinka;
    }

    public int getId() { return id; }
    public String getIme() { return ime; }
    public String getPrezime() { return prezime; }
    public String getJmbg() { return jmbg; }
    public String getBrojRacuna() { return broj_racuna; }
    public String getKorisnickoIme() { return korisnicko_ime; }
    public String getLozinka() { return lozinka; }

    public void setId(int id) { this.id = id; }
    public void setIme(String ime) { this.ime = ime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }
    public void setJmbg(String jmbg) { this.jmbg = jmbg; }
    public void setBrojRacuna(String broj_racuna) { this.broj_racuna = broj_racuna; }
    public void setKorisnickoIme(String korisnicko_ime) { this.korisnicko_ime = korisnicko_ime; }
    public void setLozinka(String lozinka) { this.lozinka = lozinka; }

    @Override
    public String toString() {
        return "Owner [id=" + id + ", ime=" + ime + ", prezime=" + prezime + ", jmbg=" + jmbg + ", broj_racuna=" + broj_racuna + ", korisnicko_ime=" + korisnicko_ime + ", lozinka=" + lozinka + "]";
    }
}