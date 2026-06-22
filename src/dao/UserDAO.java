package dao;

import config.DatabaseConnection;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO untuk operasi autentikasi user.
 * Semua password disimpan HANYA di tabel user.
 * Mendukung proses login untuk semua role (Admin, Dosen, Mahasiswa).
 */
public class UserDAO {

    public User login(String username, String password) {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("level"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert user baru ke tabel user.
     * Jika role Mahasiswa, otomatis buat record di tabel mahasiswa (tanpa password).
     * Jika role Dosen, otomatis buat record di tabel dosen (tanpa password).
     */
    public void insert(User user) throws SQLException {
        String query = "INSERT INTO user (username, password, level) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.executeUpdate();
        }

        // Otomatis tambah ke tabel mahasiswa atau dosen (tanpa password)
        if ("Mahasiswa".equalsIgnoreCase(user.getRole())) {
            String queryMhs = "INSERT INTO mahasiswa (nim, nama, gender) VALUES (?, ?, 'L')";
            try (PreparedStatement stmtMhs = conn.prepareStatement(queryMhs)) {
                stmtMhs.setString(1, user.getUsername());
                stmtMhs.setString(2, user.getUsername()); // Default nama
                stmtMhs.executeUpdate();
            }
        } else if ("Dosen".equalsIgnoreCase(user.getRole())) {
            String queryDsn = "INSERT INTO dosen (nidn, nama_lengkap) VALUES (?, ?)";
            try (PreparedStatement stmtDsn = conn.prepareStatement(queryDsn)) {
                stmtDsn.setString(1, user.getUsername());
                stmtDsn.setString(2, user.getUsername()); // Default nama
                stmtDsn.executeUpdate();
            }
        }
    }

    /**
     * Insert HANYA ke tabel user (tanpa auto-create di tabel mahasiswa/dosen).
     * Digunakan oleh admin controller yang sudah meng-handle insert profil sendiri.
     */
    public void insertUserOnly(String username, String password, String level) throws SQLException {
        String query = "INSERT INTO user (username, password, level) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, level);
            stmt.executeUpdate();
        }
    }

    /**
     * Update password user berdasarkan username.
     */
    public void updatePassword(String username, String newPassword) {
        String query = "UPDATE user SET password = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cek apakah username sudah ada di tabel user.
     */
    public boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getById(String userId) {
        String query = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("level"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Menyimpan data pendaftaran Mahasiswa baru.
     * Menggunakan transaksi (commit/rollback) agar tabel user dan tabel mahasiswa tetap konsisten.
     */
    public void registerMahasiswaTransaction(model.Mahasiswa mhs, String password) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            // 1. Insert ke tabel user
            String queryUser = "INSERT INTO user (username, password, level) VALUES (?, ?, 'Mahasiswa')";
            try (PreparedStatement stmtUser = conn.prepareStatement(queryUser)) {
                stmtUser.setString(1, mhs.getNim());
                stmtUser.setString(2, password);
                stmtUser.executeUpdate();
            }

            // 2. Insert ke tabel mahasiswa
            String queryMhs = "INSERT INTO mahasiswa (nim, nama, gender, alamat, kelas, prodi) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtMhs = conn.prepareStatement(queryMhs)) {
                stmtMhs.setString(1, mhs.getNim());
                stmtMhs.setString(2, mhs.getNama());
                stmtMhs.setString(3, mhs.getJenisKelamin());
                stmtMhs.setString(4, mhs.getAlamat());
                stmtMhs.setString(5, mhs.getKelas());
                stmtMhs.setString(6, mhs.getProdi());
                stmtMhs.executeUpdate();
            }

            conn.commit(); // Jika sukses semua, commit transaksi
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Jika gagal, batalkan semua perubahan
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Lempar exception ke controller untuk ditampilkan ke user
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Kembalikan state default
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
