package dao;

import model.Prodi;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Program Studi.
 * Mendukung use case: Input Data Prodi.
 */
public class ProdiDAO {

    // TODO: Implement method
    public void insert(Prodi prodi) {
        // INSERT ke tabel prodi
    }

    public void update(Prodi prodi) {
        // UPDATE data prodi
    }

    public void delete(String idProdi) {
        // DELETE data prodi
    }

    public Prodi getById(String idProdi) {
        // SELECT prodi berdasarkan ID
        return null;
    }

    public List<Prodi> getAll() {
        // SELECT semua prodi
        return null;
    }
}
