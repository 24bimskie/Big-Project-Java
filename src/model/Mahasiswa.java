package model;

public class Mahasiswa {
    private String nim;
    private String nama;
    private int semester; 
    private String email;
    private String asalDaerah;
    private String prodi;
    
    // Atribut dari Form Registrasi
    private String gender;
    private String kelas;
    private String alamat;

    // Constructor Kosong
    public Mahasiswa() {}

    // Constructor untuk Registrasi (Menerima 6 String dari RegisterController)
    public Mahasiswa(String nim, String nama, String gender, String alamat, String kelas, String prodi) {
        this.nim = nim;
        this.nama = nama;
        this.gender = gender;
        this.alamat = alamat;
        this.asalDaerah = alamat; // Amankan kalau-kalau DAO manggil getAsalDaerah
        this.kelas = kelas;
        this.prodi = prodi;
        this.semester = 1;        
        this.email = nama.trim().toLowerCase().replace(" ", "") + "@student.umpwr.ac.id";
    }

    // Constructor Lama (Bawaan tim)
    public Mahasiswa(String nim, String nama, int semester, String email, String asalDaerah, String prodi) {
        this.nim = nim;
        this.nama = nama;
        this.semester = semester;
        this.email = nama.trim().toLowerCase().replace(" ", "") + "@student.umpwr.ac.id";
        this.asalDaerah = asalDaerah;
        this.prodi = prodi;
    }

    // --- GETTER & SETTER ---
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

    public String getProdi() {
        return prodi;
    }

    public void setProdi(String prodi) {
        this.prodi = prodi;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // 💡 KUNCI PENYESUAIAN: Alias khusus biar UserDAO buatan temen lu gak error
    public String getJenisKelamin() {
        return gender;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.gender = jenisKelamin;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}