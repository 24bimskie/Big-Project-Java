package dao;

import config.DatabaseConnection;
import model.Absensi;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Absensi.
 * Mendukung use case: Mulai Absen, Data Absen, Rekap Absen.
 *
 * Skema tabel absensi (sesuai database/migrate_absensi.sql + migrate_absensi_jadwal_id.sql):
 *   id_absensi INT AUTO_INCREMENT PK,
 *   jadwal_id  INT NULL  (FK ke jadwal.id, ditambahkan via migrate_absensi_jadwal_id.sql),
 *   tanggal DATE,
 *   hari_jam VARCHAR(50),
 *   mata_kuliah VARCHAR(100),
 *   kelas VARCHAR(20),
 *   nim VARCHAR(20),
 *   nama_lengkap VARCHAR(150),
 *   kehadiran ENUM('Hadir','Sakit','Izin','Alfa'),
 *   keterangan TEXT
 *
 * Catatan penting:
 *   - jadwal_id adalah kunci utama untuk menghubungkan satu baris absensi
 *     ke jadwal tertentu. Mulai Absen mengisi jadwal_id saat insert,
 *     dan Rekap Absen memfilter berdasarkan jadwal_id ini (bukan
 *     mencocokkan string nama kelas/mata kuliah, yang rapuh).
 *   - nim di sini adalah NIM mahasiswa asli (varchar), BUKAN mahasiswa.id.
 */
public class AbsensiDAO {

    /**
     * Insert satu record absensi baru untuk satu mahasiswa pada satu jadwal & tanggal.
     */
    public void insert(Absensi absensi) {
        String sql = "INSERT INTO absensi (jadwal_id, tanggal, hari_jam, mata_kuliah, kelas, nim, nama_lengkap, kehadiran, keterangan) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parseId(absensi.getIdJadwal()));
            stmt.setDate(2, Date.valueOf(absensi.getTanggal()));
            stmt.setString(3, absensi.getHariJam());
            stmt.setString(4, absensi.getMataKuliah());
            stmt.setString(5, absensi.getKelas());
            stmt.setString(6, absensi.getNim());
            stmt.setString(7, absensi.getNamaLengkap());
            stmt.setString(8, absensi.getKehadiran());
            stmt.setString(9, absensi.getKeterangan());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update kehadiran dan keterangan absensi berdasarkan id_absensi.
     */
    public void update(Absensi absensi) {
        String sql = "UPDATE absensi SET kehadiran=?, keterangan=? WHERE id_absensi=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, absensi.getKehadiran());
            stmt.setString(2, absensi.getKeterangan());
            stmt.setInt(3, parseId(absensi.getIdAbsensi()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete data absensi berdasarkan id_absensi.
     */
    public void delete(String idAbsensi) {
        String sql = "DELETE FROM absensi WHERE id_absensi=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, parseId(idAbsensi));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ambil daftar absensi berdasarkan jadwal_id dan tanggal tertentu.
     * Digunakan pada halaman Mulai Absen untuk mengecek apakah absensi
     * pada tanggal tersebut sudah pernah diisi (agar bisa di-update, bukan duplikat).
     */
    public List<Absensi> getByJadwalAndTanggal(String idJadwal, LocalDate tanggal) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE jadwal_id=? AND tanggal=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parseId(idJadwal));
            stmt.setDate(2, Date.valueOf(tanggal));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Ambil seluruh record rekap absensi untuk SATU jadwal tertentu,
     * diurutkan per tanggal lalu per nim. Ini adalah method yang dipakai
     * oleh RekapAbsenController, dan jadwal_id adalah filter satu-satunya
     * yang dibutuhkan -- tidak perlu lagi mencocokkan nama kelas/mata kuliah
     * secara manual di sisi Java.
     */
    public List<Absensi> getRekapByJadwal(String idJadwal) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE jadwal_id=? ORDER BY tanggal, nim";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parseId(idJadwal));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Ambil semua tanggal unik di mana absensi sudah dilakukan untuk jadwal tertentu.
     */
    public List<LocalDate> getTanggalByJadwal(String idJadwal) {
        List<LocalDate> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT tanggal FROM absensi WHERE jadwal_id=? ORDER BY tanggal";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parseId(idJadwal));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dates.add(rs.getDate("tanggal").toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }

    /**
     * Ambil seluruh riwayat absensi seorang mahasiswa (berdasarkan NIM) di semua jadwal.
     */
    public List<Absensi> getByMahasiswa(String nim) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE nim=? ORDER BY tanggal";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nim);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Ambil absensi satu mahasiswa pada satu jadwal tertentu (semua tanggal).
     */
    public List<Absensi> getByJadwalAndMahasiswa(String idJadwal, String nim) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE jadwal_id=? AND nim=? ORDER BY tanggal";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parseId(idJadwal));
            stmt.setString(2, nim);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Memetakan satu baris ResultSet ke objek Absensi sesuai skema absensi saat ini. */
    private Absensi mapRow(ResultSet rs) throws SQLException {
        Absensi a = new Absensi();
        a.setIdAbsensi(String.valueOf(rs.getInt("id_absensi")));

        int jadwalId = rs.getInt("jadwal_id");
        a.setIdJadwal(rs.wasNull() ? null : String.valueOf(jadwalId));

        Date tgl = rs.getDate("tanggal");
        a.setTanggal(tgl == null ? null : tgl.toLocalDate());

        a.setHariJam(rs.getString("hari_jam"));
        a.setMataKuliah(rs.getString("mata_kuliah"));
        a.setKelas(rs.getString("kelas"));
        a.setNim(rs.getString("nim"));
        a.setNamaLengkap(rs.getString("nama_lengkap"));
        a.setKehadiran(rs.getString("kehadiran"));
        a.setStatus(rs.getString("kehadiran"));
        a.setKeterangan(rs.getString("keterangan"));
        return a;
    }

    /** Konversi string id ke integer dengan aman */
    private int parseId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}