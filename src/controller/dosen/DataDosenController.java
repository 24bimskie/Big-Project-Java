package controller.dosen;

import dao.DosenDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Dosen;
import model.User;
import util.AlertHelper;
import util.UserSession;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

/**
 * Controller untuk halaman Data Dosen.
 * Menampilkan profil dosen yang sedang login dan memungkinkan edit profil serta upload foto.
 */
public class DataDosenController implements Initializable {

    @FXML private Circle fotoProfil;
    @FXML private Label lblNamaDosen;
    @FXML private Label lblFakultasBadge;
    @FXML private Label lblNidn;
    @FXML private Label lblNama;
    @FXML private Label lblEmail;
    @FXML private Label lblFakultas;
    @FXML private Label lblUsername;
    @FXML private Label lblRole;
    @FXML private Label lblStatus;

    @FXML private Button btnEditProfil;
    @FXML private Button btnSimpanProfil;
    @FXML private Button btnBatalEdit;
    @FXML private Button btnUbahFoto;
    @FXML private TextField txtEditNama;
    @FXML private TextField txtEditEmail;
    @FXML private TextField txtEditFakultas;
    @FXML private VBox editForm;

    private final DosenDAO dosenDAO = new DosenDAO();
    private File selectedImageFile;
    private String currentFotoPath;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (btnEditProfil != null) {
            btnEditProfil.setOnAction(e -> toggleEditForm(true));
        }
        if (btnBatalEdit != null) {
            btnBatalEdit.setOnAction(e -> toggleEditForm(false));
        }
        if (btnSimpanProfil != null) {
            btnSimpanProfil.setOnAction(e -> simpanProfil());
        }
        if (btnUbahFoto != null) {
            btnUbahFoto.setOnAction(e -> ubahFoto());
        }

        loadProfilDosen();
        toggleEditForm(false);
    }

    private void loadProfilDosen() {
        currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            setStatus("⚠ Tidak ada user yang login.");
            return;
        }

        String username = currentUser.getUsername();
        Dosen dosen = findCurrentDosen(username);

        if (dosen != null) {
            lblNamaDosen.setText(dosen.getNamaLengkap() != null ? dosen.getNamaLengkap() : "-");
            lblFakultasBadge.setText(dosen.getFakultas() != null && !dosen.getFakultas().isEmpty()
                    ? dosen.getFakultas() : "Belum diatur");
            lblNidn.setText(dosen.getNidn() != null ? dosen.getNidn() : "-");
            lblNama.setText(dosen.getNamaLengkap() != null ? dosen.getNamaLengkap() : "-");
            lblEmail.setText(dosen.getEmail() != null && !dosen.getEmail().isEmpty()
                    ? dosen.getEmail() : "-");
            lblFakultas.setText(dosen.getFakultas() != null && !dosen.getFakultas().isEmpty()
                    ? dosen.getFakultas() : "-");
            lblUsername.setText(username);
            lblRole.setText(currentUser.getRole() != null ? currentUser.getRole() : "Dosen");

            if (txtEditNama != null) txtEditNama.setText(dosen.getNamaLengkap() != null ? dosen.getNamaLengkap() : "");
            if (txtEditEmail != null) txtEditEmail.setText(dosen.getEmail() != null ? dosen.getEmail() : "");
            if (txtEditFakultas != null) txtEditFakultas.setText(dosen.getFakultas() != null ? dosen.getFakultas() : "");
            currentFotoPath = dosen.getFotoProfil();
            loadProfileImage(currentFotoPath);

            setStatus("✅ Data profil berhasil dimuat.");
        } else {
            lblNamaDosen.setText(username);
            lblFakultasBadge.setText("Belum diatur");
            lblNidn.setText("-");
            lblNama.setText(username);
            lblEmail.setText("-");
            lblFakultas.setText("-");
            lblUsername.setText(username);
            lblRole.setText(currentUser.getRole() != null ? currentUser.getRole() : "Dosen");

            if (txtEditNama != null) txtEditNama.setText(username);
            if (txtEditEmail != null) txtEditEmail.setText("");
            if (txtEditFakultas != null) txtEditFakultas.setText("");
            currentFotoPath = null;
            loadProfileImage(null);

            setStatus("⚠ Data dosen belum lengkap di database. Silakan lengkapi profil Anda.");
        }
    }

    private Dosen findCurrentDosen(String username) {
        Dosen dosen = dosenDAO.getByNidn(username);
        if (dosen == null) {
            dosen = dosenDAO.getByNamaLengkap(username);
        }
        return dosen;
    }

    private void toggleEditForm(boolean show) {
        if (editForm != null) {
            editForm.setVisible(show);
            editForm.setManaged(show);
        }
        if (btnEditProfil != null) {
            btnEditProfil.setVisible(!show);
            btnEditProfil.setManaged(!show);
        }
        if (show) {
            selectedImageFile = null;
        }
    }

    private void simpanProfil() {
        if (currentUser == null) {
            AlertHelper.showError("Gagal", "Tidak ada user yang login.");
            return;
        }

        String username = currentUser.getUsername();
        String nama = txtEditNama != null ? txtEditNama.getText().trim() : "";
        String email = txtEditEmail != null ? txtEditEmail.getText().trim() : "";
        String fakultas = txtEditFakultas != null ? txtEditFakultas.getText().trim() : "";

        if (nama.isEmpty()) {
            AlertHelper.showError("Gagal", "Nama lengkap wajib diisi.");
            return;
        }

        Dosen dosen = findCurrentDosen(username);
        if (dosen == null) {
            dosen = new Dosen(username, nama, email, fakultas);
        } else {
            dosen.setNamaLengkap(nama);
            dosen.setEmail(email);
            dosen.setFakultas(fakultas);
        }

        if (selectedImageFile != null) {
            String savedPath = saveProfileImage(selectedImageFile, username);
            dosen.setFotoProfil(savedPath);
            currentFotoPath = savedPath;
        } else if (currentFotoPath != null) {
            dosen.setFotoProfil(currentFotoPath);
        }

        dosenDAO.update(dosen);
        loadProfilDosen();
        toggleEditForm(false);
        AlertHelper.showInfo("Berhasil", "Profil dosen berhasil diperbarui.");
    }

    private void ubahFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        Stage stage = (Stage) (btnUbahFoto != null ? btnUbahFoto.getScene().getWindow() : null);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            selectedImageFile = file;
            loadProfileImage(file.getAbsolutePath());
        }
    }

    private String saveProfileImage(File imageFile, String username) {
        try {
            Path uploadDir = Paths.get("uploads", "profile");
            Files.createDirectories(uploadDir);

            String extension = imageFile.getName().contains(".")
                    ? imageFile.getName().substring(imageFile.getName().lastIndexOf('.'))
                    : ".png";
            Path target = uploadDir.resolve(username + "_" + System.currentTimeMillis() + extension);
            Files.copy(imageFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().normalize().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadProfileImage(String photoPath) {
        if (fotoProfil == null) {
            return;
        }

        if (photoPath != null && !photoPath.isBlank()) {
            File file = new File(photoPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                fotoProfil.setFill(new ImagePattern(image));
                return;
            }
        }

        fotoProfil.setFill(new javafx.scene.paint.Color(0.8, 0.84, 0.9, 1));
    }

    private void setStatus(String message) {
        if (lblStatus != null) {
            lblStatus.setText(message);
        }
    }
}
