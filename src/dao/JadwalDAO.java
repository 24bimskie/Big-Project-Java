package dao;

import config.DatabaseConnection;
import model.Jadwal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk operasi CRUD data Jadwal.
 * Menggunakan skema tabel:
 *   jadwal(id INT AUTO_INCREMENT, hari VARCHAR, jam TIME, kelas_id INT FK, dosen_id INT FK, matkul_id INT FK)
 *
 * Catatan penting:
 * - FK menggunakan id integer dari tabel dosen/kelas/mata_kuliah (bukan string NIP/nama)
 * - Hanya ada satu kolom jam (TIME), bukan jam_mulai + jam_selesai
 * - Tidak ada kolom ruangan, tahun_akademik, semester di DB saat ini
 */
public class JadwalDAO {

    public void insert(Jadwal jadwal) {
        String query = "INSERT INTO jadwal (hari, jam, kelas_id, dosen_id, matkul_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, jadwal.getHari());
            stmt.setString(2, jadwal.getJamMulai());       // jam_mulai → kolom jam
            stmt.setInt(3, parseId(jadwal.getIdKelas()));  // idKelas (string) → kelas_id (int FK)
            stmt.setInt(4, parseId(jadwal.getNidn()));     // nidn/nip (string) → dosen_id (int FK)
            stmt.setInt(5, parseId(jadwal.getKodeMk()));   // kodeMk (string) → matkul_id (int FK)
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Jadwal jadwal) {
        String query = "UPDATE jadwal SET hari=?, jam=?, kelas_id=?, dosen_id=?, matkul_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, jadwal.getHari());
            stmt.setString(2, jadwal.getJamMulai());
            stmt.setInt(3, parseId(jadwal.getIdKelas()));
            stmt.setInt(4, parseId(jadwal.getNidn()));
            stmt.setInt(5, parseId(jadwal.getKodeMk()));
            stmt.setInt(6, parseId(jadwal.getIdJadwal()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String idJadwal) {
        String query = "DELETE FROM jadwal WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(idJadwal));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Jadwal getById(String idJadwal) {
        String query = "SELECT * FROM jadwal WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(idJadwal));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Jadwal> getAll() {
        List<Jadwal> list = new ArrayList<>();
        String query = "SELECT * FROM jadwal ORDER BY hari, jam";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Jadwal> getByDosen(String dosenId) {
        List<Jadwal> list = new ArrayList<>();
        String query = "SELECT * FROM jadwal WHERE dosen_id=? ORDER BY hari, jam";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(dosenId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Jadwal> getByKelas(String kelasId) {
        List<Jadwal> list = new ArrayList<>();
        String query = "SELECT * FROM jadwal WHERE kelas_id=? ORDER BY hari, jam";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(kelasId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Memetakan satu baris ResultSet ke objek Jadwal */
    private Jadwal mapRow(ResultSet rs) throws SQLException {
        return new Jadwal(
                String.valueOf(rs.getInt("id")),          // id → idJadwal
                String.valueOf(rs.getInt("matkul_id")),   // matkul_id → kodeMk
                String.valueOf(rs.getInt("dosen_id")),    // dosen_id → nidn
                String.valueOf(rs.getInt("kelas_id")),    // kelas_id → idKelas
                rs.getString("hari"),
                rs.getString("jam"),                       // jam → jamMulai
                "",                                        // jamSelesai tidak ada di DB
                "",                                        // ruangan tidak ada di DB
                "",                                        // tahunAkademik tidak ada di DB
                ""                                         // semester tidak ada di DB
        );
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
