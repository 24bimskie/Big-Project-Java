package model;

/**
 * Model untuk entitas Jadwal.
 * Digunakan pada use case: Input Data Jadwal, Lihat Jadwal.
 * Menghubungkan Dosen, Mata Kuliah, Kelas, dan waktu perkuliahan.
 */
public class Jadwal {

    private String idJadwal;
    private String kodeMk;
    private String nidn; // ID Dosen
    private String idKelas;
    private String hari;
    private String jamMulai;
    private String jamSelesai;
    private String ruangan;
    private String tahunAkademik;
    private String semester; // "Ganjil" / "Genap"

    public Jadwal() {}

    public Jadwal(String idJadwal, String kodeMk, String nidn, String idKelas,
                  String hari, String jamMulai, String jamSelesai,
                  String ruangan, String tahunAkademik, String semester) {
        this.idJadwal = idJadwal;
        this.kodeMk = kodeMk;
        this.nidn = nidn;
        this.idKelas = idKelas;
        this.hari = hari;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.ruangan = ruangan;
        this.tahunAkademik = tahunAkademik;
        this.semester = semester;
    }

    // Getter dan Setter
    public String getIdJadwal() { return idJadwal; }
    public void setIdJadwal(String idJadwal) { this.idJadwal = idJadwal; }

    public String getKodeMk() { return kodeMk; }
    public void setKodeMk(String kodeMk) { this.kodeMk = kodeMk; }

    public String getNidn() { return nidn; }
    public void setNidn(String nidn) { this.nidn = nidn; }

    public String getIdKelas() { return idKelas; }
    public void setIdKelas(String idKelas) { this.idKelas = idKelas; }

    public String getHari() { return hari; }
    public void setHari(String hari) { this.hari = hari; }

    public String getJamMulai() { return jamMulai; }
    public void setJamMulai(String jamMulai) { this.jamMulai = jamMulai; }

    public String getJamSelesai() { return jamSelesai; }
    public void setJamSelesai(String jamSelesai) { this.jamSelesai = jamSelesai; }

    public String getRuangan() { return ruangan; }
    public void setRuangan(String ruangan) { this.ruangan = ruangan; }

    public String getTahunAkademik() { return tahunAkademik; }
    public void setTahunAkademik(String tahunAkademik) { this.tahunAkademik = tahunAkademik; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
