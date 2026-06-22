package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Konfigurasi koneksi database untuk Sistem Absensi Mahasiswa.
 * Menggunakan MySQL sebagai database.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/sam?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver tidak ditemukan. Pastikan driver connector MySQL tersedia.",
                        e);
            } catch (SQLException e) {
                throw new SQLException(
                        "Gagal terhubung ke database. Pastikan MySQL sedang berjalan dan konfigurasi user/password benar.\n"
                                + e.getMessage(),
                        e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
