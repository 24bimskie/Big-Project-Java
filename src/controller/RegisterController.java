package controller;

import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import util.AlertHelper;
import util.SceneManager;

/**
 * Controller untuk halaman Registrasi.
 */
public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() || 
            role == null || role.trim().isEmpty()) {
            AlertHelper.showWarning("Peringatan", "Semua field harus diisi!");
            return;
        }

        // Buat user baru dengan dummy ID (nanti AUTO_INCREMENT di DB)
        User newUser = new User(null, username, password, role);
        try {
            userDAO.insert(newUser);
            AlertHelper.showInfo("Registrasi Berhasil", "Akun berhasil didaftarkan. Silakan login.");
            handleBackToLogin(event);
        } catch (Exception e) {
            AlertHelper.showError("Registrasi Gagal", "Terjadi kesalahan saat mendaftar: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneManager.switchScene(stage, "/view/login/LoginView.fxml", "Login");
    }
}
