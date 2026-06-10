package dao;

import model.Dosen;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Dosen.
 * Mendukung use case: Input Data Dosen, Lihat Data Dosen.
 */
public class DosenDAO {

    // TODO: Implement method
    public void insert(Dosen dosen) {
        // INSERT ke tabel dosen
    }

    public void update(Dosen dosen) {
        // UPDATE data dosen berdasarkan NIDN
    }

    public void delete(String nidn) {
        // DELETE data dosen berdasarkan NIDN
    }

    public Dosen getByNidn(String nidn) {
        // SELECT dosen berdasarkan NIDN
        return null;
    }

    public List<Dosen> getAll() {
        // SELECT semua dosen
        return null;
    }
}
