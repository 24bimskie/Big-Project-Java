package model;

/**
 * Model untuk entitas Mahasiswa.
 * Digunakan pada use case: Input Data Mahasiswa, Lihat Data Mahasiswa, Data Absen.
 * Kolom DB: id, nim, nama, gender, alamat, kelas, prodi
 */
public class Mahasiswa {

    private String nim;
    private String nama;
    private String jenisKelamin; // kolom: gender
    private String alamat;
    private String kelas;
    private String prodi;

    public Mahasiswa() {}

    public Mahasiswa(String nim, String nama, String jenisKelamin, String alamat,
                     String kelas, String prodi) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.kelas = kelas;
        this.prodi = prodi;
    }

    // Getter dan Setter
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }

    public String getProdi() { return prodi; }
    public void setProdi(String prodi) { this.prodi = prodi; }
}
