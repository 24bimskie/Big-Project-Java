package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Model untuk entitas Absensi.
 * Digunakan pada use case: Mulai Absen, Data Absen, Rekap Absen.
 * Mencatat kehadiran mahasiswa pada jadwal tertentu.
 */
public class Absensi {

    private String idAbsensi;
    private String idJadwal;
    private String nim; // ID Mahasiswa
    private LocalDate tanggal;
    private LocalTime waktuAbsen;
    private String status; // "Hadir", "Izin", "Sakit", "Alpha"
    private String keterangan;
    private String hariJam;
    private String mataKuliah;
    private String kelas;
    private String namaLengkap;
    private String kehadiran;
    private int pertemuanKe;

    public Absensi() {}

    public Absensi(String idAbsensi, String idJadwal, String nim, LocalDate tanggal,
                   LocalTime waktuAbsen, String status, String keterangan, int pertemuanKe) {
        this.idAbsensi = idAbsensi;
        this.idJadwal = idJadwal;
        this.nim = nim;
        this.tanggal = tanggal;
        this.waktuAbsen = waktuAbsen;
        this.status = status;
        this.keterangan = keterangan;
        this.pertemuanKe = pertemuanKe;
    }

    // Getter dan Setter
    public String getIdAbsensi() { return idAbsensi; }
    public void setIdAbsensi(String idAbsensi) { this.idAbsensi = idAbsensi; }

    public String getIdJadwal() { return idJadwal; }
    public void setIdJadwal(String idJadwal) { this.idJadwal = idJadwal; }

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public LocalTime getWaktuAbsen() { return waktuAbsen; }
    public void setWaktuAbsen(LocalTime waktuAbsen) { this.waktuAbsen = waktuAbsen; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public String getHariJam() { return hariJam; }
    public void setHariJam(String hariJam) { this.hariJam = hariJam; }

    public String getMataKuliah() { return mataKuliah; }
    public void setMataKuliah(String mataKuliah) { this.mataKuliah = mataKuliah; }

    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getKehadiran() { return kehadiran; }
    public void setKehadiran(String kehadiran) { this.kehadiran = kehadiran; }

    public int getPertemuanKe() { return pertemuanKe; }
    public void setPertemuanKe(int pertemuanKe) { this.pertemuanKe = pertemuanKe; }
}
