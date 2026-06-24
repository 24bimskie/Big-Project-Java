package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Main entry point untuk Sistem Absensi Mahasiswa.
 * Menampilkan halaman login sebagai tampilan awal.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login/LoginView.fxml"));
            if (root == null) {
                throw new IllegalStateException("Tidak dapat menemukan file FXML: /view/login/LoginView.fxml");
            }

            Scene scene = new Scene(root);
            if (getClass().getResource("/style/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
            }
            

            primaryStage.setTitle("Sistem Absensi Mahasiswa");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Startup Error");
            alert.setHeaderText("Aplikasi gagal dimulai");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
