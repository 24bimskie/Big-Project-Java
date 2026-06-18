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
import util.UserSession;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.control.Label;

/**
 * Controller untuk halaman Registrasi.
 */
public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

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

        // Bypass insert ke database seperti yang diminta
        User dummyUser = new User("1", username, password, role);
        UserSession.setCurrentUser(dummyUser);

        statusLabel.setText("Registrasi Berhasil! Mengalihkan ke halaman login...");
        statusLabel.setStyle("-fx-text-fill: green;");

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneManager.switchScene(stage, "/view/login/LoginView.fxml", "Login");
        });
        pause.play();
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneManager.switchScene(stage, "/view/login/LoginView.fxml", "Login");
    }
}
