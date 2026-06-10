package dao;

import model.MataKuliah;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Mata Kuliah.
 * Mendukung use case: Input Mata Kuliah.
 */
public class MataKuliahDAO {

    // TODO: Implement method
    public void insert(MataKuliah mk) {
        // INSERT ke tabel mata_kuliah
    }

    public void update(MataKuliah mk) {
        // UPDATE data mata kuliah
    }

    public void delete(String kodeMk) {
        // DELETE data mata kuliah
    }

    public MataKuliah getByKode(String kodeMk) {
        // SELECT mata kuliah berdasarkan kode
        return null;
    }

    public List<MataKuliah> getAll() {
        // SELECT semua mata kuliah
        return null;
    }

    public List<MataKuliah> getByProdi(String idProdi) {
        // SELECT mata kuliah berdasarkan prodi
        return null;
    }
}
