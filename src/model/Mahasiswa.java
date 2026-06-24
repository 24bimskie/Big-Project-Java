package model;

public class Mahasiswa {
    private String nim;
    private String nama;
    private int semester; 
    private String email;
    private String asalDaerah;
    private String prodi; // 👈 Tambah atribut prodi

    // Constructor Kosong
    public Mahasiswa() {}

    // Constructor Lengkap
    public Mahasiswa(String nim, String nama, int semester, String email, String asalDaerah, String prodi) {
        this.nim = nim;
        this.nama = nama;
        this.semester = semester;
        this.email = email;
        this.asalDaerah = asalDaerah;
        this.prodi = prodi;
    }

    // --- GETTER & SETTER LENGKAP ---
    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAsalDaerah() {
        return asalDaerah;
    }

    public void setAsalDaerah(String asalDaerah) {
        this.asalDaerah = asalDaerah;
    }

    // 👈 KUNCI PERBAIKAN: Getter & Setter Prodi buat MahasiswaProfilController
    public String getProdi() {
        return prodi;
    }

    public void setProdi(String prodi) {
        this.prodi = prodi;
    }
}