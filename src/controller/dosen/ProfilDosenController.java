package controller.dosen;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.AlertHelper;
import util.UserSession;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfilDosenController implements Initializable {

    @FXML
    private TextField txtNidn;
    @FXML
    private TextField txtNama;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtFakultas;
    @FXML
    private PasswordField txtPassword;

    @FXML
    private Circle fotoProfil;
    @FXML
    private Button btnUbahFoto;
    @FXML
    private Button btnSimpan;
    @FXML
    private Button btnBatal;

    private File selectedImageFile;

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (btnSimpan != null)
            btnSimpan.setOnAction(e -> simpanProfil());
        if (btnBatal != null)
            btnBatal.setOnAction(e -> batalUbah());
        if (btnUbahFoto != null)
            btnUbahFoto.setOnAction(e -> ubahFoto());

        loadProfilDosen();
    }

    private void loadProfilDosen() {
        System.out.println("🔄 Menunggu data dari database...");
        // Text field sudah diset editable=false dan memiliki prompt di FXML.
    }

    private void simpanProfil() {
        if (txtNama != null && txtEmail != null) {
            System.out.println("✅ BERHASIL SIMPAN!");
            System.out.println("Nama Baru: " + txtNama.getText());
            System.out.println("Email Baru: " + txtEmail.getText());
            System.out.println("Password Baru: " + txtPassword.getText());
            if (selectedImageFile != null) {
                System.out.println("File Foto Baru: " + selectedImageFile.getAbsolutePath());
            }

            // Jika password diisi, update via tabel user
            String newPassword = txtPassword.getText();
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                // Gunakan username dari session untuk update password di tabel user
                if (UserSession.getCurrentUser() != null) {
                    userDAO.updatePassword(UserSession.getCurrentUser().getUsername(), newPassword.trim());
                    System.out.println("🔑 Password berhasil diperbarui di tabel user.");
                }
            }

            System.out.println("Menyimpan pembaruan profil ke database...");
        }
    }

    private void batalUbah() {
        System.out.println("❌ Perubahan dibatalkan, me-reload data awal...");
        selectedImageFile = null;
        loadProfilDosen();
    }

    private void ubahFoto() {
        System.out.println("📸 Membuka dialog FileChooser untuk memilih foto baru...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profil");

        // Filter agar hanya bisa memilih gambar
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        // Dapatkan stage dari salah satu node yang ada (misal dari button)
        Stage stage = (Stage) btnUbahFoto.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            if (fotoProfil != null) {
                fotoProfil.setFill(new ImagePattern(image));
            }
            System.out.println("✅ Foto profil berhasil diubah di tampilan!");
        }
    }
}