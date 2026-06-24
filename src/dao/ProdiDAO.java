package dao;

import config.DatabaseConnection;
import model.Prodi;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        // Prodi tidak punya tabel sendiri, akan disimpan via Kelas
        // Method ini kosong karena insert dilakukan saat menambah kelas
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
        Set<String> seen = new LinkedHashSet<>();
        List<Prodi> list = new ArrayList<>();

        String[] queries = {
                "SELECT DISTINCT Prodi FROM kelas WHERE Prodi IS NOT NULL AND TRIM(Prodi) <> ''",
                "SELECT DISTINCT prodi FROM mahasiswa WHERE prodi IS NOT NULL AND TRIM(prodi) <> ''"
        };

        try (Connection conn = DatabaseConnection.getConnection()) {
            for (String query : queries) {
                try (PreparedStatement stmt = conn.prepareStatement(query);
                        ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String value = rs.getString(1);
                        if (value != null && !value.trim().isEmpty() && seen.add(value.trim())) {
                            list.add(new Prodi(value.trim(), value.trim(), ""));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
