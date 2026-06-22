package controller.dosen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.User;
import util.AlertHelper;
import util.SceneManager;
import util.UserSession;

import java.net.URL;
import java.util.ResourceBundle;

public class DosenDashboardController implements Initializable {

    @FXML private Button btnJadwalMengajar;
    @FXML private Button btnDataDosen;
    @FXML private Button btnLogout;
    @FXML private Button btnToggleSidebar;

    @FXML private StackPane papanKontenTengah; // Wadah bongkar pasang
    @FXML private javafx.scene.layout.VBox sidebar;
    
    @FXML private Label lblNamaDosen;
    @FXML private TextField txtSearch;
    @FXML private Button btnCari;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser != null && lblNamaDosen != null) {
            lblNamaDosen.setText(currentUser.getUsername());
        }

        // Klik tombol asli sidebar langsung ganti isi wadah tengah!
        if (btnJadwalMengajar != null) btnJadwalMengajar.setOnAction(e -> gantiPanelTengah("JadwalMengajarView.fxml", btnJadwalMengajar));
        if (btnDataDosen != null) btnDataDosen.setOnAction(e -> gantiPanelTengah("DataDosenView.fxml", btnDataDosen));
        
        if (btnLogout != null) {
            btnLogout.setOnAction(this::handleLogout);
        }
        
        if (btnToggleSidebar != null && sidebar != null) {
            btnToggleSidebar.setOnAction(e -> {
                boolean isVisible = sidebar.isVisible();
                sidebar.setVisible(!isVisible);
                sidebar.setManaged(!isVisible);
            });
        }
        
        // Default halaman pertama pas dibuka
        gantiPanelTengah("JadwalMengajarView.fxml", btnJadwalMengajar);
    }

    private void gantiPanelTengah(String namaFile, Button activeButton) {
        try {
            URL url = getClass().getResource("/view/dosen/" + namaFile);
            if (url == null) {
                System.err.println("Halaman tidak ditemukan: /view/dosen/" + namaFile);
                return;
            }
            Parent halamanBaru = FXMLLoader.load(url);
            
            if (papanKontenTengah != null) {
                papanKontenTengah.getChildren().clear(); // Hapus halaman lama
                papanKontenTengah.getChildren().add(halamanBaru); // Masuk halaman baru
            }
            setActiveButton(activeButton);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setActiveButton(Button activeButton) {
        Button[] allButtons = {btnJadwalMengajar, btnDataDosen};
        for (Button btn : allButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-btn-active");
            }
        }
        if (activeButton != null) {
            activeButton.getStyleClass().add("sidebar-btn-active");
        }
    }

    private void handleLogout(javafx.event.ActionEvent event) {
        boolean konfirmasi = AlertHelper.showConfirmation(
                "Konfirmasi Logout",
                "Apakah Anda yakin ingin keluar?"
        );

        if (konfirmasi) {
            UserSession.clearSession();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneManager.switchScene(stage, "/view/login/LoginView.fxml", "Login - Sistem Akademik");
        }
    }
}