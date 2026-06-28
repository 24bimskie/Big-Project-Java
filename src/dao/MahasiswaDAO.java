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
 * Kolom DB: id, nim, nama, gender, alamat, kelas, prodi
 * Password disimpan di tabel user, bukan di tabel mahasiswa.
 */
public class MahasiswaDAO {

    public void insert(Mahasiswa m) {
        String sql = "INSERT INTO mahasiswa (nim, nama, gender, alamat, kelas, prodi) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getNim());
            stmt.setString(2, m.getNama());
            stmt.setString(3, m.getJenisKelamin());
            stmt.setString(4, m.getAlamat());
            stmt.setString(5, m.getKelas());
            stmt.setString(6, m.getProdi());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Mahasiswa m) {
        String sql = "UPDATE mahasiswa SET nama=?, gender=?, alamat=?, kelas=?, prodi=? WHERE nim=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getNama());
            stmt.setString(2, m.getJenisKelamin());
            stmt.setString(3, m.getAlamat());
            stmt.setString(4, m.getKelas());
            stmt.setString(5, m.getProdi());
            stmt.setString(6, m.getNim());

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

    public List<Mahasiswa> getAll() {
        List<Mahasiswa> list = new ArrayList<>();

        String sql = "SELECT * FROM mahasiswa ORDER BY nama";

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

    public List<Mahasiswa> getByKelas(String kelas) {
        List<Mahasiswa> hasil = new ArrayList<>();

        for (Mahasiswa m : getAll()) {
            if (kelas.equalsIgnoreCase(m.getKelas())) {
                hasil.add(m);
            }
        }

        return hasil;
    }

    /**
     * Ambil semua mahasiswa yang terdaftar di kelas berdasarkan kelas.id (integer).
     * Mencocokkan mahasiswa.kelas = kelas.kelas DAN mahasiswa.prodi = kelas.Prodi.
     */
    public List<Mahasiswa> getByKelasId(int kelasId) {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT m.* FROM mahasiswa m " +
                "JOIN kelas k ON m.kelas = k.kelas AND m.prodi = k.Prodi " +
                "WHERE k.id = ? ORDER BY m.nama";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kelasId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Ambil mahasiswa.id (integer) berdasarkan NIM.
     * Digunakan untuk mapping aktor_id di tabel absensi.
     */
    public int getIdByNim(String nim) {
        String sql = "SELECT id FROM mahasiswa WHERE nim = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nim);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Ambil mahasiswa berdasarkan id (integer).
     */
    public Mahasiswa getById(int id) {
        String sql = "SELECT * FROM mahasiswa WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Mahasiswa map(ResultSet rs) throws SQLException {
        return new Mahasiswa(
                rs.getString("nim"),
                rs.getString("nama"),
                rs.getString("gender"),
                rs.getString("alamat"),
                rs.getString("kelas"),
                rs.getString("prodi"));
    }
}