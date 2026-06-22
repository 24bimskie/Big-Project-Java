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
import javafx.scene.layout.VBox;
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
 * Navigasi ke semua fitur admin: data mahasiswa, dosen, prodi, kelas, matkul,
 * jadwal.
 */
public class AdminDashboardController implements Initializable {

    // ===== FXML Bindings =====

    @FXML
    private Label labelNamaUser;
    @FXML
    private Label labelRole;
    @FXML
    private StackPane contentArea;

    // Tombol & submenu Mahasiswa
    @FXML
    private Button btnMahasiswa;
    @FXML
    private VBox submenuMahasiswa;
    @FXML
    private Button btnMahasiswaInput;
    @FXML
    private Button btnMahasiswaLihat;

    // Tombol & submenu Dosen
    @FXML
    private Button btnDosen;
    @FXML
    private VBox submenuDosen;
    @FXML
    private Button btnDosenLihat;
    @FXML
    private Button btnDosenInput;

    // Tombol & submenu Prodi & Kelas
    @FXML
    private Button btnProdiKelas;
    @FXML
    private VBox submenuProdiKelas;
    @FXML
    private Button btnProdiKelasLihat;
    @FXML
    private Button btnProdiKelasInput;

    // Tombol & submenu Mata Kuliah
    @FXML
    private Button btnMataKuliah;
    @FXML
    private VBox submenuMataKuliah;
    @FXML
    private Button btnMataKuliahLihat;
    @FXML
    private Button btnMataKuliahInput;

    // Tombol & submenu Jadwal
    @FXML
    private Button btnJadwal;
    @FXML
    private VBox submenuJadwal;
    @FXML
    private Button btnJadwalLihat;
    @FXML
    private Button btnJadwalInput;

    @FXML
    private Button btnLogout;

