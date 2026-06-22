package controller.mahasiswa;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;
import util.UserSession;

public class MahasiswaProfilController {

    @FXML
    private Label txtNamaBesar;
    @FXML
    private Label txtRole;
    @FXML
    private Label lblNim;
    @FXML
    private Label lblNama;

    @FXML
    public void initialize() {
        // Ambil token data user aktif dari global session
        User user = UserSession.getCurrentUser();
        
        if (user != null) {
            // Tembakkan data session ke dalam label FXML secara real-time
            if (txtNamaBesar != null) txtNamaBesar.setText(user.getUsername());
            if (txtRole != null) txtRole.setText(user.getRole().toUpperCase() + " AKTIF");
            if (lblNama != null) lblNama.setText(user.getUsername());
            if (lblNim != null) lblNim.setText(user.getUserId());
            
            System.out.println("[ProfilController] Sinkronisasi data sesi " + user.getUsername() + " sukses.");
        } else {
            System.out.println("[ProfilController] Peringatan: Sesi pengguna kosong!");
        }
    }
}