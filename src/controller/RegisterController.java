package controller;

import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Mahasiswa;
import model.Kelas;
import util.AlertHelper;
import util.SceneManager;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.List;
import java.util.stream.Collectors;
import dao.KelasDAO;

/**
 * Controller untuk halaman Registrasi (Khusus Mahasiswa).
 */
public class RegisterController implements Initializable {

    @FXML
    private TextField nimField;
    @FXML
    private TextField namaField;
    @FXML
    private ToggleGroup genderGroup;
    @FXML
    private RadioButton radioL;
    @FXML
    private RadioButton radioP;
    @FXML
    private ComboBox<String> kelasComboBox;
    @FXML
    private ComboBox<String> prodiComboBox;
    @FXML
    private TextField alamatField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label statusLabel;

    private UserDAO userDAO = new UserDAO();
    private KelasDAO kelasDAO = new KelasDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ambil data prodi dari database (distinct dari tabel kelas)
        List<Kelas> semuaKelas = kelasDAO.getAll();
        List<String> prodiList = semuaKelas.stream()
                .map(Kelas::getIdProdi)
                .distinct()
                .collect(Collectors.toList());
        prodiComboBox.getItems().addAll(prodiList);

        // Listener untuk mengubah daftar kelas saat prodi dipilih
        prodiComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            kelasComboBox.getItems().clear();
            if (newVal != null) {
                List<Kelas> kelasByProdi = kelasDAO.getByProdi(newVal);
                List<String> namaKelasList = kelasByProdi.stream()
                        .map(Kelas::getNamaKelas)
                        .collect(Collectors.toList());
                kelasComboBox.getItems().addAll(namaKelasList);
            }
        });
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        statusLabel.setText("");

        String nim = nimField.getText();
        String nama = namaField.getText();

        String gender = null;
        if (radioL.isSelected()) {
            gender = "L";
        } else if (radioP.isSelected()) {
            gender = "P";
        }

        String kelas = kelasComboBox.getValue();
        String prodi = prodiComboBox.getValue();
        String alamat = alamatField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 1. Validasi field wajib diisi
        if (nim == null || nim.trim().isEmpty()
                || nama == null || nama.trim().isEmpty()
                || gender == null
                || kelas == null || kelas.trim().isEmpty()
                || prodi == null || prodi.trim().isEmpty()
                || alamat == null || alamat.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()) {

            AlertHelper.showWarning("Peringatan", "Semua field harus diisi lengkap!");
            return;
        }

        // 2. Validasi NIM hanya angka
        boolean nimValid = true;
        for (char c : nim.trim().toCharArray()) {
            if (!Character.isDigit(c)) {
                nimValid = false;
                break;
            }
        }
        if (!nimValid) {
            AlertHelper.showWarning("Peringatan", "NIM harus berupa angka!");
            return;
        }

        // 3. Validasi kesamaan password
        if (!password.equals(confirmPassword)) {
            AlertHelper.showWarning("Peringatan", "Password dan Konfirmasi Password tidak cocok!");
            return;
        }

        // 4. Cek apakah NIM (username) sudah terdaftar
        if (userDAO.isUsernameExists(nim.trim())) {
            AlertHelper.showWarning("Peringatan", "NIM sudah terdaftar!");
            return;
        }

        // 5. Eksekusi penyimpanan dengan transaksi
        Mahasiswa mhs = new Mahasiswa(nim.trim(), nama.trim(), gender, alamat.trim(), kelas, prodi);
        try {
            userDAO.registerMahasiswaTransaction(mhs, password);

            statusLabel.setText("Registrasi Berhasil! Mengalihkan ke halaman login...");
            statusLabel.setStyle("-fx-text-fill: green;");

            Node sourceNode = (Node) event.getSource();
            sourceNode.setDisable(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(actionEvent -> {
                Stage stage = (Stage) sourceNode.getScene().getWindow();
                SceneManager.switchScene(stage, "/view/login/LoginView.fxml", "Login");
            });
            pause.play();

        } catch (SQLException ex) {
            ex.printStackTrace();
            AlertHelper.showError("Kesalahan Server", "Gagal menyimpan data: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneManager.switchScene(stage, "/view/login/LoginView.fxml", "Login");
    }
}
