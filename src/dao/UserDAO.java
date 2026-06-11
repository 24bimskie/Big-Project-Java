package dao;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO untuk operasi autentikasi user.
 * Mendukung proses login untuk semua role (Admin, Dosen, Mahasiswa).
 */
public class UserDAO {

    public User login(String username, String password) {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection conn = config.DatabaseConnection.getConnection();
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

    public void insert(User user) throws SQLException {
        String query = "INSERT INTO user (username, password, level) VALUES (?, ?, ?)";
        Connection conn = config.DatabaseConnection.getConnection();
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.executeUpdate();
        }
        
        // Otomatis tambah ke tabel mahasiswa atau dosen
        if ("Mahasiswa".equalsIgnoreCase(user.getRole())) {
            String queryMhs = "INSERT INTO mahasiswa (nim, nama, gender, password) VALUES (?, ?, 'L', ?)";
            try (PreparedStatement stmtMhs = conn.prepareStatement(queryMhs)) {
                stmtMhs.setString(1, user.getUsername());
                stmtMhs.setString(2, user.getUsername()); // Default nama
                stmtMhs.setString(3, user.getPassword());
                stmtMhs.executeUpdate();
            }
        } else if ("Dosen".equalsIgnoreCase(user.getRole())) {
            String queryDsn = "INSERT INTO dosen (nip, nama, gender, password) VALUES (?, ?, 'L', ?)";
            try (PreparedStatement stmtDsn = conn.prepareStatement(queryDsn)) {
                stmtDsn.setString(1, user.getUsername());
                stmtDsn.setString(2, user.getUsername()); // Default nama
                stmtDsn.setString(3, user.getPassword());
                stmtDsn.executeUpdate();
            }
        }
    }

    public void updatePassword(String userId, String newPassword) {
        // UPDATE password user
    }

    public User getById(String userId) {
        // SELECT user berdasarkan ID
        return null;
    }
}
