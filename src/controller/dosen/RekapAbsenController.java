package controller.dosen;

import dao.AbsensiDAO;
import dao.JadwalDAO;
import dao.MahasiswaDAO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import model.Jadwal;

import util.AlertHelper;
import util.PDFExporter;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RekapAbsenController implements Initializable {

    @FXML
    private Label lblMatkul;
    @FXML
    private Label lblKelas;
    @FXML
    private Label lblHariJam;
    @FXML
    private TableView<StudentRekapRow> tableRekap;

    @FXML
    private Button btnKembali;
    @FXML
    private Button btnCetak;
    @FXML
    private Button btnRefresh;

    private String idJadwal;
    private final MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();
    private final AbsensiDAO absensiDAO = new AbsensiDAO();
    private final JadwalDAO jadwalDAO = new JadwalDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (btnKembali != null) {
            btnKembali.setOnAction(e -> kembaliKeJadwal());
        }
        if (btnCetak != null) {
            btnCetak.setOnAction(e -> handleCetak());
        }
        if (btnRefresh != null) {
            btnRefresh.setOnAction(e -> loadRekapData());
        }
    }

    public void initData(String idJadwal, String namaKelas, String namaMatkul, String hari, String jam) {
        this.idJadwal = idJadwal;
        lblKelas.setText(namaKelas);
        lblMatkul.setText(namaMatkul);
        lblHariJam.setText(hari + ", " + jam);

        loadRekapData();
    }

    private void loadRekapData() {
        if (idJadwal == null)
            return;

        tableRekap.getColumns().clear();
        tableRekap.getItems().clear();

        // 1. Setup static columns: No, NIM, Nama
        TableColumn<StudentRekapRow, Integer> colNo = new TableColumn<>("No");
        colNo.setCellValueFactory(cellData -> cellData.getValue().noProperty().asObject());
        colNo.setPrefWidth(50);
        colNo.setStyle("-fx-alignment: CENTER;");

        TableColumn<StudentRekapRow, String> colNim = new TableColumn<>("NIM");
        colNim.setCellValueFactory(cellData -> cellData.getValue().nimProperty());
        colNim.setPrefWidth(120);

        TableColumn<StudentRekapRow, String> colNama = new TableColumn<>("Nama Mahasiswa");
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colNama.setPrefWidth(220);

        tableRekap.getColumns().addAll(colNo, colNim, colNama);

        // 2. Get Schedule & Class ID
        Jadwal j = jadwalDAO.getById(idJadwal);
        if (j == null)
            return;

        int kelasId = 0;
        try {
            kelasId = Integer.parseInt(j.getIdKelas());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // 3. Get students in this class
        List<model.Mahasiswa> mahasiswas = mahasiswaDAO.getByKelasId(kelasId);

        // 4. Get all unique dates when attendance was taken
        List<LocalDate> dates = absensiDAO.getTanggalByJadwal(idJadwal);

        // 5. Dynamically create columns for each attendance date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (LocalDate d : dates) {
            TableColumn<StudentRekapRow, String> colDate = new TableColumn<>(d.format(formatter));
            colDate.setPrefWidth(85);
            colDate.setStyle("-fx-alignment: CENTER;");

            colDate.setCellValueFactory(cellData -> {
                String status = cellData.getValue().getStatusForDate(d);
                return new SimpleStringProperty(status);
            });

            colDate.setCellFactory(column -> new TableCell<StudentRekapRow, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        switch (item) {
                            case "M":
                                setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-alignment: CENTER;");
                                break;
                            case "I":
                                setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-alignment: CENTER;");
                                break;
                            case "S":
                                setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-alignment: CENTER;");
                                break;
                            case "A":
                                setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-alignment: CENTER;");
                                break;
                            default:
                                setStyle("-fx-alignment: CENTER;");
                        }
                    }
                }
            });

            tableRekap.getColumns().add(colDate);
        }

        // 6. Get attendance records for this schedule
        List<model.Absensi> absensiList = absensiDAO.getRekapByJadwal(idJadwal);

        // 7. Populate rows, mapping each attendance record to its date column
        ObservableList<StudentRekapRow> rowList = FXCollections.observableArrayList();
        int no = 1;
        for (model.Mahasiswa m : mahasiswas) {
            int dbMahasiswaId = mahasiswaDAO.getIdByNim(m.getNim());
            String dbMahasiswaIdStr = String.valueOf(dbMahasiswaId);

            StudentRekapRow row = new StudentRekapRow(no++, m.getNim(), m.getNama());

            for (model.Absensi a : absensiList) {
                if (dbMahasiswaIdStr.equals(a.getNim())) {
                    row.addAttendance(a.getTanggal(), a.getStatus());
                }
            }

            rowList.add(row);
        }

        tableRekap.setItems(rowList);
    }

    private void handleCetak() {
        try {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Simpan Rekap Absensi");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            fileChooser.setInitialFileName(
                    "Rekap_" + lblKelas.getText().replace(" ", "_") + ".pdf");

            File file = fileChooser.showSaveDialog(
                    btnCetak.getScene().getWindow());

            if (file == null) {
                return;
            }

            PDFExporter.exportTableViewToPDF(
                    tableRekap,
                    file.getAbsolutePath(),
                    "REKAP ABSENSI\n"
                            + "Mata Kuliah : " + lblMatkul.getText()
                            + "\nKelas : " + lblKelas.getText()
                            + "\nJadwal : " + lblHariJam.getText());

            AlertHelper.showInfo(
                    "Berhasil",
                    "PDF berhasil disimpan di:\n" +
                            file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();

            AlertHelper.showError(
                    "Gagal Export PDF",
                    e.getMessage());
        }
    }

    private void kembaliKeJadwal() {
        try {
            StackPane papanKontenTengah = (StackPane) btnKembali.getScene().lookup("#papanKontenTengah");
            if (papanKontenTengah != null) {
                URL url = getClass().getResource("/view/dosen/JadwalMengajarView.fxml");
                Parent page = FXMLLoader.load(url);
                papanKontenTengah.getChildren().clear();
                papanKontenTengah.getChildren().add(page);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Row class representasi mahasiswa di TableView rekap
    public static class StudentRekapRow {
        private final SimpleIntegerProperty no;
        private final SimpleStringProperty nim;
        private final SimpleStringProperty nama;
        private final Map<LocalDate, String> attendanceMap = new HashMap<>();

        public StudentRekapRow(int no, String nim, String nama) {
            this.no = new SimpleIntegerProperty(no);
            this.nim = new SimpleStringProperty(nim);
            this.nama = new SimpleStringProperty(nama);
        }

        public SimpleIntegerProperty noProperty() {
            return no;
        }

        public SimpleStringProperty nimProperty() {
            return nim;
        }

        public SimpleStringProperty namaProperty() {
            return nama;
        }

        public void addAttendance(LocalDate date, String status) {
            String code = "-";
            if (status != null) {
                if (status.equalsIgnoreCase("Hadir"))
                    code = "M";
                else if (status.equalsIgnoreCase("Izin"))
                    code = "I";
                else if (status.equalsIgnoreCase("Sakit"))
                    code = "S";
                else if (status.equalsIgnoreCase("Alpha"))
                    code = "A";
            }
            attendanceMap.put(date, code);
        }

        public String getStatusForDate(LocalDate date) {
            return attendanceMap.getOrDefault(date, "-");
        }
    }
}