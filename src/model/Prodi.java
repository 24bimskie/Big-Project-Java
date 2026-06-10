package model;

/**
 * Model untuk entitas Program Studi (Prodi).
 * Digunakan pada use case: Input Data Prodi.
 */
public class Prodi {

    private String idProdi;
    private String namaProdi;
    private String fakultas;

    public Prodi() {}

    public Prodi(String idProdi, String namaProdi, String fakultas) {
        this.idProdi = idProdi;
        this.namaProdi = namaProdi;
        this.fakultas = fakultas;
    }

    // Getter dan Setter
    public String getIdProdi() { return idProdi; }
    public void setIdProdi(String idProdi) { this.idProdi = idProdi; }

    public String getNamaProdi() { return namaProdi; }
    public void setNamaProdi(String namaProdi) { this.namaProdi = namaProdi; }

    public String getFakultas() { return fakultas; }
    public void setFakultas(String fakultas) { this.fakultas = fakultas; }
}
