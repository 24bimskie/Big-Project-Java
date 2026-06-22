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

public class MulaiAbsenController implements Initializable {

    @FXML private Label lblMatkul;
    @FXML private Label lblKelas;
    @FXML private Label lblHariJam;
    @FXML private Label lblJumlahSiswa;
    @FXML private DatePicker datePickerTanggal;
    @FXML private TableView<SiswaAbsenRow> tableMahasiswa;
    
    @FXML private TableColumn<SiswaAbsenRow, Integer> colNo;
    @FXML private TableColumn<SiswaAbsenRow, String> colNim;
    @FXML private TableColumn<SiswaAbsenRow, String> colNama;
    @FXML private TableColumn<SiswaAbsenRow, String> colKehadiran;
    @FXML private TableColumn<SiswaAbsenRow, String> colKeterangan;

    @FXML private Button btnReset;
    @FXML private Button btnSimpan;
    @FXML private Button btnKembali;

    private String idJadwal;
    private final MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();
    private final AbsensiDAO absensiDAO = new AbsensiDAO();
    private final JadwalDAO jadwalDAO = new JadwalDAO();
    private final ObservableList<SiswaAbsenRow> siswaList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        colNo.setCellValueFactory(cellData -> cellData.getValue().noProperty().asObject());
        colNim.setCellValueFactory(cellData -> cellData.getValue().nimProperty());
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        
        setupKehadiranColumn();
        setupKeteranganColumn();

