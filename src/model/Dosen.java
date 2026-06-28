package model;

/**
 * Model untuk entitas Dosen.
 * Kolom DB: id, nidn, nama_lengkap, email, fakultas, foto_profil
 */
public class Dosen {

    private String nidn;
    private String namaLengkap;
    private String email;
    private String fakultas;
    private String fotoProfil;

    public Dosen() {}

    public Dosen(String nidn, String namaLengkap, String email, String fakultas) {
        this.nidn = nidn;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.fakultas = fakultas;
    }

    public String getNidn() { return nidn; }
    public void setNidn(String nidn) { this.nidn = nidn; }

    // backward compatibility
    public String getNip() { return nidn; }
    public void setNip(String nidn) { this.nidn = nidn; }

    public String getNama() { return namaLengkap; }
    public void setNama(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFakultas() { return fakultas; }
    public void setFakultas(String fakultas) { this.fakultas = fakultas; }

    public String getFotoProfil() { return fotoProfil; }
    public void setFotoProfil(String fotoProfil) { this.fotoProfil = fotoProfil; }
}
