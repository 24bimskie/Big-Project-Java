package controller.dosen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RekapAbsenController implements Initializable {

    @FXML private ComboBox<String> comboMataKuliahRekap;
    @FXML private DatePicker datePickerRekap;
    @FXML private Button btnFilter;
    @FXML private Button btnExportPdf;

    @FXML private TableView<?> tableRekap;
    @FXML private TableColumn<?, ?> colRekapNim;
    @FXML private TableColumn<?, ?> colRekapNama;
    @FXML private TableColumn<?, ?> colRekapHadir;
    @FXML private TableColumn<?, ?> colRekapIzin;
    @FXML private TableColumn<?, ?> colRekapSakit;
    @FXML private TableColumn<?, ?> colRekapAlpha;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (comboMataKuliahRekap != null) {
            comboMataKuliahRekap.getItems().addAll(
                "Pemrograman Berorientasi Objek - Kelas A", 
                "Struktur Data - Kelas B", 
                "Basis Data - Kelas C"
            );
        }

        if (btnFilter != null) {
            btnFilter.setOnAction(e -> System.out.println("🔍 Memfilter data rekap absensi..."));
        }
        if (btnExportPdf != null) {
            btnExportPdf.setOnAction(e -> System.out.println("📄 Proses export rekap absensi ke PDF..."));
        }
    }

}