import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class SistemBioskop implements Serializable {
    private static final long serialVersionUID = 1L;
    private String namaBioskop;
    private ArrayList<Penonton> daftarPenonton = new ArrayList<>();
    private ArrayList<JadwalFilm> daftarJadwal = new ArrayList<>();
    private ArrayList<Transaksi> daftarTransaksi = new ArrayList<>();

    public SistemBioskop(String namaBioskop) {
        this.namaBioskop = namaBioskop;
    }

    public void tambahPenonton(String id, String nama) {
        daftarPenonton.add(new Penonton(id, nama));
    }

    public void tambahJadwalFilm(String id, String nama, int kuota) {
        daftarJadwal.add(new JadwalFilm(id, nama, kuota));
    }

    private Penonton cariPenonton(String id) throws TransaksiTidakValidException {
        for (Penonton p : daftarPenonton) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        throw new TransaksiTidakValidException("Penonton dengan ID " + id + " tidak ditemukan");
    }

    private JadwalFilm cariJadwal(String kode) throws TransaksiTidakValidException {
        for (JadwalFilm j : daftarJadwal) {
            if (j.getId().equals(kode)) {
                return j;
            }
        }
        throw new TransaksiTidakValidException("Jadwal film dengan kode " + kode + " tidak ditemukan");
    }

    private String ambilNilai(String bagian, String kunci) throws TransaksiTidakValidException {
        String[] pecah = bagian.split(":");
        if (pecah.length != 2 || !pecah[0].trim().equals(kunci)) {
            throw new TransaksiTidakValidException("Bagian '" + bagian + "' tidak sesuai format " + kunci + ":nilai");
        }
        return pecah[1].trim();
    }

    private void prosesTransaksi(String data) throws TransaksiTidakValidException {
        String[] bagian = data.trim().split(";");
        if (bagian.length != 3) {
            throw new TransaksiTidakValidException("Format data salah: " + data);
        }

        String idPenonton = ambilNilai(bagian[0], "ID");
        String kodeFilm = ambilNilai(bagian[1], "KD");
        String teksJumlah = ambilNilai(bagian[2], "JML");

        int jumlah;
        try {
            jumlah = Integer.parseInt(teksJumlah);
        } catch (NumberFormatException e) {
            throw new TransaksiTidakValidException("Jumlah bukan angka: " + teksJumlah);
        }
        if (jumlah <= 0) {
            throw new TransaksiTidakValidException("Jumlah harus lebih dari 0");
        }

        Penonton penonton = cariPenonton(idPenonton);
        JadwalFilm jadwal = cariJadwal(kodeFilm);
        jadwal.kurangiKuota(jumlah);
        daftarTransaksi.add(new Transaksi(penonton, jadwal, jumlah));
    }

    public void jalankanTransaksiDariFile(String namaFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(namaFile))) {
            String baris;
            int nomor = 0;
            while ((baris = br.readLine()) != null) {
                String[] daftarData = baris.split("\\|");
                for (String data : daftarData) {
                    if (data.trim().isEmpty()) {
                        continue;
                    }
                    nomor++;
                    try {
                        prosesTransaksi(data);
                        System.out.println("[OK] Transaksi " + nomor + ": " + data.trim());
                    } catch (TransaksiTidakValidException e) {
                        System.out.println("[GAGAL] Transaksi " + nomor + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    public void simpanLaporanAkhir() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("state.bak"))) {
            oos.writeObject(this);
            System.out.println("Status sistem disimpan ke state.bak");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan state.bak: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("laporan.txt"))) {
            String format = "%-4s %-12s %-10s %-10s %-26s %6s%n";
            String formatBaris = "%-4d %-12s %-10s %-10s %-26s %6d%n";
            pw.printf(format, "No", "ID Penonton", "Nama", "Kode Film", "Judul Film", "Jumlah");
            pw.println("-".repeat(73));
            int nomor = 1;
            for (Transaksi t : daftarTransaksi) {
                pw.printf(formatBaris, nomor, t.getPenonton().getId(), t.getPenonton().getNama(),
                        t.getJadwal().getId(), t.getJadwal().getNama(), t.getJumlah());
                nomor++;
            }
            System.out.println("Laporan disimpan ke laporan.txt");
        } catch (IOException e) {
            System.out.println("Gagal menulis laporan.txt: " + e.getMessage());
        }
    }
}
