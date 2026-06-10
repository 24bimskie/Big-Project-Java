package model;

/**
 * Model untuk entitas Mahasiswa.
 * Digunakan pada use case: Input Data Mahasiswa, Lihat Data Mahasiswa, Data Absen.
 */
public class Mahasiswa {

    private String nim;
    private String nama;
    private String jenisKelamin;
    private String alamat;
    private String noTelp;
    private String email;
    private String idKelas;
    private String password;

    public Mahasiswa() {}

    public Mahasiswa(String nim, String nama, String jenisKelamin, String alamat,
                     String noTelp, String email, String idKelas, String password) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.email = email;
        this.idKelas = idKelas;
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

    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIdKelas() { return idKelas; }
    public void setIdKelas(String idKelas) { this.idKelas = idKelas; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