    // ===== Lifecycle =====

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            labelNamaUser.setText(currentUser.getUsername());
            labelRole.setText("Role: " + currentUser.getRole());
        }
        // Default: tampilkan halaman Data Mahasiswa saat pertama masuk
        loadContent("/view/admin/DataMahasiswaView.fxml");
        setActiveButton(btnMahasiswa);
    }

    // ===== Navigasi Sidebar — Mahasiswa =====

    @FXML
    private void handleMenuMahasiswaToggle(ActionEvent event) {
        toggleSubmenu(submenuMahasiswa, btnMahasiswa, "Data Mahasiswa");
    }

    @FXML
    private void handleMenuMahasiswaInput(ActionEvent event) {
        DataMahasiswaController ctrl = loadContentWithController("/view/admin/DataMahasiswaView.fxml");
        if (ctrl != null)
            ctrl.selectTab(1);
        setActiveButton(btnMahasiswaInput);
    }

    @FXML
    private void handleMenuMahasiswaLihat(ActionEvent event) {
        DataMahasiswaController ctrl = loadContentWithController("/view/admin/DataMahasiswaView.fxml");
        if (ctrl != null)
            ctrl.selectTab(0);
        setActiveButton(btnMahasiswaLihat);
    }

    // ===== Navigasi Sidebar — Dosen =====

    @FXML
    private void handleMenuDosenToggle(ActionEvent event) {
        toggleSubmenu(submenuDosen, btnDosen, "Data Dosen");
    }

    @FXML
    private void handleMenuDosenLihat(ActionEvent event) {
        DataDosenController ctrl = loadContentWithController("/view/admin/DataDosenView.fxml");
        if (ctrl != null)
            ctrl.selectTab(0);
        setActiveButton(btnDosenLihat);
    }

    @FXML
    private void handleMenuDosenInput(ActionEvent event) {
        DataDosenController ctrl = loadContentWithController("/view/admin/DataDosenView.fxml");
        if (ctrl != null)
            ctrl.selectTab(1);
        setActiveButton(btnDosenInput);
    }

    // ===== Navigasi Sidebar — Prodi & Kelas =====

    @FXML
    private void handleMenuProdiKelasToggle(ActionEvent event) {
        toggleSubmenu(submenuProdiKelas, btnProdiKelas, "Data Prodi & Kelas");
    }

    @FXML
    private void handleMenuProdiKelasLihat(ActionEvent event) {
        DataProdiKelasController ctrl = loadContentWithController("/view/admin/DataProdiKelasView.fxml");
        if (ctrl != null)
            ctrl.selectTab(0);
        setActiveButton(btnProdiKelasLihat);
    }

    @FXML
    private void handleMenuProdiKelasInput(ActionEvent event) {
        DataProdiKelasController ctrl = loadContentWithController("/view/admin/DataProdiKelasView.fxml");
        if (ctrl != null)
            ctrl.selectTab(1);
        setActiveButton(btnProdiKelasInput);
    }

    // ===== Navigasi Sidebar — Mata Kuliah =====

    @FXML
    private void handleMenuMataKuliahToggle(ActionEvent event) {
        toggleSubmenu(submenuMataKuliah, btnMataKuliah, "Data Mata Kuliah");
    }

    @FXML
    private void handleMenuMataKuliahLihat(ActionEvent event) {
        DataMataKuliahController ctrl = loadContentWithController("/view/admin/DataMataKuliahView.fxml");
        if (ctrl != null)
            ctrl.selectTab(0);
        setActiveButton(btnMataKuliahLihat);
    }

    @FXML
    private void handleMenuMataKuliahInput(ActionEvent event) {
        DataMataKuliahController ctrl = loadContentWithController("/view/admin/DataMataKuliahView.fxml");
        if (ctrl != null)
            ctrl.selectTab(1);
        setActiveButton(btnMataKuliahInput);
    }

    // ===== Navigasi Sidebar — Jadwal =====

    @FXML
    private void handleMenuJadwalToggle(ActionEvent event) {
        toggleSubmenu(submenuJadwal, btnJadwal, "Data Jadwal");
    }

    @FXML
    private void handleMenuJadwalLihat(ActionEvent event) {
        DataJadwalController ctrl = loadContentWithController("/view/admin/DataJadwalView.fxml");
        if (ctrl != null)
            ctrl.selectTab(0);
        setActiveButton(btnJadwalLihat);
    }

    @FXML
    private void handleMenuJadwalInput(ActionEvent event) {
        DataJadwalController ctrl = loadContentWithController("/view/admin/DataJadwalView.fxml");
        if (ctrl != null)
            ctrl.selectTab(1);
        setActiveButton(btnJadwalInput);
    }

    // ===== Logout =====

    @FXML
    private void handleLogout(ActionEvent event) {
        boolean konfirmasi = AlertHelper.showConfirmation(
                "Konfirmasi Logout", "Apakah Anda yakin ingin keluar?");
        if (konfirmasi) {
            UserSession.clearSession();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneManager.switchScene(stage, "/view/login/LoginView.fxml", "Login - Sistem Akademik");
        }
    }

    // ===== Helper Methods =====

    /**
     * Toggle visibilitas submenu sidebar.
     */
    private void toggleSubmenu(VBox submenu, Button parentBtn, String label) {
        if (submenu == null)
            return;
        boolean isVisible = submenu.isVisible();
        submenu.setVisible(!isVisible);
        submenu.setManaged(!isVisible);
        parentBtn.setText(isVisible ? label + " ▼" : label + " ▲");
    }

    /**
     * Memuat konten FXML ke dalam contentArea (StackPane).
     */
    private void loadContent(String fxmlPath) {
        loadContentWithController(fxmlPath);
    }

    /**
     * Memuat konten FXML dan mengembalikan controller-nya.
     */
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
     * Menandai tombol sidebar yang sedang aktif dengan style CSS.
     */
    private void setActiveButton(Button activeButton) {
        Button[] allButtons = {
                btnMahasiswa, btnMahasiswaInput, btnMahasiswaLihat,
                btnDosen, btnDosenLihat, btnDosenInput,
                btnProdiKelas, btnProdiKelasLihat, btnProdiKelasInput,
                btnMataKuliah, btnMataKuliahLihat, btnMataKuliahInput,
                btnJadwal, btnJadwalLihat, btnJadwalInput
        };
        for (Button btn : allButtons) {
            if (btn != null)
                btn.getStyleClass().remove("sidebar-btn-active");
        }
        if (activeButton != null)
            activeButton.getStyleClass().add("sidebar-btn-active");
    }
}
