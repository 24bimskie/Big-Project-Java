package controller.mahasiswa;

import dao.MahasiswaDAO;
import model.Mahasiswa;
import util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button; 
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.stage.Stage;
import util.SceneManager;
import java.io.IOException;

public class MahasiswaDashboardController {

    @FXML private Label lblHeaderNama;
    @FXML private Label lblHeaderNim;
    @FXML private Button btnJadwal;
    @FXML private Button btnProfil;
    @FXML private AnchorPane contentArea; 

    private MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();

    @FXML
    public void initialize() {
        loadDataMahasiswaDinamis();
        // Default awal halaman absen
        setSubView("/view/mahasiswa/DataAbsenMahasiswaView.fxml");
    }

    private void loadDataMahasiswaDinamis() {
        if (UserSession.getCurrentUser() != null) {
            String nimLogin = UserSession.getCurrentUser().getUsername();
            Mahasiswa mhs = mahasiswaDAO.getByNim(nimLogin);
            
            if (mhs != null) {
                lblHeaderNama.setText(mhs.getNama());
                lblHeaderNim.setText(mhs.getNim());
            } else {
                lblHeaderNama.setText("Data Profil Belum Diisi");
                lblHeaderNim.setText(nimLogin);
            }
        } else {
            lblHeaderNama.setText("Guest");
            lblHeaderNim.setText("-");
        }
    }

    private void setSubView(String fxmlPath) {
        try {
            java.net.URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                System.out.println("❌ File FXML tidak ditemukan: " + fxmlPath);
                return;
            }
            Parent view = FXMLLoader.load(fxmlLocation);
            contentArea.getChildren().setAll(view);
            
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException e) {
            System.out.println("❌ Gagal memuat sub-view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML
    private void showDataAbsen(ActionEvent event) {
        setSubView("/view/mahasiswa/DataAbsenMahasiswaView.fxml"); 
        btnJadwal.setStyle("-fx-background-color: #334155; -fx-text-fill: #ffffff; -fx-alignment: center-left; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnProfil.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-alignment: center-left; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: normal;");
    }

    @FXML
    private void showProfil(ActionEvent event) {
        setSubView("/view/mahasiswa/ProfilMahasiswaView.fxml"); 
        btnProfil.setStyle("-fx-background-color: #334155; -fx-text-fill: #ffffff; -fx-alignment: center-left; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnJadwal.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-alignment: center-left; -fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: normal;");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("🚪 Proses Logout dijalankan...");
        UserSession.setCurrentUser(null);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneManager.switchScene(stage, "/view/login/LoginView.fxml", "Login");
    }
}