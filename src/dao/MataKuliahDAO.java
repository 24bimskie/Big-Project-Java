package dao;

import config.DatabaseConnection;
import model.MataKuliah;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Mata Kuliah.
 * Menggunakan skema tabel: mata_kuliah(id INT AUTO_INCREMENT, Mata_Kuliah VARCHAR)
 * Catatan: tabel hanya memiliki nama MK. Kolom sks, semester, id_prodi tidak ada di DB.
 */
public class MataKuliahDAO {

    public void insert(MataKuliah mk) {
        String query = "INSERT INTO mata_kuliah (Mata_Kuliah) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mk.getNamaMk());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(MataKuliah mk) {
        // Update berdasarkan id integer (disimpan di kodeMk sebagai string)
        String query = "UPDATE mata_kuliah SET Mata_Kuliah=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mk.getNamaMk());
            stmt.setInt(2, parseId(mk.getKodeMk()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String kodeMk) {
        String query = "DELETE FROM mata_kuliah WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(kodeMk));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MataKuliah getByKode(String kodeMk) {
        String query = "SELECT * FROM mata_kuliah WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(kodeMk));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MataKuliah> getAll() {
        List<MataKuliah> list = new ArrayList<>();
        String query = "SELECT * FROM mata_kuliah ORDER BY Mata_Kuliah";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MataKuliah> getByProdi(String idProdi) {
        // Kolom prodi tidak ada di tabel, kembalikan semua
        return getAll();
    }

    /** Memetakan satu baris ResultSet ke objek MataKuliah */
    private MataKuliah mapRow(ResultSet rs) throws SQLException {
        return new MataKuliah(
                String.valueOf(rs.getInt("id")),   // id integer → kodeMk
                rs.getString("Mata_Kuliah"),        // → namaMk
                0,                                  // sks tidak ada di DB
                0,                                  // semester tidak ada di DB
                ""                                  // id_prodi tidak ada di DB
        );
    }

    /** Konversi string id ke integer dengan aman */
    private int parseId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
