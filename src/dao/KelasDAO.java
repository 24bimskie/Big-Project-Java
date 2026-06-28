package dao;

import config.DatabaseConnection;
import model.Kelas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Kelas.
 * Menggunakan skema tabel: kelas(id INT AUTO_INCREMENT, Prodi VARCHAR, kelas
 * VARCHAR)
 * Catatan: id adalah integer auto-increment, bukan string.
 * Kolom tahun_akademik tidak ada di DB saat ini.
 * Database sudah dibersihkan dari data invalid (Prompt Engineering, Robi
 * Silat).
 */
public class KelasDAO {

    public void insert(Kelas kelas) {
        String query = "INSERT INTO kelas (Prodi, kelas) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kelas.getIdProdi()); // idProdi → kolom Prodi
            stmt.setString(2, kelas.getNamaKelas()); // namaKelas → kolom kelas
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Kelas kelas) {
        // Update berdasarkan id integer (disimpan di idKelas sebagai string)
        String query = "UPDATE kelas SET Prodi=?, kelas=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kelas.getIdProdi());
            stmt.setString(2, kelas.getNamaKelas());
            stmt.setInt(3, parseId(kelas.getIdKelas()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String idKelas) {
        String query = "DELETE FROM kelas WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(idKelas));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Kelas getById(String idKelas) {
        String query = "SELECT * FROM kelas WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(idKelas));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Kelas> getAll() {
        List<Kelas> list = new ArrayList<>();
        String query = "SELECT * FROM kelas ORDER BY Prodi, kelas";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Kelas> getByProdi(String namaProdi) {
        List<Kelas> list = new ArrayList<>();
        String query = "SELECT * FROM kelas WHERE Prodi=? ORDER BY kelas";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, namaProdi);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Memetakan satu baris ResultSet ke objek Kelas */
    private Kelas mapRow(ResultSet rs) throws SQLException {
        return new Kelas(
                String.valueOf(rs.getInt("id")), // id integer → string
                rs.getString("kelas"), // kolom kelas → namaKelas
                rs.getString("Prodi"), // kolom Prodi → idProdi
                "2024/2025" // tahun akademik default
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
