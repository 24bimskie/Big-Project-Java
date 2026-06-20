package dao;

import config.DatabaseConnection;
import model.Mahasiswa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Mahasiswa.
 * Menggunakan skema tabel: mahasiswa(id, nim, nama, gender, alamat, password)
 * gender: enum 'L' atau 'P'
 * Catatan: kolom id_kelas & email tidak ada di DB, diabaikan saat insert/update.
 */
public class MahasiswaDAO {

    public void insert(Mahasiswa mahasiswa) {
        String query = "INSERT INTO mahasiswa (nim, nama, gender, alamat, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mahasiswa.getNim());
            stmt.setString(2, mahasiswa.getNama());
            stmt.setString(3, toGenderEnum(mahasiswa.getJenisKelamin()));
            stmt.setString(4, mahasiswa.getAlamat());
            stmt.setString(5, mahasiswa.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Mahasiswa mahasiswa) {
        String query = "UPDATE mahasiswa SET nama=?, gender=?, alamat=? WHERE nim=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mahasiswa.getNama());
            stmt.setString(2, toGenderEnum(mahasiswa.getJenisKelamin()));
            stmt.setString(3, mahasiswa.getAlamat());
            stmt.setString(4, mahasiswa.getNim());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String nim) {
        String query = "DELETE FROM mahasiswa WHERE nim=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nim);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Mahasiswa getByNim(String nim) {
        String query = "SELECT * FROM mahasiswa WHERE nim=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nim);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Mahasiswa> getAll() {
        List<Mahasiswa> list = new ArrayList<>();
        String query = "SELECT * FROM mahasiswa ORDER BY nama";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Mahasiswa> getByKelas(String idKelas) {
        // Tabel mahasiswa tidak memiliki kolom id_kelas di DB saat ini
        // Kembalikan semua, filtering dilakukan di controller jika diperlukan
        return getAll();
    }

    /** Konversi tampilan UI ("Laki-laki"/"Perempuan") ke enum DB ('L'/'P') */
    private String toGenderEnum(String jenisKelamin) {
        if (jenisKelamin == null) return "L";
        return jenisKelamin.equalsIgnoreCase("Perempuan") ? "P" : "L";
    }

    /** Konversi enum DB ('L'/'P') ke tampilan UI */
    private String fromGenderEnum(String gender) {
        return "P".equalsIgnoreCase(gender) ? "Perempuan" : "Laki-laki";
    }

    /** Memetakan satu baris ResultSet ke objek Mahasiswa */
    private Mahasiswa mapRow(ResultSet rs) throws SQLException {
        return new Mahasiswa(
                rs.getString("nim"),
                rs.getString("nama"),
                fromGenderEnum(rs.getString("gender")),
                rs.getString("alamat"),
                "",    // no_telp tidak ada di DB
                "",    // email tidak ada di DB
                "",    // id_kelas tidak ada di DB
                rs.getString("password")
        );
    }
}
