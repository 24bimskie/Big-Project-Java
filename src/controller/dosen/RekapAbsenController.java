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

import util.AlertHelper;
import util.PDFExporter;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

        if (tableRekap != null) {
            tableRekap.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableRekap.setMinWidth(1100);
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

        TableColumn<StudentRekapRow, Integer> colNo = new TableColumn<>("No");
        colNo.setCellValueFactory(cellData -> cellData.getValue().noProperty().asObject());
        colNo.setPrefWidth(50);
        colNo.setStyle("-fx-alignment: CENTER;");

        TableColumn<StudentRekapRow, String> colTanggal = new TableColumn<>("Tanggal");
        colTanggal.setCellValueFactory(cellData -> cellData.getValue().tanggalProperty());
        colTanggal.setPrefWidth(100);

        TableColumn<StudentRekapRow, String> colHariJam = new TableColumn<>("Hari / Jam");
        colHariJam.setCellValueFactory(cellData -> cellData.getValue().hariJamProperty());
        colHariJam.setPrefWidth(140);

        TableColumn<StudentRekapRow, String> colMatkul = new TableColumn<>("Mata Kuliah");
        colMatkul.setCellValueFactory(cellData -> cellData.getValue().mataKuliahProperty());
        colMatkul.setPrefWidth(200);

        TableColumn<StudentRekapRow, String> colKelas = new TableColumn<>("Kelas");
        colKelas.setCellValueFactory(cellData -> cellData.getValue().kelasProperty());
        colKelas.setPrefWidth(90);

        TableColumn<StudentRekapRow, String> colNim = new TableColumn<>("NIM");
        colNim.setCellValueFactory(cellData -> cellData.getValue().nimProperty());
        colNim.setPrefWidth(120);

        TableColumn<StudentRekapRow, String> colNama = new TableColumn<>("Nama Lengkap");
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colNama.setPrefWidth(220);

        TableColumn<StudentRekapRow, String> colKehadiran = new TableColumn<>("Kehadiran");
        colKehadiran.setCellValueFactory(cellData -> cellData.getValue().kehadiranProperty());
        colKehadiran.setPrefWidth(110);

        TableColumn<StudentRekapRow, String> colKeterangan = new TableColumn<>("Keterangan");
        colKeterangan.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        colKeterangan.setPrefWidth(220);

        tableRekap.getColumns().addAll(colNo, colTanggal, colHariJam, colMatkul, colKelas, colNim, colNama, colKehadiran, colKeterangan);

        // Filter langsung di query SQL berdasarkan jadwal_id -- bukan mencocokkan
        // nama kelas/mata kuliah secara string, yang rapuh dan bisa meleset.
        List<model.Absensi> absensiList = absensiDAO.getRekapByJadwal(idJadwal);

        ObservableList<StudentRekapRow> rowList = FXCollections.observableArrayList();
        int no = 1;
        for (model.Absensi a : absensiList) {
            rowList.add(new StudentRekapRow(
                    no++,
                    a.getTanggal(),
                    a.getHariJam(),
                    a.getMataKuliah(),
                    a.getKelas(),
                    a.getNim(),
                    a.getNamaLengkap(),
                    a.getKehadiran(),
                    a.getKeterangan()));
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
        private final SimpleStringProperty tanggal;
        private final SimpleStringProperty hariJam;
        private final SimpleStringProperty mataKuliah;
        private final SimpleStringProperty kelas;
        private final SimpleStringProperty nim;
        private final SimpleStringProperty nama;
        private final SimpleStringProperty kehadiran;
        private final SimpleStringProperty keterangan;

        public StudentRekapRow(int no, LocalDate tanggal, String hariJam, String mataKuliah, String kelas,
                               String nim, String nama, String kehadiran, String keterangan) {
            this.no = new SimpleIntegerProperty(no);
            this.tanggal = new SimpleStringProperty(tanggal == null ? "-" : tanggal.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            this.hariJam = new SimpleStringProperty(hariJam == null ? "-" : hariJam);
            this.mataKuliah = new SimpleStringProperty(mataKuliah == null ? "-" : mataKuliah);
            this.kelas = new SimpleStringProperty(kelas == null ? "-" : kelas);
            this.nim = new SimpleStringProperty(nim == null ? "-" : nim);
            this.nama = new SimpleStringProperty(nama == null ? "-" : nama);
            this.kehadiran = new SimpleStringProperty(kehadiran == null ? "-" : kehadiran);
            this.keterangan = new SimpleStringProperty(keterangan == null ? "-" : keterangan);
        }

        public SimpleIntegerProperty noProperty() {
            return no;
        }

        public SimpleStringProperty tanggalProperty() {
            return tanggal;
        }

        public SimpleStringProperty hariJamProperty() {
            return hariJam;
        }

        public SimpleStringProperty mataKuliahProperty() {
            return mataKuliah;
        }

        public SimpleStringProperty kelasProperty() {
            return kelas;
        }

        public SimpleStringProperty nimProperty() {
            return nim;
        }

        public SimpleStringProperty namaProperty() {
            return nama;
        }

        public SimpleStringProperty kehadiranProperty() {
            return kehadiran;
        }

        public SimpleStringProperty keteranganProperty() {
            return keterangan;
        }
    }
}