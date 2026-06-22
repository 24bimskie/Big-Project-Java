package controller.mahasiswa;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent; // Tambahan import
import javafx.scene.Scene;  // Tambahan import
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;
import util.UserSession;

public class MahasiswaDashboardController {

    @FXML
    public Button btnJadwal;
    @FXML
    public Button btnProfil;
    @FXML
    public Button btnLogout;
    @FXML
    public AnchorPane contentArea;
    @FXML
    public Label lblHeaderNama;
    @FXML
    public Label lblHeaderNim;

    private List<Node> dashboardAwalChildren = new ArrayList<>();

    @FXML
    public void initialize() {
        System.out.println("[INTEGRASI] MahasiswaDashboardController berhasil dimuat!");
        
        if (contentArea != null) {
            dashboardAwalChildren.addAll(contentArea.getChildren());
            System.out.println("[INTEGRASI] Berhasil mem-backup halaman utama dashboard.");
        }
        
        try {
            User user = UserSession.getCurrentUser();
            if (user != null) {
                if (lblHeaderNama != null) lblHeaderNama.setText(user.getUsername());
                if (lblHeaderNim != null) lblHeaderNim.setText("NIM. " + user.getUserId());
            }
        } catch (Exception e) {
            System.out.println("[INFO] Sesi kosong.");
        }
    }

    @FXML
    public void showDataAbsen(ActionEvent event) {
        System.out.println("[KLIK] Tombol 'Jadwal & Absensi' aktif.");
        if (contentArea != null) {
            contentArea.getChildren().setAll(dashboardAwalChildren);
            updateVisualTombol(btnJadwal, btnProfil);
        }
    }

    @FXML
    public void showProfil(ActionEvent event) {
        System.out.println("[KLIK] Tombol 'Profil Saya' aktif. Mencoba memuat halaman...");
        
        URL fxmlLocation = getClass().getResource("/view/mahasiswa/ProfilMahasiswaView.fxml");
        
        if (fxmlLocation == null) {
            System.err.println("\n========================================================");
            System.err.println("[EROR] File 'ProfilMahasiswaView.fxml' tidak ditemukan!");
            System.err.println("========================================================\n");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            AnchorPane profilView = loader.load();

            AnchorPane.setTopAnchor(profilView, 0.0);
            AnchorPane.setBottomAnchor(profilView, 0.0);
            AnchorPane.setLeftAnchor(profilView, 0.0);
            AnchorPane.setRightAnchor(profilView, 0.0);

            if (contentArea != null) {
                contentArea.getChildren().setAll(profilView);
                updateVisualTombol(btnProfil, btnJadwal);
                System.out.println("[INTEGRASI] Halaman Profil sukses ditampilkan!");
            }

        } catch (IOException e) {
            System.err.println("[EROR KRITIKAL] Gagal me-load komponen FXML.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Hapus sesi user
            UserSession.clearSession();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Load LoginView.fxml dengan jalur yang benar
            URL loginLocation = getClass().getResource("/view/login/LoginView.fxml");
            
            if (loginLocation == null) {
                System.err.println("[ERROR] LoginView.fxml tidak ditemukan!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(loginLocation);
            Parent loginRoot = loader.load();
            
            Scene scene = new Scene(loginRoot);
            stage.setScene(scene);
            stage.setTitle("Login - Sistem Akademik");
            stage.centerOnScreen();
            stage.show();
            
            System.out.println("[SUCCESS] Berhasil logout dan kembali ke halaman login");

        } catch (Exception e) {
            System.err.println("[ERROR] Gagal load halaman login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void updateVisualTombol(Button tombolAktif, Button tombolMati) {
        if (tombolAktif != null) {
            tombolAktif.setStyle("-fx-background-color: #334155; -fx-text-fill: #ffffff; -fx-alignment: CENTER_LEFT; -fx-background-radius: 8px; -fx-font-weight: bold;");
        }
        if (tombolMati != null) {
            tombolMati.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-alignment: CENTER_LEFT; -fx-background-radius: 8px;");
        }
    }
}