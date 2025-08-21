package SistemZaPlaniranjeProslava;

public class TableLayout {
    private int id;
    private Table sto;
    private Celebration proslava;
    private String gosti;

    public TableLayout() {}

    public TableLayout(int id, Table sto, Celebration proslava, String gosti) {
        this.id = id;
        this.sto = sto;
        this.proslava = proslava;
        this.gosti = gosti;
    }

    public int getId() { return id; }
    public Table getSto() { return sto; }
    public Celebration getProslava() { return proslava; }
    public String getGosti() { return gosti; }

    public void setId(int id) { this.id = id; }
    public void setSto(Table sto) { this.sto = sto; }
    public void setProslava(Celebration proslava) { this.proslava = proslava; }
    public void setGosti(String gosti) { this.gosti = gosti; }

    @Override
    public String toString() {
        return "TableLayout [id=" + id + ", sto=" + sto + ", proslava=" + proslava + ", gosti=" + gosti + "]";
    }
}