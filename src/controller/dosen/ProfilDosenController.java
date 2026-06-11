package controller.dosen;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfilDosenController implements Initializable {

    @FXML private TextField txtNidn;
    @FXML private TextField txtNama;
    @FXML private TextField txtEmail;
    @FXML private TextField txtFakultas;
    @FXML private PasswordField txtPassword;
    
    @FXML private Button btnUbahFoto;
    @FXML private Button btnSimpan;
    @FXML private Button btnBatal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (btnSimpan != null) btnSimpan.setOnAction(e -> simpanProfil());
        if (btnBatal != null) btnBatal.setOnAction(e -> batalUbah());
        if (btnUbahFoto != null) btnUbahFoto.setOnAction(e -> ubahFoto());
        
        loadProfilDosen();
    }
    
    private void loadProfilDosen() {
        // TODO: Ambil data dari database untuk dosen yang sedang login
        System.out.println("Memuat data profil dosen...");
    }
    
    private void simpanProfil() {
        System.out.println("Menyimpan pembaruan profil ke database...");
        // TODO: Update tabel Dosen dengan NIDN, Nama, Email, dan Password Baru
    }
    
    private void batalUbah() {
        System.out.println("Membatalkan perubahan, me-reload data awal...");
        loadProfilDosen(); // Reload existing data
    }
    
    private void ubahFoto() {
        System.out.println("Membuka dialog FileChooser untuk foto...");
        // TODO: Logika ganti gambar profil
    }
}
