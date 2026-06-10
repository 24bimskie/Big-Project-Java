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
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(User user) {
        // INSERT user baru
    }

    public void updatePassword(String userId, String newPassword) {
        // UPDATE password user
    }

    public User getById(String userId) {
        // SELECT user berdasarkan ID
        return null;
    }
}
