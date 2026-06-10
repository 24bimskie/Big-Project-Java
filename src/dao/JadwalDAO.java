package dao;

import model.Jadwal;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Jadwal.
 * Mendukung use case: Input Data Jadwal, Lihat Jadwal.
 */
public class JadwalDAO {

    // TODO: Implement method
    public void insert(Jadwal jadwal) {
        // INSERT ke tabel jadwal
    }

    public void update(Jadwal jadwal) {
        // UPDATE data jadwal
    }

    public void delete(String idJadwal) {
        // DELETE data jadwal
    }

    public Jadwal getById(String idJadwal) {
        // SELECT jadwal berdasarkan ID
        return null;
    }

    public List<Jadwal> getAll() {
        // SELECT semua jadwal
        return null;
    }

    public List<Jadwal> getByDosen(String nidn) {
        // SELECT jadwal berdasarkan dosen (untuk Dosen: Jadwal mengajar)
        return null;
    }

    public List<Jadwal> getByKelas(String idKelas) {
        // SELECT jadwal berdasarkan kelas (untuk Mahasiswa: Jadwal masuk)
        return null;
    }
}
