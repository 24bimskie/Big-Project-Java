package dao;

import model.Mahasiswa;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Mahasiswa.
 * Mendukung use case: Input Data Mahasiswa, Lihat Data Mahasiswa.
 */
public class MahasiswaDAO {

    // TODO: Implement method
    public void insert(Mahasiswa mahasiswa) {
        // INSERT ke tabel mahasiswa
    }

    public void update(Mahasiswa mahasiswa) {
        // UPDATE data mahasiswa berdasarkan NIM
    }

    public void delete(String nim) {
        // DELETE data mahasiswa berdasarkan NIM
    }

    public Mahasiswa getByNim(String nim) {
        // SELECT mahasiswa berdasarkan NIM
        return null;
    }

    public List<Mahasiswa> getAll() {
        // SELECT semua mahasiswa
        return null;
    }

    public List<Mahasiswa> getByKelas(String idKelas) {
        // SELECT mahasiswa berdasarkan kelas
        return null;
    }
}
