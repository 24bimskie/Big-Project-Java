package controller.mahasiswa;

import dao.MahasiswaDAO;
import model.Mahasiswa;
import util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MahasiswaProfilController {

    // ID sudah disesuaikan persis dengan file FXML di atas!
    @FXML private Label lblProfilNama;
    @FXML private Label lblProfilNim;
    @FXML private Label lblProfilProdi;
    @FXML private Label lblProfilSemester;
    @FXML private Label lblProfilEmail;
    @FXML private Label lblProfilAsalDaerah;

    private MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();

    @FXML
    public void initialize() {
        loadProfilDinamis();
    }

    private void loadProfilDinamis() {
        if (UserSession.getCurrentUser() != null) {
            String nimLogin = UserSession.getCurrentUser().getUsername();
            Mahasiswa mhs = mahasiswaDAO.getByNim(nimLogin);
            
            if (mhs != null) {
                // Set data riil dari DB ke halaman UI
                lblProfilNama.setText(mhs.getNama());
                lblProfilNim.setText(mhs.getNim());
                lblProfilProdi.setText(mhs.getProdi());       
                lblProfilSemester.setText("Semester " + mhs.getSemester()); 
                lblProfilEmail.setText(mhs.getEmail());       
                lblProfilAsalDaerah.setText(mhs.getAsalDaerah()); 
                
                System.out.println("✅ UI Profil sinkron dengan Database untuk NIM: " + nimLogin);
            } else {
                System.out.println("⚠️ Akun ada tapi detail tabel mahasiswa kosong di DB.");
                lblProfilNama.setText("Data Kosong");
                lblProfilNim.setText(nimLogin);
            }
        }
    }
}