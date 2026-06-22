package util;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;

public class PopulateDatabase {
    public static void main(String[] args) {
        System.out.println("⏳ Starting database populator...");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Turn off foreign key checks temporarily to avoid constraint issues during truncation/inserts
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            
            // Clean up existing data to avoid duplicate key issues
            stmt.execute("TRUNCATE TABLE absensi;");
            stmt.execute("TRUNCATE TABLE jadwal;");
            stmt.execute("TRUNCATE TABLE kelas;");
            stmt.execute("TRUNCATE TABLE mahasiswa;");
            stmt.execute("TRUNCATE TABLE mata_kuliah;");
            stmt.execute("DELETE FROM dosen WHERE id = 32;");
            stmt.execute("DELETE FROM user WHERE id = 32;");
            
            // 1. Insert User 'qq' as Dosen
            stmt.execute("INSERT INTO user (id, username, password, level) VALUES (32, 'qq', '11', 'Dosen');");
            
            // 2. Insert Dosen profile with matching ID 32
            stmt.execute("INSERT INTO dosen (id, nidn, nama_lengkap, email, fakultas) " +
                         "VALUES (32, 'qq', 'Dosen QQ', 'qq@dosen.ac.id', 'Teknik Informatika');");
            
            // 3. Insert Classes
            stmt.execute("INSERT INTO kelas (id, Prodi, kelas) VALUES (1, 'Teknik Informatika', 'A');");
            stmt.execute("INSERT INTO kelas (id, Prodi, kelas) VALUES (2, 'Sistem Informasi', 'B');");
            
            // 4. Insert Mata Kuliah
            stmt.execute("INSERT INTO mata_kuliah (id, Mata_Kuliah) VALUES (1, 'Pemrograman Berorientasi Objek');");
            stmt.execute("INSERT INTO mata_kuliah (id, Mata_Kuliah) VALUES (2, 'Basis Data Lanjut');");
            
            // 5. Insert Schedules for Dosen ID 32
            stmt.execute("INSERT INTO jadwal (id, hari, jam, kelas_id, dosen_id, matkul_id) " +
                         "VALUES (1, 'Senin', '08:00:00', 1, 32, 1);");
            stmt.execute("INSERT INTO jadwal (id, hari, jam, kelas_id, dosen_id, matkul_id) " +
                         "VALUES (2, 'Rabu', '10:00:00', 2, 32, 2);");
            
            // 6. Insert Students for Class 1 (Teknik Informatika, A)
            stmt.execute("INSERT INTO mahasiswa (id, nim, nama, gender, alamat, kelas, prodi) " +
                         "VALUES (101, '24010101', 'Andi Wijaya', 'L', 'Semarang', 'A', 'Teknik Informatika');");
            stmt.execute("INSERT INTO mahasiswa (id, nim, nama, gender, alamat, kelas, prodi) " +
                         "VALUES (102, '24010102', 'Budi Santoso', 'L', 'Solo', 'A', 'Teknik Informatika');");
            stmt.execute("INSERT INTO mahasiswa (id, nim, nama, gender, alamat, kelas, prodi) " +
                         "VALUES (103, '24010103', 'Citra Lestari', 'P', 'Yogyakarta', 'A', 'Teknik Informatika');");
            
            // 7. Insert Students for Class 2 (Sistem Informasi, B)
            stmt.execute("INSERT INTO mahasiswa (id, nim, nama, gender, alamat, kelas, prodi) " +
                         "VALUES (201, '24020201', 'Dedi Kurniawan', 'L', 'Bandung', 'B', 'Sistem Informasi');");
            stmt.execute("INSERT INTO mahasiswa (id, nim, nama, gender, alamat, kelas, prodi) " +
                         "VALUES (202, '24020202', 'Elisa Putri', 'P', 'Jakarta', 'B', 'Sistem Informasi');");
            
            // Re-enable foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            
            System.out.println("✅ Database populated successfully with test data!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to populate database: " + e.getMessage());
        }
    }
}
