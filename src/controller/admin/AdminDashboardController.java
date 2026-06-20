package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.User;
import util.AlertHelper;
import util.SceneManager;
import util.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller untuk Dashboard Admin.
 * Navigasi ke semua fitur admin: data mahasiswa, dosen, prodi, kelas, matkul, jadwal.
 */
public class AdminDashboardController implements Initializable {

    // ===== FXML Bindings =====

    /** Label untuk menampilkan nama user yang sedang login */
    @FXML
    private Label labelNamaUser;

    /** Label untuk menampilkan role user */
    @FXML
    private Label labelRole;

    /** Area utama tempat konten sub-halaman ditampilkan */
    @FXML
    private StackPane contentArea;

    // Tombol-tombol menu sidebar
    @FXML
    private Button btnMahasiswa;

    @FXML
    private javafx.scene.layout.VBox submenuMahasiswa;

    @FXML
    private Button btnMahasiswaInput;

    @FXML
    private Button btnMahasiswaLihat;

    @FXML
    private Button btnDosen;

    @FXML
    private javafx.scene.layout.VBox submenuDosen;

    @FXML
    private Button btnDosenLihat;

    @FXML
    private Button btnDosenInput;

    @FXML
    private Button btnProdiKelas;

    @FXML
    private Button btnMataKuliah;

    @FXML
    private Button btnJadwal;

    @FXML
    private Button btnLogout;

    // ===== Lifecycle =====

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tampilkan info user yang sedang login
        User currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            labelNamaUser.setText(currentUser.getUsername());
            labelRole.setText("Role: " + currentUser.getRole());
        }

        // Tampilkan halaman Data Mahasiswa sebagai default saat pertama masuk
        loadContent("/view/admin/DataMahasiswaView.fxml");
        setActiveButton(btnMahasiswa);
    }

    // ===== Navigasi Sidebar =====

    /** Toggle submenu Data Mahasiswa */
    @FXML
    private void handleMenuMahasiswaToggle(ActionEvent event) {
        boolean isVisible = submenuMahasiswa.isVisible();
        submenuMahasiswa.setVisible(!isVisible);
        submenuMahasiswa.setManaged(!isVisible);
        btnMahasiswa.setText(isVisible ? "Data Mahasiswa ▼" : "Data Mahasiswa ▲");
    }

    @FXML
    private void handleMenuMahasiswaInput(ActionEvent event) {
        DataMahasiswaController controller = loadContentWithController("/view/admin/DataMahasiswaView.fxml");
        if (controller != null) controller.selectTab(1);
        setActiveButton(btnMahasiswaInput);
    }

    @FXML
    private void handleMenuMahasiswaLihat(ActionEvent event) {
        DataMahasiswaController controller = loadContentWithController("/view/admin/DataMahasiswaView.fxml");
        if (controller != null) controller.selectTab(0);
        setActiveButton(btnMahasiswaLihat);
    }

    /** Toggle submenu Data Dosen */
    @FXML
    private void handleMenuDosenToggle(ActionEvent event) {
        boolean isVisible = submenuDosen.isVisible();
        submenuDosen.setVisible(!isVisible);
        submenuDosen.setManaged(!isVisible);
        btnDosen.setText(isVisible ? "Data Dosen ▼" : "Data Dosen ▲");
    }

    @FXML
    private void handleMenuDosenInput(ActionEvent event) {
        DataDosenController controller = loadContentWithController("/view/admin/DataDosenView.fxml");
        if (controller != null) controller.selectTab(1);
        setActiveButton(btnDosenInput);
    }

    @FXML
    private void handleMenuDosenLihat(ActionEvent event) {
        DataDosenController controller = loadContentWithController("/view/admin/DataDosenView.fxml");
        if (controller != null) controller.selectTab(0);
        setActiveButton(btnDosenLihat);
    }

    /** Navigasi ke halaman Data Prodi & Kelas */
    @FXML
    private void handleMenuProdiKelas(ActionEvent event) {
        loadContent("/view/admin/DataProdiKelasView.fxml");
        setActiveButton(btnProdiKelas);
    }

    /** Navigasi ke halaman Data Mata Kuliah */
    @FXML
    private void handleMenuMataKuliah(ActionEvent event) {
        loadContent("/view/admin/DataMataKuliahView.fxml");
        setActiveButton(btnMataKuliah);
    }

    /** Navigasi ke halaman Data Jadwal */
    @FXML
    private void handleMenuJadwal(ActionEvent event) {
        loadContent("/view/admin/DataJadwalView.fxml");
        setActiveButton(btnJadwal);
    }

    // ===== Logout =====

    /** Logout dari sistem dan kembali ke halaman Login */
    @FXML
    private void handleLogout(ActionEvent event) {
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

    // ===== Helper Methods =====

    /**
     * Memuat konten FXML ke dalam contentArea (StackPane).
     * @param fxmlPath Path ke file FXML yang akan dimuat
     */
    private void loadContent(String fxmlPath) {
        loadContentWithController(fxmlPath);
    }

    private <T> T loadContentWithController(String fxmlPath) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                AlertHelper.showError("Error", "Halaman tidak ditemukan: " + fxmlPath);
                return null;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent content = loader.load();
            contentArea.getChildren().setAll(content);
            return loader.getController();
        } catch (IOException e) {
            AlertHelper.showError("Error", "Gagal memuat halaman: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Menandai tombol sidebar yang sedang aktif dengan style CSS "active".
     * @param activeButton Tombol yang ingin ditandai aktif
     */
    private void setActiveButton(Button activeButton) {
        Button[] allButtons = {btnMahasiswa, btnMahasiswaInput, btnMahasiswaLihat, btnDosen, btnProdiKelas, btnMataKuliah, btnJadwal};
        for (Button btn : allButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-btn-active");
            }
        }
        if (activeButton != null) {
            activeButton.getStyleClass().add("sidebar-btn-active");
        }
    }
}
