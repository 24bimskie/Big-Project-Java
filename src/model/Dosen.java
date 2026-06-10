package model;

/**
 * Model untuk entitas Dosen.
 * Digunakan pada use case: Input Data Dosen, Lihat Data Dosen, Mulai Absen.
 */
public class Dosen {

    private String nidn;
    private String nama;
    private String jenisKelamin;
    private String alamat;
    private String noTelp;
    private String email;
    private String password;

    public Dosen() {}

    public Dosen(String nidn, String nama, String jenisKelamin, String alamat,
                 String noTelp, String email, String password) {
        this.nidn = nidn;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.email = email;
        this.password = password;
    }

    // Getter dan Setter
    public String getNidn() { return nidn; }
    public void setNidn(String nidn) { this.nidn = nidn; }

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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
