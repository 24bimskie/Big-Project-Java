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
import model.Jadwal;
import util.AlertHelper;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class RekapAbsenController implements Initializable {

    @FXML private Label lblMatkul;
    @FXML private Label lblKelas;
    @FXML private Label lblHariJam;
    @FXML private TableView<StudentRekapRow> tableRekap;
    
    @FXML private Button btnKembali;
    @FXML private Button btnCetak;
    @FXML private Button btnRefresh;

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
        if (idJadwal == null) return;
        
        tableRekap.getColumns().clear();
        tableRekap.getItems().clear();

        // 1. Setup static columns: No, NIM, Nama, Status, Keterangan
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

        TableColumn<StudentRekapRow, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colStatus.setPrefWidth(100);
        colStatus.setStyle("-fx-alignment: CENTER;");

        TableColumn<StudentRekapRow, String> colKeterangan = new TableColumn<>("Keterangan");
        colKeterangan.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        colKeterangan.setPrefWidth(220);

        tableRekap.getColumns().addAll(colNo, colNim, colNama, colStatus, colKeterangan);

        // 2. Get Schedule & Class ID
        Jadwal j = jadwalDAO.getById(idJadwal);
        if (j == null) return;

        int kelasId = 0;
        try {
            kelasId = Integer.parseInt(j.getIdKelas());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // 3. Get students in this class
        List<model.Mahasiswa> mahasiswas = mahasiswaDAO.getByKelasId(kelasId);

        // 4. Get attendance records for this schedule
        List<model.Absensi> absensiList = absensiDAO.getRekapByJadwal(idJadwal);

        // 5. Populate rows directly from absensi table data
        ObservableList<StudentRekapRow> rowList = FXCollections.observableArrayList();
        int no = 1;
        for (model.Mahasiswa m : mahasiswas) {
            int dbMahasiswaId = mahasiswaDAO.getIdByNim(m.getNim());
            String dbMahasiswaIdStr = String.valueOf(dbMahasiswaId);

            StudentRekapRow row = new StudentRekapRow(no++, m.getNim(), m.getNama());
            boolean hasAttendance = false;

            for (model.Absensi a : absensiList) {
                if (dbMahasiswaIdStr.equals(a.getNim())) {
                    row.setStatus(a.getStatus());
                    row.setKeterangan(a.getKeterangan());
                    row.setTanggal(a.getTanggal());
                    hasAttendance = true;
                    break;
                }
            }

            if (!hasAttendance) {
                row.setStatus("-");
                row.setKeterangan("-");
            }

            rowList.add(row);
        }

        tableRekap.setItems(rowList);
    }

    private void handleCetak() {
        AlertHelper.showInfo("Simulasi Cetak", "Mengekspor rekap absensi ke format cetak...\n"
                + "Data untuk Kelas " + lblKelas.getText() + " (" + lblMatkul.getText() + ") siap dikirim ke printer.");
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
        private final SimpleStringProperty status;
        private final SimpleStringProperty keterangan;
        private LocalDate tanggal;

        public StudentRekapRow(int no, String nim, String nama) {
            this.no = new SimpleIntegerProperty(no);
            this.nim = new SimpleStringProperty(nim);
            this.nama = new SimpleStringProperty(nama);
            this.status = new SimpleStringProperty("-");
            this.keterangan = new SimpleStringProperty("-");
        }

        public SimpleIntegerProperty noProperty() { return no; }
        public SimpleStringProperty nimProperty() { return nim; }
        public SimpleStringProperty namaProperty() { return nama; }
        public SimpleStringProperty statusProperty() { return status; }
        public SimpleStringProperty keteranganProperty() { return keterangan; }

        public void setStatus(String status) { this.status.set(status == null ? "-" : status); }
        public void setKeterangan(String keterangan) { this.keterangan.set(keterangan == null ? "-" : keterangan); }
        public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
        public LocalDate getTanggal() { return tanggal; }
    }
}