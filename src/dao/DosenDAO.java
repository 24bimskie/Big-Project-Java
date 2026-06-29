package dao;

import config.DatabaseConnection;
import model.Dosen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DosenDAO {

    public void insert(Dosen d) {
        String sql = "INSERT INTO dosen (nidn, nama_lengkap, email, fakultas, foto_profil) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getNidn());
            stmt.setString(2, d.getNamaLengkap());
            stmt.setString(3, d.getEmail());
            stmt.setString(4, d.getFakultas());
            stmt.setString(5, d.getFotoProfil());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Dosen d) {
        String sql = "UPDATE dosen SET nama_lengkap=?, email=?, fakultas=?, foto_profil=? WHERE nidn=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getNamaLengkap());
            stmt.setString(2, d.getEmail());
            stmt.setString(3, d.getFakultas());
            stmt.setString(4, d.getFotoProfil());
            stmt.setString(5, d.getNidn());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String nidn) {
        String sql = "DELETE FROM dosen WHERE nidn=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nidn);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dosen getByNidn(String nidn) {
        String sql = "SELECT * FROM dosen WHERE nidn=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nidn);

            // ResultSet dimasukkan ke try-with-resources agar auto-close
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Backward compatibility untuk controller lama yang masih mencari berdasarkan
    // NIP
    public Dosen getByNip(String nip) {
        return getByNidn(nip);
    }

    /**
     * Cari dosen berdasarkan nama_lengkap.
     * Digunakan untuk mencocokkan username login dengan data profil dosen.
     */
    public Dosen getByNamaLengkap(String namaLengkap) {
        String sql = "SELECT * FROM dosen WHERE nama_lengkap=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, namaLengkap);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return map(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Dosen> getAll() {
        List<Dosen> list = new ArrayList<>();
        String sql = "SELECT * FROM dosen ORDER BY nama_lengkap";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Dosen map(ResultSet rs) throws SQLException {
        Dosen dosen = new Dosen(
                rs.getString("nidn"),
                rs.getString("nama_lengkap"),
                rs.getString("email"),
                rs.getString("fakultas"));
        dosen.setFotoProfil(rs.getString("foto_profil"));
        return dosen;
    }
}