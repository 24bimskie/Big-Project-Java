package model;

/**
 * Model untuk entitas Kelas.
 * Digunakan pada use case: Input Kelas (include dari Input Data Prodi).
 */
public class Kelas {

    private String idKelas;
    private String namaKelas;
    private String idProdi;
    private String tahunAkademik;

    public Kelas() {}

    public Kelas(String idKelas, String namaKelas, String idProdi, String tahunAkademik) {
        this.idKelas = idKelas;
        this.namaKelas = namaKelas;
        this.idProdi = idProdi;
        this.tahunAkademik = tahunAkademik;
    }

    // Getter dan Setter
    public String getIdKelas() { return idKelas; }
    public void setIdKelas(String idKelas) { this.idKelas = idKelas; }

    public String getNamaKelas() { return namaKelas; }
    public void setNamaKelas(String namaKelas) { this.namaKelas = namaKelas; }

    public String getIdProdi() { return idProdi; }
    public void setIdProdi(String idProdi) { this.idProdi = idProdi; }

    public String getTahunAkademik() { return tahunAkademik; }
    public void setTahunAkademik(String tahunAkademik) { this.tahunAkademik = tahunAkademik; }
}
