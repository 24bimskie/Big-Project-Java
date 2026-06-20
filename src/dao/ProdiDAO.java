package dao;

import config.DatabaseConnection;
import model.Prodi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk data Program Studi.
 * PERHATIAN: Tabel 'prodi' TIDAK ADA di database (sam.sql).
 * Data prodi disimpan sebagai kolom VARCHAR 'Prodi' di tabel 'kelas'.
 *
 * Implementasi ini mengambil nilai unik kolom Prodi dari tabel kelas
 * dan memperlakukannya sebagai daftar prodi.
 */
public class ProdiDAO {

    public void insert(Prodi prodi) {
        // Prodi tidak memiliki tabel sendiri di DB.
        // Prodi ditambahkan secara implisit saat menambah kelas baru.
        // Method ini sengaja dikosongkan.
    }

    public void update(Prodi prodi) {
        // Update nama prodi di semua baris kelas yang menggunakan prodi tersebut
        String query = "UPDATE kelas SET Prodi=? WHERE Prodi=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, prodi.getNamaProdi());
            stmt.setString(2, prodi.getIdProdi()); // idProdi = nama prodi lama
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String idProdi) {
        // Hapus semua kelas yang termasuk dalam prodi ini
        String query = "DELETE FROM kelas WHERE Prodi=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idProdi);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Prodi getById(String idProdi) {
        // Cari apakah ada kelas dengan prodi tersebut
        String query = "SELECT DISTINCT Prodi FROM kelas WHERE Prodi=? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idProdi);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Prodi(rs.getString("Prodi"), rs.getString("Prodi"), "");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mengambil daftar prodi unik dari kolom Prodi di tabel kelas.
     * idProdi dan namaProdi sama (nama prodi itu sendiri).
     */
    public List<Prodi> getAll() {
        List<Prodi> list = new ArrayList<>();
        String query = "SELECT DISTINCT Prodi FROM kelas ORDER BY Prodi";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String namaProdi = rs.getString("Prodi");
                list.add(new Prodi(namaProdi, namaProdi, "")); // id = nama prodi
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
