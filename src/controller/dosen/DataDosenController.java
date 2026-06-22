package controller.dosen;

import dao.DosenDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import model.Dosen;
import model.User;
import util.UserSession;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller untuk halaman Data Dosen.
 * Menampilkan profil dosen yang sedang login berdasarkan data dari database.
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

    private DosenDAO dosenDAO = new DosenDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProfilDosen();
    }

    /**
     * Memuat data profil dosen yang sedang login dari database.
     * Mencocokkan username dari session dengan data di tabel dosen
     * (melalui nidn atau nama_lengkap).
     */
    private void loadProfilDosen() {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            setStatus("⚠ Tidak ada user yang login.");
            return;
        }

        String username = currentUser.getUsername();

        // Cari dosen: coba cocokkan via nidn dulu, lalu via nama_lengkap
        Dosen dosen = dosenDAO.getByNidn(username);
        if (dosen == null) {
            dosen = dosenDAO.getByNamaLengkap(username);
        }

        if (dosen != null) {
            // Tampilkan data profil dosen
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

            setStatus("✅ Data profil berhasil dimuat.");
            System.out.println("✅ Profil dosen dimuat: " + dosen.getNamaLengkap());
        } else {
            // Dosen tidak ditemukan, tampilkan info dari session saja
            lblNamaDosen.setText(username);
            lblFakultasBadge.setText("Belum diatur");
            lblNidn.setText("-");
            lblNama.setText(username);
            lblEmail.setText("-");
            lblFakultas.setText("-");
            lblUsername.setText(username);
            lblRole.setText(currentUser.getRole() != null ? currentUser.getRole() : "Dosen");

            setStatus("⚠ Data dosen belum lengkap di database. Hubungi admin untuk melengkapi.");
            System.out.println("⚠ Dosen tidak ditemukan di tabel dosen untuk username: " + username);
        }
    }

    private void setStatus(String message) {
        if (lblStatus != null) {
            lblStatus.setText(message);
        }
    }
}
