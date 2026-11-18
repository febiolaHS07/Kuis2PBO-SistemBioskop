import java.io.Serializable;

public class Transaksi implements Serializable {
    private static final long serialVersionUID = 1L;
    private Penonton penonton;
    private JadwalFilm jadwal;
    private int jumlah;

    public Transaksi(Penonton penonton, JadwalFilm jadwal, int jumlah) {
        this.penonton = penonton;
        this.jadwal = jadwal;
        this.jumlah = jumlah;
    }

    public Penonton getPenonton() {
        return penonton;
    }

    public JadwalFilm getJadwal() {
        return jadwal;
    }

    public int getJumlah() {
        return jumlah;
    }
}
