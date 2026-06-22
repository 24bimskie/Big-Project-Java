package dao;

import config.DatabaseConnection;
import model.Jadwal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalDAO {

    private String lastError;

    public String getLastError() {
        return lastError;
    }

    public boolean insert(Jadwal jadwal) {
        lastError = null;
        String query = """
                INSERT INTO jadwal (hari, jam_mulai, jam_selesai, ruangan, tahun_akademik, semester, kelas_id, dosen_id, matkul_id)
                VALUES (?, ?, ?, ?, ?, ?,
                        ?,
                        (SELECT id FROM dosen WHERE nidn = ?),
                        (SELECT id FROM mata_kuliah WHERE kode_mk = ?))
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, jadwal.getHari());
            stmt.setString(2, jadwal.getJamMulai());
            stmt.setString(3, jadwal.getJamSelesai());
            stmt.setString(4, jadwal.getRuangan());
            stmt.setString(5, jadwal.getTahunAkademik());
            stmt.setString(6, jadwal.getSemester());
            stmt.setInt(7, parseId(jadwal.getIdKelas()));
            stmt.setString(8, jadwal.getNidn());
            stmt.setString(9, jadwal.getKodeMk());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Jadwal jadwal) {
        lastError = null;
        String query = """
                UPDATE jadwal SET
                    hari = ?, jam_mulai = ?, jam_selesai = ?, ruangan = ?, tahun_akademik = ?, semester = ?,
                    kelas_id = ?,
                    dosen_id = (SELECT id FROM dosen WHERE nidn = ?),
                    matkul_id = (SELECT id FROM mata_kuliah WHERE kode_mk = ?)
                WHERE id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, jadwal.getHari());
            stmt.setString(2, jadwal.getJamMulai());
            stmt.setString(3, jadwal.getJamSelesai());
            stmt.setString(4, jadwal.getRuangan());
            stmt.setString(5, jadwal.getTahunAkademik());
            stmt.setString(6, jadwal.getSemester());
            stmt.setInt(7, parseId(jadwal.getIdKelas()));
            stmt.setString(8, jadwal.getNidn());
            stmt.setString(9, jadwal.getKodeMk());
            stmt.setInt(10, parseId(jadwal.getIdJadwal()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String idJadwal) {
        lastError = null;
        String query = "DELETE FROM jadwal WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(idJadwal));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public Jadwal getById(String idJadwal) {
        String query = """
                SELECT j.id, j.hari, j.jam_mulai, j.jam_selesai, j.ruangan, j.tahun_akademik, j.semester,
                       d.nidn, d.nama_lengkap,
                       k.id AS kelas_id, k.kelas AS nama_kelas,
                       m.kode_mk, m.nama_mk
                FROM jadwal j
                LEFT JOIN dosen d ON j.dosen_id = d.id
                LEFT JOIN kelas k ON j.kelas_id = k.id
                LEFT JOIN mata_kuliah m ON j.matkul_id = m.id
                WHERE j.id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(idJadwal));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Jadwal> getAll() {
        List<Jadwal> list = new ArrayList<>();
        String query = """
                SELECT j.id, j.hari, j.jam_mulai, j.jam_selesai, j.ruangan, j.tahun_akademik, j.semester,
                       d.nidn, d.nama_lengkap,
                       k.id AS kelas_id, k.kelas AS nama_kelas,
                       m.kode_mk, m.nama_mk
                FROM jadwal j
                LEFT JOIN dosen d ON j.dosen_id = d.id
                LEFT JOIN kelas k ON j.kelas_id = k.id
                LEFT JOIN mata_kuliah m ON j.matkul_id = m.id
                ORDER BY j.hari, j.jam_mulai
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
        }
        return list;
    }

    public List<Jadwal> getByDosen(String nidn) {
        List<Jadwal> list = new ArrayList<>();
        String query = """
                SELECT j.id, j.hari, j.jam_mulai, j.jam_selesai, j.ruangan, j.tahun_akademik, j.semester,
                       d.nidn, d.nama_lengkap,
                       k.id AS kelas_id, k.kelas AS nama_kelas,
                       m.kode_mk, m.nama_mk
                FROM jadwal j
                LEFT JOIN dosen d ON j.dosen_id = d.id
                LEFT JOIN kelas k ON j.kelas_id = k.id
                LEFT JOIN mata_kuliah m ON j.matkul_id = m.id
                WHERE d.nidn = ?
                ORDER BY j.hari, j.jam_mulai
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nidn);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Jadwal> getByKelas(String kelasId) {
        List<Jadwal> list = new ArrayList<>();
        String query = """
                SELECT j.id, j.hari, j.jam_mulai, j.jam_selesai, j.ruangan, j.tahun_akademik, j.semester,
                       d.nidn, d.nama_lengkap,
                       k.id AS kelas_id, k.kelas AS nama_kelas,
                       m.kode_mk, m.nama_mk
                FROM jadwal j
                LEFT JOIN dosen d ON j.dosen_id = d.id
                LEFT JOIN kelas k ON j.kelas_id = k.id
                LEFT JOIN mata_kuliah m ON j.matkul_id = m.id
                WHERE j.kelas_id = ?
                ORDER BY j.hari, j.jam_mulai
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parseId(kelasId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Jadwal mapRow(ResultSet rs) throws SQLException {
        return new Jadwal(
                String.valueOf(rs.getInt("id")),
                rs.getString("kode_mk"),
                rs.getString("nidn"),
                String.valueOf(rs.getInt("kelas_id")),
                rs.getString("hari"),
                rs.getString("jam_mulai"),
                rs.getString("jam_selesai"),
                rs.getString("ruangan"),
                rs.getString("tahun_akademik"),
                rs.getString("semester"));
    }

    private int parseId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}