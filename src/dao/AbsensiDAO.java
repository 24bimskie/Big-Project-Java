package dao;

import model.Absensi;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Absensi.
 * Mendukung use case: Mulai Absen, Data Absen, Rekap Absen.
 */
public class AbsensiDAO {

    // TODO: Implement method
    public void insert(Absensi absensi) {
        // INSERT ke tabel absensi
    }

    public void update(Absensi absensi) {
        // UPDATE data absensi
    }

    public void delete(String idAbsensi) {
        // DELETE data absensi
    }

    public List<Absensi> getByJadwalAndTanggal(String idJadwal, LocalDate tanggal) {
        // SELECT absensi berdasarkan jadwal dan tanggal (untuk Mulai Absen)
        return null;
    }

    public List<Absensi> getByMahasiswa(String nim) {
        // SELECT absensi berdasarkan mahasiswa (untuk Data Absen mahasiswa)
        return null;
    }

    public List<Absensi> getRekapByJadwal(String idJadwal) {
        // SELECT rekap absensi berdasarkan jadwal (untuk Rekap Absen dosen)
        return null;
    }

    public List<Absensi> getByJadwalAndMahasiswa(String idJadwal, String nim) {
        // SELECT absensi per mahasiswa per jadwal
        return null;
    }
}
