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
 * Skema tabel absensi:
 *   id INT AUTO_INCREMENT, tanggal DATE, jadwal_id INT FK,
 *   peran ENUM('Dosen','Mahasiswa'), aktor_id INT, 
 *   status ENUM('Hadir','Izin','Sakit','Alpha'), keterangan TEXT
 *
 * Catatan: aktor_id merujuk ke mahasiswa.id (bukan nim).
 *          Field nim di model Absensi menyimpan mahasiswa.id sebagai String.
 */
public class AbsensiDAO {

    /**
     * Insert satu record absensi baru.
     * absensi.nim berisi String dari mahasiswa.id (aktor_id).
     */
    public void insert(Absensi absensi) {
        String sql = "INSERT INTO absensi (tanggal, jadwal_id, peran, aktor_id, status, keterangan) "
                   + "VALUES (?, ?, 'Mahasiswa', ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(absensi.getTanggal()));
            stmt.setInt(2, parseId(absensi.getIdJadwal()));
            stmt.setInt(3, parseId(absensi.getNim())); // nim = mahasiswa.id as string
            stmt.setString(4, absensi.getStatus());
            stmt.setString(5, absensi.getKeterangan());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update status dan keterangan absensi berdasarkan id.
     */
    public void update(Absensi absensi) {
        String sql = "UPDATE absensi SET status=?, keterangan=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, absensi.getStatus());
            stmt.setString(2, absensi.getKeterangan());
            stmt.setInt(3, parseId(absensi.getIdAbsensi()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete data absensi berdasarkan id.
     */
    public void delete(String idAbsensi) {
        String sql = "DELETE FROM absensi WHERE id=?";
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
     * Digunakan pada halaman Mulai Absen untuk mengecek apakah absensi sudah diisi.
     */
    public List<Absensi> getByJadwalAndTanggal(String idJadwal, LocalDate tanggal) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE jadwal_id=? AND tanggal=? AND peran='Mahasiswa'";
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
     * Ambil semua record absensi untuk jadwal tertentu (semua tanggal).
     * Digunakan untuk Rekap Absen.
     */
    public List<Absensi> getRekapByJadwal(String idJadwal) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE jadwal_id=? AND peran='Mahasiswa' ORDER BY tanggal, aktor_id";
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
        String sql = "SELECT DISTINCT tanggal FROM absensi WHERE jadwal_id=? AND peran='Mahasiswa' ORDER BY tanggal";
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
     * Ambil absensi berdasarkan mahasiswa (nim sebagai mahasiswa.id string).
     */
    public List<Absensi> getByMahasiswa(String idMahasiswa) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE aktor_id=? AND peran='Mahasiswa' ORDER BY tanggal";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parseId(idMahasiswa));
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
     * Ambil absensi per mahasiswa per jadwal.
     */
    public List<Absensi> getByJadwalAndMahasiswa(String idJadwal, String idMahasiswa) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE jadwal_id=? AND aktor_id=? AND peran='Mahasiswa' ORDER BY tanggal";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parseId(idJadwal));
            stmt.setInt(2, parseId(idMahasiswa));
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

    /** Memetakan satu baris ResultSet ke objek Absensi */
    private Absensi mapRow(ResultSet rs) throws SQLException {
        Absensi a = new Absensi();
        a.setIdAbsensi(String.valueOf(rs.getInt("id")));
        a.setIdJadwal(String.valueOf(rs.getInt("jadwal_id")));
        a.setNim(String.valueOf(rs.getInt("aktor_id"))); // aktor_id → nim (as mahasiswa.id string)
        a.setTanggal(rs.getDate("tanggal").toLocalDate());
        a.setStatus(rs.getString("status"));
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