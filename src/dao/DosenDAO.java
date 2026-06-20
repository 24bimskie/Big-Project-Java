package dao;

import config.DatabaseConnection;
import model.Dosen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Dosen.
 * Menggunakan skema tabel: dosen(id, nip, nama, gender, alamat, password)
 * gender: enum 'L' atau 'P'
 */
public class DosenDAO {

    public void insert(Dosen dosen) {
        String query = "INSERT INTO dosen (nip, nama, gender, alamat, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dosen.getNidn());   // nidn di model → nip di DB
            stmt.setString(2, dosen.getNama());
            stmt.setString(3, toGenderEnum(dosen.getJenisKelamin()));
            stmt.setString(4, dosen.getAlamat());
            stmt.setString(5, dosen.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Dosen dosen) {
        String query = "UPDATE dosen SET nama=?, gender=?, alamat=? WHERE nip=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dosen.getNama());
            stmt.setString(2, toGenderEnum(dosen.getJenisKelamin()));
            stmt.setString(3, dosen.getAlamat());
            stmt.setString(4, dosen.getNidn());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String nip) {
        String query = "DELETE FROM dosen WHERE nip=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nip);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dosen getByNidn(String nip) {
        String query = "SELECT * FROM dosen WHERE nip=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nip);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Dosen> getAll() {
        List<Dosen> list = new ArrayList<>();
        String query = "SELECT * FROM dosen ORDER BY nama";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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

    /** Memetakan satu baris ResultSet ke objek Dosen */
    private Dosen mapRow(ResultSet rs) throws SQLException {
        return new Dosen(
                rs.getString("nip"),           // → nidn
                rs.getString("nama"),
                fromGenderEnum(rs.getString("gender")),
                rs.getString("alamat"),
                "",                            // no_telp tidak ada di DB
                "",                            // email tidak ada di DB
                rs.getString("password")
        );
    }
}
