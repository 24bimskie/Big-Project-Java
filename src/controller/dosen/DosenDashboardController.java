package controller.dosen;

import dao.DosenDAO;
import dao.MahasiswaDAO;
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
import model.Dosen;
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

    private final DosenDAO dosenDAO = new DosenDAO();
    private final MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser != null && lblNamaDosen != null) {
            lblNamaDosen.setText(resolveDisplayName(currentUser));
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

        if (btnCari != null) {
            btnCari.setOnAction(e -> cariMahasiswa());
        }
        if (txtSearch != null) {
            txtSearch.setOnAction(e -> cariMahasiswa());
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

    private String resolveDisplayName(User currentUser) {
        if (currentUser == null) {
            return "Dosen";
        }

        String username = currentUser.getUsername();
        Dosen dosen = dosenDAO.getByNidn(username);
        if (dosen == null) {
            dosen = dosenDAO.getByNamaLengkap(username);
        }

        if (dosen != null && dosen.getNamaLengkap() != null && !dosen.getNamaLengkap().isBlank()) {
            return dosen.getNamaLengkap();
        }
        return username;
    }

    private void cariMahasiswa() {
        if (txtSearch == null) {
            return;
        }

        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            AlertHelper.showWarning("Pencarian", "Masukkan NIM atau nama mahasiswa terlebih dahulu.");
            return;
        }

        var mahasiswaList = mahasiswaDAO.getAll();
        var hasil = mahasiswaList.stream()
                .filter(m -> {
                    String nim = m.getNim() == null ? "" : m.getNim().toLowerCase();
                    String nama = m.getNama() == null ? "" : m.getNama().toLowerCase();
                    String k = keyword.toLowerCase();
                    return nim.contains(k) || nama.contains(k);
                })
                .toList();

        if (hasil.isEmpty()) {
            AlertHelper.showWarning("Pencarian", "Tidak ada mahasiswa yang cocok dengan pencarian Anda.");
        } else {
            StringBuilder msg = new StringBuilder();
            for (var m : hasil) {
                msg.append("- ").append(m.getNim()).append(" | ").append(m.getNama()).append("\n");
            }
            AlertHelper.showInfo("Hasil Pencarian", msg.toString());
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