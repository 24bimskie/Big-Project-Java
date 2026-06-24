package controller.dosen;

import dao.DosenDAO;
import dao.JadwalDAO;
import dao.KelasDAO;
import dao.MataKuliahDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import util.AlertHelper;
import model.Dosen;
import model.Jadwal;
import model.Kelas;
import model.MataKuliah;
import model.User;
import util.UserSession;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class JadwalMengajarController implements Initializable {

    @FXML private TableView<JadwalRow> tabelJadwal;
    @FXML private TableColumn<JadwalRow, Integer> colNo;
    @FXML private TableColumn<JadwalRow, String> colHari;
    @FXML private TableColumn<JadwalRow, String> colJam;
    @FXML private TableColumn<JadwalRow, String> colKelas;
    @FXML private TableColumn<JadwalRow, String> colMataKuliah;
    @FXML private TableColumn<JadwalRow, String> colAksi;

    private JadwalDAO jadwalDAO = new JadwalDAO();
    private KelasDAO kelasDAO = new KelasDAO();
    private MataKuliahDAO mataKuliahDAO = new MataKuliahDAO();
    private DosenDAO dosenDAO = new DosenDAO();

    private ObservableList<JadwalRow> jadwalList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colNo.setCellValueFactory(cellData -> cellData.getValue().noProperty().asObject());
        colHari.setCellValueFactory(cellData -> cellData.getValue().hariProperty());
        colJam.setCellValueFactory(cellData -> cellData.getValue().jamProperty());
        colKelas.setCellValueFactory(cellData -> cellData.getValue().namaKelasProperty());
        colMataKuliah.setCellValueFactory(cellData -> cellData.getValue().namaMatkulProperty());
        colAksi.setCellValueFactory(cellData -> new SimpleStringProperty(""));

        colAksi.setCellFactory(param -> new TableCell<JadwalRow, String>() {
            private final Button btnMulaiAbsen = new Button("Mulai Absen");
            private final Button btnRekapAbsen = new Button("Rekap Absen");
            private final HBox pane = new HBox(10, btnMulaiAbsen, btnRekapAbsen);

            {
                btnMulaiAbsen.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6;");
                btnRekapAbsen.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6;");

                btnMulaiAbsen.setOnAction(event -> {
                    JadwalRow row = getTableView().getItems().get(getIndex());
                    bukaMulaiAbsen(row);
                });

                btnRekapAbsen.setOnAction(event -> {
                    JadwalRow row = getTableView().getItems().get(getIndex());
                    bukaRekapAbsen(row);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadData() {
        jadwalList.clear();
        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null) return;

        String dosenIdentifier = currentUser.getUsername();
        Dosen dosen = dosenDAO.getByNidn(dosenIdentifier);
        if (dosen == null) {
            dosen = dosenDAO.getByNamaLengkap(dosenIdentifier);
        }

        // Mendapatkan data jadwal untuk dosen yang sedang login
        List<Jadwal> jadwals = jadwalDAO.getByDosen(dosen != null ? dosen.getNidn() : dosenIdentifier);
        if (jadwals.isEmpty()) {
            jadwals = jadwalDAO.getByDosen(currentUser.getUserId());
        }
        if (jadwals.isEmpty()) {
            jadwals = jadwalDAO.getByDosen(currentUser.getUsername());
        }
        int no = 1;
        for (Jadwal j : jadwals) {
            Kelas kelas = kelasDAO.getById(j.getIdKelas());
            MataKuliah mk = mataKuliahDAO.getByKode(j.getKodeMk());

            String namaKelas = (kelas != null) ? kelas.getNamaKelas() : j.getIdKelas();
            String namaMatkul = (mk != null) ? mk.getNamaMk() : j.getKodeMk();

            jadwalList.add(new JadwalRow(no++, j.getHari(), j.getJamMulai(), namaKelas, namaMatkul, j.getIdJadwal()));
        }
        tabelJadwal.setItems(jadwalList);
    }

    /**
     * Buka halaman Mulai Absen dan kirim data jadwal ke controller tujuan.
     */
    private void bukaMulaiAbsen(JadwalRow row) {
        AlertHelper.showInfo(
                "Absensi Dimulai",
                "Absensi untuk kelas " + row.namaKelasProperty().get() + " pada " + row.namaMatkulProperty().get() + " telah dimulai."
        );
    }

    /**
     * Buka halaman Rekap Absen dan kirim data jadwal ke controller tujuan.
     */
    private void bukaRekapAbsen(JadwalRow row) {
        try {
            StackPane papanKontenTengah = (StackPane) tabelJadwal.getScene().lookup("#papanKontenTengah");
            if (papanKontenTengah != null) {
                URL url = getClass().getResource("/view/dosen/RekapAbsenView.fxml");
                if (url == null) {
                    System.err.println("File FXML tidak ditemukan: RekapAbsenView.fxml");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(url);
                Parent halamanBaru = loader.load();

                // Kirim data jadwal ke controller RekapAbsen
                RekapAbsenController controller = loader.getController();
                controller.initData(row.getIdJadwal(), row.namaKelasProperty().get(), row.namaMatkulProperty().get(),
                                    row.hariProperty().get(), row.jamProperty().get());

                papanKontenTengah.getChildren().clear();
                papanKontenTengah.getChildren().add(halamanBaru);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class untuk representasi baris di TableView
    public static class JadwalRow {
        private final SimpleIntegerProperty no;
        private final SimpleStringProperty hari;
        private final SimpleStringProperty jam;
        private final SimpleStringProperty namaKelas;
        private final SimpleStringProperty namaMatkul;
        private final String idJadwal;

        public JadwalRow(int no, String hari, String jam, String namaKelas, String namaMatkul, String idJadwal) {
            this.no = new SimpleIntegerProperty(no);
            this.hari = new SimpleStringProperty(hari);
            this.jam = new SimpleStringProperty(jam);
            this.namaKelas = new SimpleStringProperty(namaKelas);
            this.namaMatkul = new SimpleStringProperty(namaMatkul);
            this.idJadwal = idJadwal;
        }

        public SimpleIntegerProperty noProperty() { return no; }
        public SimpleStringProperty hariProperty() { return hari; }
        public SimpleStringProperty jamProperty() { return jam; }
        public SimpleStringProperty namaKelasProperty() { return namaKelas; }
        public SimpleStringProperty namaMatkulProperty() { return namaMatkul; }
        public String getIdJadwal() { return idJadwal; }
    }
}
