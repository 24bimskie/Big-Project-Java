package controller.dosen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MulaiAbsenController implements Initializable {

    @FXML private ComboBox<String> comboMataKuliah;
    @FXML private Button btnTampilkan;
    @FXML private Button btnReset;
    @FXML private Button btnSimpan;
    
    @FXML private TableView<?> tableMahasiswa;
    @FXML private TableColumn<?, ?> colNim;
    @FXML private TableColumn<?, ?> colNama;
    @FXML private TableColumn<?, ?> colKehadiran;
    @FXML private TableColumn<?, ?> colKeterangan;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (comboMataKuliah != null) {
            comboMataKuliah.getItems().addAll(
                "Pemrograman Berorientasi Objek - Kelas A", 
                "Struktur Data - Kelas B", 
                "Basis Data - Kelas C"
            );
        }
        
        if (btnTampilkan != null) btnTampilkan.setOnAction(e -> tampilkanData());
        if (btnReset != null) btnReset.setOnAction(e -> resetForm());
        if (btnSimpan != null) btnSimpan.setOnAction(e -> simpanAbsensi());
    }
    
    
    private void tampilkanData() {
        if (comboMataKuliah != null && comboMataKuliah.getValue() != null) {
            System.out.println("✅ Menampilkan daftar mahasiswa untuk: " + comboMataKuliah.getValue());
        } else {
            System.out.println("⚠️ WARNING: Pilih mata kuliah dulu baru klik tampilkan, bro!");
        }
    }
    
    private void resetForm() {
        if (comboMataKuliah != null) {
            comboMataKuliah.getSelectionModel().clearSelection();
        }
        System.out.println("🔄 Form absensi berhasil di-reset.");
    }
    
    private void simpanAbsensi() {
        System.out.println("💾 Menyimpan data kehadiran mahasiswa ke database...");
    }
}