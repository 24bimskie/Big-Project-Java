package dao;

import config.DatabaseConnection;
import model.Dosen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Dosen.
 * Skema tabel: dosen(id, nip, nama, gender, alamat, password)
 */
public class DosenDAO {

    public void insert(Dosen d) {
        String sql = "INSERT INTO dosen (nip, nama, gender, alamat, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Memperbaiki pemanggilan variabel dari 'dosen' menjadi 'd' sesuai parameter
            // method
            stmt.setString(1, d.getNip());
            stmt.setString(2, d.getNama());
            stmt.setString(3, toGenderEnum(d.getJenisKelamin()));
            stmt.setString(4, d.getAlamat());
            stmt.setString(5, d.getPassword());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Dosen d) {
        String sql = "UPDATE dosen SET nama=?, gender=?, alamat=?, password=? WHERE nip=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getNama());
            stmt.setString(2, toGenderEnum(d.getJenisKelamin()));
            stmt.setString(3, d.getAlamat());
            stmt.setString(4, d.getPassword());
            stmt.setString(5, d.getNip());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String nip) {
        String sql = "DELETE FROM dosen WHERE nip=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nip);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dosen getByNip(String nip) {
        String sql = "SELECT * FROM dosen WHERE nip=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nip);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Dosen> getAll() {
        List<Dosen> list = new ArrayList<>();
        String sql = "SELECT * FROM dosen ORDER BY nama";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Konversi tampilan UI ("Laki-laki"/"Perempuan") ke enum DB ('L'/'P') */
    private String toGenderEnum(String jenisKelamin) {
        if (jenisKelamin == null)
            return "L";
        return jenisKelamin.equalsIgnoreCase("Perempuan") || jenisKelamin.equalsIgnoreCase("P") ? "P" : "L";
    }

    /** Konversi enum DB ('L'/'P') ke tampilan UI */
    private String fromGenderEnum(String gender) {
        return "P".equalsIgnoreCase(gender) ? "Perempuan" : "Laki-laki";
    }

    /** Memetakan satu baris ResultSet ke objek Dosen */
    private Dosen mapRow(ResultSet rs) throws SQLException {
        return new Dosen(
                rs.getString("nip"),
                rs.getString("nama"),
                fromGenderEnum(rs.getString("gender")),
                rs.getString("alamat"),
                rs.getString("password"));
    }
}