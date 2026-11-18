public class JadwalFilm extends Entitas {
    private static final long serialVersionUID = 1L;
    private int kuota;

    public JadwalFilm(String id, String nama, int kuota) {
        super(id, nama);
        this.kuota = kuota;
    }

    public int getKuota() {
        return kuota;
    }

    public void kurangiKuota(int jumlah) throws TransaksiTidakValidException {
        if (jumlah > kuota) {
            throw new TransaksiTidakValidException("Kuota " + getNama() + " tidak cukup (sisa " + kuota + ", diminta " + jumlah + ")");
        }
        kuota -= jumlah;
    }

    @Override
    public String getInfo() {
        return "Jadwal " + getId() + " - " + getNama() + " (kuota " + kuota + ")";
    }
}
