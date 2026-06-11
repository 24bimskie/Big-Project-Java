package controller.dosen;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller untuk Dashboard Dosen.
 * Navigasi ke fitur: Mulai Absen, Rekap Absen, Lihat Data Dosen.
 */
public class DosenDashboardController implements Initializable {

    @FXML private Button btnDashboard;
    @FXML private Button btnMulaiAbsen;
    @FXML private Button btnRekapAbsen;
    @FXML private Button btnProfil;
    @FXML private Button btnLogout;
    @FXML private Button btnCari;
    
    @FXML private TextField txtSearch;
    @FXML private Label lblNamaDosen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup event handlers untuk navigasi
        if (btnDashboard != null) {
            btnDashboard.setOnAction(e -> handleNavDashboard());
        }
        if (btnMulaiAbsen != null) {
            btnMulaiAbsen.setOnAction(e -> handleNavMulaiAbsen());
        }
        if (btnRekapAbsen != null) {
            btnRekapAbsen.setOnAction(e -> handleNavRekapAbsen());
        }
        if (btnProfil != null) {
            btnProfil.setOnAction(e -> handleNavProfil());
        }
        if (btnLogout != null) {
            btnLogout.setOnAction(e -> handleLogout());
        }
        if (btnCari != null) {
            btnCari.setOnAction(e -> handleCari());
        }
    }
    
    private void handleNavDashboard() {
        System.out.println("Navigasi ke Dashboard Utama...");
        // TODO: Load view utama ke Center BorderPane
    }
    
    private void handleNavMulaiAbsen() {
        System.out.println("Navigasi ke Mulai Absen...");
        // TODO: Load MulaiAbsenView.fxml ke Center BorderPane
    }
    
    private void handleNavRekapAbsen() {
        System.out.println("Navigasi ke Rekap Absen...");
        // TODO: Load RekapAbsenView.fxml ke Center BorderPane
    }
    
    private void handleNavProfil() {
        System.out.println("Navigasi ke Profil Dosen...");
        // TODO: Load ProfilDosenView.fxml ke Center BorderPane
    }
    
    private void handleLogout() {
        System.out.println("Proses Logout...");
        // TODO: Kembali ke halaman Login (LoginView.fxml)
    }

    private void handleCari() {
        System.out.println("Mencari data mahasiswa...");
        // TODO: Implementasi logika pencarian data di tabel utama
    }
}
