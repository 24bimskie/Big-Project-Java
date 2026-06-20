package model;

/**
 * Model untuk entitas Dosen.
 * Kolom DB: id, nip, nama, gender, alamat, password
 */
public class Dosen {

    private String nip;       // primary key di DB
    private String nama;
    private String jenisKelamin; // kolom: gender
    private String alamat;
    private String password;

    public Dosen() {}

    public Dosen(String nip, String nama, String jenisKelamin,
                 String alamat, String password) {
        this.nip = nip;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.password = password;
    }

    // Getter & Setter
    public String getNip() { return nip; }
    public void setNip(String nip) { this.nip = nip; }

    // alias getNidn() agar backward-compatible dengan controller lama
    public String getNidn() { return nip; }
    public void setNidn(String nip) { this.nip = nip; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
