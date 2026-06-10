package dao;

import model.Kelas;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Kelas.
 * Mendukung use case: Input Kelas.
 */
public class KelasDAO {

    // TODO: Implement method
    public void insert(Kelas kelas) {
        // INSERT ke tabel kelas
    }

    public void update(Kelas kelas) {
        // UPDATE data kelas
    }

    public void delete(String idKelas) {
        // DELETE data kelas
    }

    public Kelas getById(String idKelas) {
        // SELECT kelas berdasarkan ID
        return null;
    }

    public List<Kelas> getAll() {
        // SELECT semua kelas
        return null;
    }

    public List<Kelas> getByProdi(String idProdi) {
        // SELECT kelas berdasarkan prodi
        return null;
    }
}
