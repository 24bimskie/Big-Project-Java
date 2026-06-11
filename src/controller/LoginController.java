package controller;

import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import util.AlertHelper;
import util.SceneManager;
import util.UserSession;

/**
 * Controller untuk halaman Login.
 * Menangani autentikasi dan redirect ke dashboard sesuai role.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            AlertHelper.showWarning("Peringatan", "Username dan Password tidak boleh kosong!");
            return;
        }

        User user = userDAO.login(username, password);

        if (user != null) {
            UserSession.setCurrentUser(user);
            AlertHelper.showInfo("Login Berhasil", "Selamat datang, " + user.getUsername() + "!");
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            switch (user.getRole().toLowerCase()) {
                case "admin":
                    SceneManager.switchScene(stage, "/view/admin/AdminDashboardView.fxml", "Admin Dashboard");
                    break;
                case "dosen":
                    SceneManager.switchScene(stage, "/view/dosen/DosenDashboardView.fxml", "Dosen Dashboard");
                    break;
                case "mahasiswa":
                    SceneManager.switchScene(stage, "/view/mahasiswa/MahasiswaDashboardView.fxml", "Mahasiswa Dashboard");
                    break;
                default:
                    AlertHelper.showError("Error", "Role tidak dikenali: " + user.getRole());
            }
        } else {
            AlertHelper.showError("Login Gagal", "Username atau Password salah!");
        }
    }

    @FXML
    private void handleGoToRegister(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneManager.switchScene(stage, "/view/login/RegisterView.fxml", "Register");
    }
}