        // Listener for date change
        datePickerTanggal.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadSiswaDanAbsensi();
            }
        });

        // Set action handlers
        btnReset.setOnAction(e -> resetKehadiran());
        btnSimpan.setOnAction(e -> simpanAbsensi());
        if (btnKembali != null) {
            btnKembali.setOnAction(e -> kembaliKeJadwal());
        }
    }

    public void initData(String idJadwal, String namaKelas, String namaMatkul, String hari, String jam) {
        this.idJadwal = idJadwal;
        lblKelas.setText(namaKelas);
        lblMatkul.setText(namaMatkul);
        lblHariJam.setText(hari + ", " + jam);
        
        // Default to current date
        datePickerTanggal.setValue(LocalDate.now());
        
        // Initially load student list and attendance
        loadSiswaDanAbsensi();
    }

    private void setupKehadiranColumn() {
        colKehadiran.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colKehadiran.setCellFactory(param -> new TableCell<SiswaAbsenRow, String>() {
            private final ComboBox<String> choiceBox = new ComboBox<>();
            {
                choiceBox.getItems().addAll("Hadir", "Izin", "Sakit", "Alpha");
                choiceBox.getStyleClass().add("combo-box");
                choiceBox.setStyle("-fx-pref-height: 30px; -fx-font-size: 13px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                choiceBox.setPrefWidth(120);
                choiceBox.setOnAction(e -> {
                    SiswaAbsenRow row = getTableView().getItems().get(getIndex());
                    if (row != null && choiceBox.getValue() != null) {
                        row.setStatus(choiceBox.getValue());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    choiceBox.setValue(item);
                    setGraphic(choiceBox);
                }
            }
        });
    }

    private void setupKeteranganColumn() {
        colKeterangan.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        colKeterangan.setCellFactory(param -> new TableCell<SiswaAbsenRow, String>() {
            private final TextField textField = new TextField();
            {
                textField.getStyleClass().add("text-field");
                textField.setStyle("-fx-pref-height: 30px; -fx-font-size: 13px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                
                // Save value when user stops editing or typing
                textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        SiswaAbsenRow row = getTableView().getItems().get(getIndex());
                        if (row != null) {
                            row.setKeterangan(textField.getText());
                        }
                    }
                });
                textField.setOnKeyReleased(e -> {
                    SiswaAbsenRow row = getTableView().getItems().get(getIndex());
                    if (row != null) {
                        row.setKeterangan(textField.getText());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    textField.setText(item == null ? "" : item);
                    setGraphic(textField);
                }
            }
        });
    }

    private void loadSiswaDanAbsensi() {
        if (idJadwal == null) return;
        siswaList.clear();
        
        // 1. Get schedule to know the class ID
        Jadwal j = jadwalDAO.getById(idJadwal);
        if (j == null) return;
        
        int kelasId = 0;
        try {
            kelasId = Integer.parseInt(j.getIdKelas());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        // 2. Get all students in this class
        List<model.Mahasiswa> mahasiswas = mahasiswaDAO.getByKelasId(kelasId);
        
        // 3. Get attendance list on selected date
        LocalDate tanggal = datePickerTanggal.getValue();
        List<model.Absensi> absensiList = absensiDAO.getByJadwalAndTanggal(idJadwal, tanggal);
        
        int no = 1;
        for (model.Mahasiswa m : mahasiswas) {
            int dbMahasiswaId = mahasiswaDAO.getIdByNim(m.getNim());
            String dbMahasiswaIdStr = String.valueOf(dbMahasiswaId);
            
            // Search for existing attendance record
            model.Absensi matchingAbs = null;
            for (model.Absensi a : absensiList) {
                if (dbMahasiswaIdStr.equals(a.getNim())) {
                    matchingAbs = a;
                    break;
                }
            }
            
            String status = "Hadir"; // Default
            String keterangan = "";
            String idAbsensi = null;
            
            if (matchingAbs != null) {
                status = matchingAbs.getStatus();
                keterangan = matchingAbs.getKeterangan();
                idAbsensi = matchingAbs.getIdAbsensi();
            }
            
            siswaList.add(new SiswaAbsenRow(no++, m.getNim(), m.getNama(), dbMahasiswaIdStr, status, keterangan, idAbsensi));
        }
        
        tableMahasiswa.setItems(siswaList);
        lblJumlahSiswa.setText("Total: " + siswaList.size() + " Mahasiswa");
    }

    private void simpanAbsensi() {
        if (siswaList.isEmpty()) {
            AlertHelper.showWarning("Absensi Kosong", "Tidak ada mahasiswa untuk diabsen.");
            return;
        }
        
        LocalDate tanggal = datePickerTanggal.getValue();
        if (tanggal == null) {
            AlertHelper.showWarning("Pilih Tanggal", "Silakan pilih tanggal absensi terlebih dahulu.");
            return;
        }
        
        try {
            for (SiswaAbsenRow row : siswaList) {
                model.Absensi a = new model.Absensi();
                a.setIdJadwal(idJadwal);
                a.setNim(row.getIdMahasiswa()); // aktor_id in db
                a.setTanggal(tanggal);
                a.setStatus(row.getStatus());
                a.setKeterangan(row.getKeterangan());
                
                if (row.getIdAbsensi() != null && !row.getIdAbsensi().isEmpty()) {
                    a.setIdAbsensi(row.getIdAbsensi());
                    absensiDAO.update(a);
                } else {
                    absensiDAO.insert(a);
                }
            }
            
            AlertHelper.showInfo("Sukses", "Absensi berhasil disimpan!");
            loadSiswaDanAbsensi(); // Reload to fetch generated primary keys
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Gagal Menyimpan", "Terjadi kesalahan saat menyimpan data absensi: " + e.getMessage());
        }
    }

    private void resetKehadiran() {
        boolean konfirmasi = AlertHelper.showConfirmation("Reset Kehadiran", "Apakah Anda yakin ingin mereset seluruh status kehadiran menjadi Hadir?");
        if (konfirmasi) {
            for (SiswaAbsenRow row : siswaList) {
                row.setStatus("Hadir");
                row.setKeterangan("");
            }
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

    // Row class representasi mahasiswa di TableView
    public static class SiswaAbsenRow {
        private final SimpleIntegerProperty no;
        private final SimpleStringProperty nim;
        private final SimpleStringProperty nama;
        private final String idMahasiswa; // Database ID (mahasiswa.id)
        private final SimpleStringProperty status;
        private final SimpleStringProperty keterangan;
        private String idAbsensi;

        public SiswaAbsenRow(int no, String nim, String nama, String idMahasiswa, String status, String keterangan, String idAbsensi) {
            this.no = new SimpleIntegerProperty(no);
            this.nim = new SimpleStringProperty(nim);
            this.nama = new SimpleStringProperty(nama);
            this.idMahasiswa = idMahasiswa;
            this.status = new SimpleStringProperty(status);
            this.keterangan = new SimpleStringProperty(keterangan);
            this.idAbsensi = idAbsensi;
        }

        public int getNo() { return no.get(); }
        public SimpleIntegerProperty noProperty() { return no; }
        public void setNo(int no) { this.no.set(no); }

        public String getNim() { return nim.get(); }
        public SimpleStringProperty nimProperty() { return nim; }

        public String getNama() { return nama.get(); }
        public SimpleStringProperty namaProperty() { return nama; }

        public String getIdMahasiswa() { return idMahasiswa; }

        public String getStatus() { return status.get(); }
        public SimpleStringProperty statusProperty() { return status; }
        public void setStatus(String status) { this.status.set(status); }

        public String getKeterangan() { return keterangan.get(); }
        public SimpleStringProperty keteranganProperty() { return keterangan; }
        public void setKeterangan(String keterangan) { this.keterangan.set(keterangan); }

        public String getIdAbsensi() { return idAbsensi; }
        public void setIdAbsensi(String idAbsensi) { this.idAbsensi = idAbsensi; }
    }
}