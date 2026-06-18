package model;

/**
 * Model untuk entitas Mahasiswa.
 * Digunakan pada use case: Input Data Mahasiswa, Lihat Data Mahasiswa, Data Absen.
 */
public class Mahasiswa {

    private String nim;
    private String nama;
    private String jenisKelamin; // kolom: gender
    private String alamat;
    private String kelas;        // kolom: kelas (baru)
    private String prodi;        // kolom: prodi (baru)
    private String password;

    public Mahasiswa() {}

    public Mahasiswa(String nim, String nama, String jenisKelamin, String alamat,
                     String kelas, String prodi, String password) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.kelas = kelas;
        this.prodi = prodi;
        this.password = password;
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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
