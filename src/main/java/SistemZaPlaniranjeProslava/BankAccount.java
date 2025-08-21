package SistemZaPlaniranjeProslava;

public class BankAccount {
    private int id;
    private String broj_racuna;
    private String jmbg;
    private double stanje;

    public BankAccount() {}

    public BankAccount(int id, String broj_racuna, String jmbg, double stanje) {
        this.id = id;
        this.broj_racuna = broj_racuna;
        this.jmbg = jmbg;
        this.stanje = stanje;
    }

    public int getId() { return id; }
    public String getBrojRacuna() { return broj_racuna; }
    public String getJmbg() { return jmbg; }
    public double getStanje() { return stanje; }

    public void setId(int id) { this.id = id; }
    public void setBrojRacuna(String broj_racuna) { this.broj_racuna = broj_racuna; }
    public void setJmbg(String jmbg) { this.jmbg = jmbg; }
    public void setStanje(double stanje) { this.stanje = stanje; }

    @Override
    public String toString() {
        return "BankAccount [id=" + id + ", brojRacuna=" + broj_racuna + ", jmbg=" + jmbg + ", stanje=" + stanje + "]";
    }
}
