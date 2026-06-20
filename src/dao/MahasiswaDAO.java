package dao;

import config.DatabaseConnection;
import model.Mahasiswa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Mahasiswa.
 * Kolom DB: id, nim, nama, gender, alamat, password, kelas, prodi
 */
public class MahasiswaDAO {

    public void insert(Mahasiswa m) {
        String sql = "INSERT INTO mahasiswa (nim, nama, gender, alamat, password, kelas, prodi) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getNim());
            stmt.setString(2, m.getNama());
            stmt.setString(3, m.getJenisKelamin());
            stmt.setString(4, m.getAlamat());
            stmt.setString(5, m.getPassword());
            stmt.setString(6, m.getKelas());
            stmt.setString(7, m.getProdi());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Mahasiswa m) {
        String sql = "UPDATE mahasiswa SET nama=?, gender=?, alamat=?, password=?, kelas=?, prodi=? WHERE nim=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getNama());
            stmt.setString(2, m.getJenisKelamin());
            stmt.setString(3, m.getAlamat());
            stmt.setString(4, m.getPassword());
            stmt.setString(5, m.getKelas());
            stmt.setString(6, m.getProdi());
            stmt.setString(7, m.getNim());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String nim) {
        String sql = "DELETE FROM mahasiswa WHERE nim=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nim);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Mahasiswa getByNim(String nim) {
        String sql = "SELECT * FROM mahasiswa WHERE nim=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nim);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Mahasiswa> getAll() {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Mahasiswa map(ResultSet rs) throws SQLException {
        return new Mahasiswa(
                rs.getString("nim"),
                rs.getString("nama"),
                rs.getString("gender"),
                rs.getString("alamat"),
                rs.getString("kelas"),
                rs.getString("prodi"),
                rs.getString("password")
        );
    }
}
