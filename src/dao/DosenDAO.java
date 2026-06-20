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
 * Kolom DB: id, nip, nama, gender, alamat, password
 */
public class DosenDAO {

    public void insert(Dosen d) {
        String sql = "INSERT INTO dosen (nip, nama, gender, alamat, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getNip());
            stmt.setString(2, d.getNama());
            stmt.setString(3, d.getJenisKelamin());
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
            stmt.setString(2, d.getJenisKelamin());
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
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Dosen> getAll() {
        List<Dosen> list = new ArrayList<>();
        String sql = "SELECT * FROM dosen";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Dosen map(ResultSet rs) throws SQLException {
        return new Dosen(
                rs.getString("nip"),
                rs.getString("nama"),
                rs.getString("gender"),
                rs.getString("alamat"),
                rs.getString("password")
        );
    }
}
