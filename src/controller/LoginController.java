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
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.control.Label;

/**
 * Controller untuk halaman Login.
 * Menangani autentikasi dan redirect ke dashboard sesuai role.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            statusLabel.setText("Username dan Password tidak boleh kosong!");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Coba autentikasi menggunakan database
        User loggedInUser = userDAO.login(username, password);
        
        // Jika login gagal, tampilkan pesan error di label dan reset form
        if (loggedInUser == null) {
            statusLabel.setText("Password atau username tidak sesuai!");
            statusLabel.setStyle("-fx-text-fill: red;");
            
            PauseTransition resetPause = new PauseTransition(Duration.seconds(1));
            resetPause.setOnFinished(e -> {
                usernameField.clear();
                passwordField.clear();
                statusLabel.setText("");
            });
            resetPause.play();
            return;
        }

        UserSession.setCurrentUser(loggedInUser);

        statusLabel.setText("Login Berhasil! ✔");
        statusLabel.setStyle("-fx-text-fill: green;");

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Redirect sesuai role
            String role = UserSession.getCurrentUser().getRole();
            if ("Dosen".equalsIgnoreCase(role)) {
                SceneManager.switchScene(stage, "/view/dosen/DosenDashboardView.fxml", "Dashboard Dosen");
            } else if ("Mahasiswa".equalsIgnoreCase(role)) {
                SceneManager.switchScene(stage, "/view/mahasiswa/MahasiswaDashboardView.fxml", "Dashboard Mahasiswa");
            } else {
                SceneManager.switchScene(stage, "/view/admin/AdminDashboardView.fxml", "Admin Dashboard");
            }
        });
        pause.play();
    }

    @FXML
    private void handleGoToRegister(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneManager.switchScene(stage, "/view/login/RegisterView.fxml", "Register");
    }
}
