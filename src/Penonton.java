public class Penonton extends Entitas {
    private static final long serialVersionUID = 1L;

    public Penonton(String id, String nama) {
        super(id, nama);
    }

    @Override
    public String getInfo() {
        return "Penonton " + getId() + " - " + getNama();
    }
}
