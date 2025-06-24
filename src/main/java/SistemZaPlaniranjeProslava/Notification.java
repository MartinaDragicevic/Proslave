package SistemZaPlaniranjeProslava;

public class Notification {
    private int id;
    private String text;
    private int objekatId;

    public Notification() {}

    public Notification(int id, int objekatId, String text) {
        this.id = id;
        this.text = text;
        this.objekatId = objekatId;
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public int getObjekatId() { return objekatId; }

    public void setId(int id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setObjekatId(int objekatId) { this.objekatId = objekatId; }

    @Override
    public String toString() {
        return "Notification [id=" + id + ", text=" + text;
    }
}
