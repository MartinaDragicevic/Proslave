package SistemZaPlaniranjeProslava;

public class Client {
    private int id;
    private String ime, prezime, jmbg, broj_racuna, korisnicko_ime, lozinka;

    public Client() {}

    public Client(int id, String ime, String prezime, String jmbg, String broj_racuna, String korisnicko_ime, String lozinka) {
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
    public void setBrojRacuna(String korisnicko_ime) { this.broj_racuna = korisnicko_ime; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnicko_ime = korisnickoIme; }
    public void setLozinka(String lozinka) { this.lozinka = lozinka; }

    @Override
    public String toString() {
        return "Client [id=" + id + ", ime=" + ime + ", prezime=" + prezime + ", jmbg=" + jmbg + ", broj racuna=" + broj_racuna + ", korisnicko ime=" + korisnicko_ime + ", lozinka=" + lozinka + "]";
    }
}
