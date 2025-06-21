package SistemZaPlaniranjeProslava;

public class TableLayout {
    private int id;
    private int tableId;
    private int celebrationId;
    private String position;

    public TableLayout() {}

    public TableLayout(int id, int tableId, int celebrationId, String position) {
        this.id = id;
        this.tableId = tableId;
        this.celebrationId = celebrationId;
        this.position = position;
    }

    public int getId() { return id; }
    public int getTableId() { return tableId; }
    public int getCelebrationId() { return celebrationId; }
    public String getPosition() { return position; }

    public void setId(int id) { this.id = id; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public void setCelebrationId(int celebrationId) { this.celebrationId = celebrationId; }
    public void setPosition(String position) { this.position = position; }

    @Override
    public String toString() {
        return "TableLayout [id=" + id + ", tableId=" + tableId + ", celebrationId=" + celebrationId + ", position=" + position + "]";
    }
}
