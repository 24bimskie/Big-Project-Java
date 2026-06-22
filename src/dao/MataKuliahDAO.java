package dao;

import config.DatabaseConnection;
import model.MataKuliah;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Mata Kuliah.
 * Menggunakan skema tabel:
 *   mata_kuliah(id INT AUTO_INCREMENT, kode_mk VARCHAR, nama_mk VARCHAR,
 *               sks INT, semester INT, id_prodi VARCHAR)
 */
public class MataKuliahDAO {

    /**
     * Menyimpan data mata kuliah baru ke database.
     */
    public void insert(MataKuliah mk) {
        String query = "INSERT INTO mata_kuliah (kode_mk, nama_mk, sks, semester, id_prodi) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mk.getKodeMk());
            stmt.setString(2, mk.getNamaMk());
            stmt.setInt(3, mk.getSks());
            stmt.setInt(4, mk.getSemester());
            stmt.setString(5, mk.getIdProdi());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Memperbarui data mata kuliah berdasarkan kode_mk (primary key logis).
     */
    public void update(MataKuliah mk) {
        String query = "UPDATE mata_kuliah SET nama_mk=?, sks=?, semester=?, id_prodi=? WHERE kode_mk=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mk.getNamaMk());
            stmt.setInt(2, mk.getSks());
            stmt.setInt(3, mk.getSemester());
            stmt.setString(4, mk.getIdProdi());
            stmt.setString(5, mk.getKodeMk());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Menghapus mata kuliah berdasarkan kode_mk.
     */
    public void delete(String kodeMk) {
        String query = "DELETE FROM mata_kuliah WHERE kode_mk=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kodeMk);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mengambil satu data mata kuliah berdasarkan kode_mk.
     */
    public MataKuliah getByKode(String kodeMk) {
        String query = "SELECT * FROM mata_kuliah WHERE kode_mk=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kodeMk);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mengambil semua data mata kuliah, diurutkan berdasarkan nama.
     */
    public List<MataKuliah> getAll() {
        List<MataKuliah> list = new ArrayList<>();
        String query = "SELECT * FROM mata_kuliah ORDER BY nama_mk";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mengambil mata kuliah berdasarkan program studi.
     */
    public List<MataKuliah> getByProdi(String idProdi) {
        List<MataKuliah> list = new ArrayList<>();
        String query = "SELECT * FROM mata_kuliah WHERE id_prodi=? ORDER BY nama_mk";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idProdi);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Memetakan satu baris ResultSet ke objek MataKuliah.
     */
    private MataKuliah mapRow(ResultSet rs) throws SQLException {
        return new MataKuliah(
                rs.getString("kode_mk"),
                rs.getString("nama_mk"),
                rs.getInt("sks"),
                rs.getInt("semester"),
                rs.getString("id_prodi")
        );
    }
}
