package controller.dosen;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;

public class RekapAbsenController implements Initializable {

    @FXML private ComboBox<String> comboMataKuliah;
    @FXML private DatePicker dateTanggal;
    @FXML private Button btnFilter;
    @FXML private Button btnExportPdf;
    
    @FXML private TableView<?> tableRekap;
    @FXML private TableColumn<?, ?> colNim;
    @FXML private TableColumn<?, ?> colNama;
    @FXML private TableColumn<?, ?> colHadir;
    @FXML private TableColumn<?, ?> colIzin;
    @FXML private TableColumn<?, ?> colSakit;
    @FXML private TableColumn<?, ?> colAlpha;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (comboMataKuliah != null) {
            comboMataKuliah.getItems().addAll(
                "Semua Mata Kuliah", 
                "Pemrograman Berorientasi Objek", 
                "Struktur Data"
            );
        }
        
        if (btnFilter != null) btnFilter.setOnAction(e -> filterData());
        if (btnExportPdf != null) btnExportPdf.setOnAction(e -> exportToPdf());
    }
    
    private void filterData() {
        System.out.println("Memfilter rekap absensi...");
        // TODO: Ambil rekap absensi dari database sesuai jadwal & tanggal, update TableView
    }
    
    private void exportToPdf() {
        System.out.println("Mengekspor rekap absensi ke PDF menggunakan Library khusus...");
        // TODO: Integrasikan library PDF (misalnya iText atau Apache PDFBox) untuk generate PDF
    }
}
