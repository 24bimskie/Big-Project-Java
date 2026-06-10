package model;

/**
 * Model untuk entitas Mata Kuliah.
 * Digunakan pada use case: Input Mata Kuliah.
 */
public class MataKuliah {

    private String kodeMk;
    private String namaMk;
    private int sks;
    private int semester;
    private String idProdi;

    public MataKuliah() {}

    public MataKuliah(String kodeMk, String namaMk, int sks, int semester, String idProdi) {
        this.kodeMk = kodeMk;
        this.namaMk = namaMk;
        this.sks = sks;
        this.semester = semester;
        this.idProdi = idProdi;
    }

    // Getter dan Setter
    public String getKodeMk() { return kodeMk; }
    public void setKodeMk(String kodeMk) { this.kodeMk = kodeMk; }

    public String getNamaMk() { return namaMk; }
    public void setNamaMk(String namaMk) { this.namaMk = namaMk; }

    public int getSks() { return sks; }
    public void setSks(int sks) { this.sks = sks; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getIdProdi() { return idProdi; }
    public void setIdProdi(String idProdi) { this.idProdi = idProdi; }
}
