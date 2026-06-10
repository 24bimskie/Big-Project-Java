package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class untuk navigasi antar halaman (scene switching).
 */
public class SceneManager {

    /**
     * Pindah ke halaman lain.
     * @param stage Stage yang aktif
     * @param fxmlPath Path ke file FXML (contoh: "/view/admin/AdminDashboardView.fxml")
     * @param title Judul window
     */
    public static void switchScene(Stage stage, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneManager.class.getResource("/style/style.css").toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
