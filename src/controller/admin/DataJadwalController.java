package controller.admin;

import dao.DosenDAO;
import dao.JadwalDAO;
import dao.KelasDAO;
import dao.MataKuliahDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Dosen;
import model.Jadwal;
import model.Kelas;
import model.MataKuliah;
import util.AlertHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Controller untuk use case: Input Data Jadwal & Lihat Jadwal.
 * Admin dapat mengelola jadwal perkuliahan (dosen, matkul, kelas, waktu,
 * ruangan).
 */
public class DataJadwalController implements Initializable {

    // ===== FXML Bindings — Tab Pane =====

    @FXML
    private TabPane tabPane;

    // ===== FXML Bindings — TableView =====

    @FXML
    private TableView<Jadwal> tableJadwal;

    @FXML
    private TableColumn<Jadwal, String> colIdJadwal;

    @FXML
    private TableColumn<Jadwal, String> colKodeMk;

    @FXML
    private TableColumn<Jadwal, String> colNidn;

    @FXML
    private TableColumn<Jadwal, String> colIdKelas;

    @FXML
    private TableColumn<Jadwal, String> colHari;

    @FXML
    private TableColumn<Jadwal, String> colJamMulai;

    @FXML
    private TableColumn<Jadwal, String> colJamSelesai;

    @FXML
    private TableColumn<Jadwal, String> colRuangan;

    @FXML
    private TableColumn<Jadwal, String> colTahunAkademik;

    @FXML
    private TableColumn<Jadwal, String> colSemester;

    // ===== FXML Bindings — Form Input =====

    @FXML
    private TextField fieldIdJadwal;

    @FXML
    private ComboBox<MataKuliah> comboMataKuliah;

    @FXML
    private ComboBox<Dosen> comboDosen;

    @FXML
    private ComboBox<Kelas> comboKelas;

    @FXML
    private ComboBox<String> comboHari;

    @FXML
    private TextField fieldJamMulai;

    @FXML
    private TextField fieldJamSelesai;

    @FXML
    private TextField fieldRuangan;

    @FXML
    private TextField fieldTahunAkademik;

    @FXML
    private ComboBox<String> comboSemester;

    // ===== FXML Bindings — Filter & Kontrol =====

    @FXML
    private ComboBox<String> filterHari;

    @FXML
    private ComboBox<Dosen> filterDosen;

    @FXML
    private ComboBox<Kelas> filterKelas;

    @FXML
    private Button btnResetFilter;

    @FXML
    private Button btnTambah;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnHapus;

    @FXML
    private Button btnBatal;

    // ===== State =====

    private final JadwalDAO jadwalDAO = new JadwalDAO();
    private final DosenDAO dosenDAO = new DosenDAO();
    private final MataKuliahDAO mataKuliahDAO = new MataKuliahDAO();
    private final KelasDAO kelasDAO = new KelasDAO();

    private final ObservableList<Jadwal> jadwalList = FXCollections.observableArrayList();
    private FilteredList<Jadwal> filteredList;

    private boolean isEditMode = false;

    // ===== Lifecycle =====

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupComboBoxes();
        setupFilter();
        loadData();
        setupTableSelectionListener();
        setEditMode(false);
    }

    // ===== Setup =====

    /** Mapping kolom TableView ke properti model Jadwal */
    private void setupTable() {
        colIdJadwal.setCellValueFactory(new PropertyValueFactory<>("idJadwal"));
        colKodeMk.setCellValueFactory(new PropertyValueFactory<>("kodeMk"));
        colNidn.setCellValueFactory(new PropertyValueFactory<>("nidn"));
        colIdKelas.setCellValueFactory(new PropertyValueFactory<>("idKelas"));
        colHari.setCellValueFactory(new PropertyValueFactory<>("hari"));
        colJamMulai.setCellValueFactory(new PropertyValueFactory<>("jamMulai"));
        colJamSelesai.setCellValueFactory(new PropertyValueFactory<>("jamSelesai"));
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("ruangan"));
        colTahunAkademik.setCellValueFactory(new PropertyValueFactory<>("tahunAkademik"));
        colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
    }

    /** Mengisi semua ComboBox dengan data dari DAO */
    private void setupComboBoxes() {
        // Hari
        ObservableList<String> hariList = FXCollections.observableArrayList(
                "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu");
        comboHari.setItems(hariList);
        filterHari.setItems(
                FXCollections.observableArrayList("Semua", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"));
        filterHari.setValue("Semua");

        // Semester
        comboSemester.setItems(FXCollections.observableArrayList("Ganjil", "Genap"));

        // Mata Kuliah
        List<MataKuliah> mkList = mataKuliahDAO.getAll();
        if (mkList != null) {
            comboMataKuliah.setItems(FXCollections.observableArrayList(mkList));
        }
        // Tampilkan nama + kode MK di ComboBox
        comboMataKuliah.setConverter(new javafx.util.StringConverter<MataKuliah>() {
            @Override
            public String toString(MataKuliah mk) {
                return mk == null ? "" : mk.getKodeMk() + " - " + mk.getNamaMk();
            }

            @Override
            public MataKuliah fromString(String s) {
                return null;
            }
        });

        // Dosen
        List<Dosen> dosenList = dosenDAO.getAll();
        ObservableList<Dosen> dosenObs = dosenList != null
                ? FXCollections.observableArrayList(dosenList)
                : FXCollections.observableArrayList();
        comboDosen.setItems(dosenObs);
        filterDosen.setItems(dosenObs);
        // Tampilkan nama dosen di ComboBox
        javafx.util.StringConverter<Dosen> dosenConverter = new javafx.util.StringConverter<Dosen>() {
            @Override
            public String toString(Dosen d) {
                return d == null ? "Semua Dosen" : d.getNidn() + " - " + d.getNama();
            }

            @Override
            public Dosen fromString(String s) {
                return null;
            }
        };
        comboDosen.setConverter(dosenConverter);
        filterDosen.setConverter(dosenConverter);

        // Kelas
        List<Kelas> kelasList = kelasDAO.getAll();
        ObservableList<Kelas> kelasObs = kelasList != null
                ? FXCollections.observableArrayList(kelasList)
                : FXCollections.observableArrayList();
        comboKelas.setItems(kelasObs);
        filterKelas.setItems(kelasObs);
        // Tampilkan nama kelas di ComboBox
        javafx.util.StringConverter<Kelas> kelasConverter = new javafx.util.StringConverter<Kelas>() {
            @Override
            public String toString(Kelas k) {
                return k == null ? "Semua Kelas" : k.getIdKelas() + " - " + k.getNamaKelas();
            }

            @Override
            public Kelas fromString(String s) {
                return null;
            }
        };
        comboKelas.setConverter(kelasConverter);
        filterKelas.setConverter(kelasConverter);
    }

    /** Menyambungkan FilteredList dengan filter hari, dosen, dan kelas */
    private void setupFilter() {
        filteredList = new FilteredList<>(jadwalList, p -> true);
        tableJadwal.setItems(filteredList);

        // Trigger filter ulang tiap kali salah satu filter berubah
        filterHari.valueProperty().addListener((obs, o, n) -> applyFilter());
        filterDosen.valueProperty().addListener((obs, o, n) -> applyFilter());
        filterKelas.valueProperty().addListener((obs, o, n) -> applyFilter());
    }

    /** Menerapkan semua kondisi filter ke FilteredList */
    private void applyFilter() {
        String hariFilter = filterHari.getValue();
        Dosen dosenFilter = filterDosen.getValue();
        Kelas kelasFilter = filterKelas.getValue();

        filteredList.setPredicate(jadwal -> {
            boolean matchHari = (hariFilter == null || hariFilter.equals("Semua"))
                    || jadwal.getHari().equalsIgnoreCase(hariFilter);
            boolean matchDosen = (dosenFilter == null)
                    || jadwal.getNidn().equals(dosenFilter.getNidn());
            boolean matchKelas = (kelasFilter == null)
                    || jadwal.getIdKelas().equals(kelasFilter.getIdKelas());
            return matchHari && matchDosen && matchKelas;
        });
    }

    /** Listener saat baris tabel dipilih — isi form dengan data jadwal */
    private void setupTableSelectionListener() {
        tableJadwal.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        populateForm(newSel);
                    }
                });
    }

    // ===== Load Data =====

    private void loadData() {
        jadwalList.clear();
        List<Jadwal> data = jadwalDAO.getAll();
        if (data != null) {
            jadwalList.addAll(data);
        }
    }

    // ===== CRUD Handlers =====

    /** Tombol Tambah — generate ID otomatis & simpan jadwal baru */
    @FXML
    private void handleTambah(ActionEvent event) {
        if (!isFormValid())
            return;

        Jadwal jadwal = buildJadwalFromForm();
        // Generate ID unik jika belum diisi
        if (jadwal.getIdJadwal().isEmpty()) {
            jadwal.setIdJadwal("JDW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        jadwalDAO.insert(jadwal);
        AlertHelper.showInfo("Berhasil", "Data jadwal berhasil ditambahkan.");
        loadData();
        clearForm();
    }

    /** Tombol Edit — aktifkan mode edit, lalu Simpan perubahan */
    @FXML
    private void handleEdit(ActionEvent event) {
        if (!isEditMode) {
            Jadwal selected = tableJadwal.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Peringatan", "Pilih jadwal yang ingin diedit terlebih dahulu.");
                return;
            }
            setEditMode(true);
            return;
        }

        // Mode edit aktif → Simpan
        if (!isFormValid())
            return;

        Jadwal jadwal = buildJadwalFromForm();
        jadwalDAO.update(jadwal);
        AlertHelper.showInfo("Berhasil", "Data jadwal berhasil diperbarui.");
        loadData();
        clearForm();
        setEditMode(false);
    }

    /** Tombol Hapus — konfirmasi lalu hapus jadwal yang dipilih */
    @FXML
    private void handleHapus(ActionEvent event) {
        Jadwal selected = tableJadwal.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih jadwal yang ingin dihapus terlebih dahulu.");
            return;
        }

        boolean konfirmasi = AlertHelper.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus jadwal ID \"" + selected.getIdJadwal() + "\" (" +
                        selected.getHari() + ", " + selected.getJamMulai() + " - " + selected.getJamSelesai() + ")?");

        if (konfirmasi) {
            jadwalDAO.delete(selected.getIdJadwal());
            AlertHelper.showInfo("Berhasil", "Data jadwal berhasil dihapus.");
            loadData();
            clearForm();
            setEditMode(false);
        }
    }

    /** Tombol Batal — reset form dan keluar dari mode edit */
    @FXML
    private void handleBatal(ActionEvent event) {
        clearForm();
        setEditMode(false);
        tableJadwal.getSelectionModel().clearSelection();
    }

    /** Tombol Reset Filter — kembalikan semua filter ke nilai awal */
    @FXML
    private void handleResetFilter(ActionEvent event) {
        filterHari.setValue("Semua");
        filterDosen.setValue(null);
        filterKelas.setValue(null);
    }

    // ===== Helper Methods =====

    /**
     * Mengisi form dari data jadwal yang dipilih di tabel.
     * ComboBox dipilih berdasarkan kecocokan ID/kode.
     */
    private void populateForm(Jadwal jadwal) {
        fieldIdJadwal.setText(jadwal.getIdJadwal());
        comboHari.setValue(jadwal.getHari());
        fieldJamMulai.setText(jadwal.getJamMulai());
        fieldJamSelesai.setText(jadwal.getJamSelesai());
        fieldRuangan.setText(jadwal.getRuangan());
        fieldTahunAkademik.setText(jadwal.getTahunAkademik());
        comboSemester.setValue(jadwal.getSemester());

        // Pilih MataKuliah yang sesuai di ComboBox
        comboMataKuliah.getItems().stream()
                .filter(mk -> mk.getKodeMk().equals(jadwal.getKodeMk()))
                .findFirst()
                .ifPresent(comboMataKuliah::setValue);

        // Pilih Dosen yang sesuai di ComboBox
        comboDosen.getItems().stream()
                .filter(d -> d.getNidn().equals(jadwal.getNidn()))
                .findFirst()
                .ifPresent(comboDosen::setValue);

        // Pilih Kelas yang sesuai di ComboBox
        comboKelas.getItems().stream()
                .filter(k -> k.getIdKelas().equals(jadwal.getIdKelas()))
                .findFirst()
                .ifPresent(comboKelas::setValue);
    }

    /**
     * Membangun objek Jadwal dari isian form.
     * 
     * @return Jadwal baru berdasarkan input form
     */
    private Jadwal buildJadwalFromForm() {
        String kodeMk = comboMataKuliah.getValue() != null ? comboMataKuliah.getValue().getKodeMk() : "";
        String nidn = comboDosen.getValue() != null ? comboDosen.getValue().getNidn() : "";
        String idKelas = comboKelas.getValue() != null ? comboKelas.getValue().getIdKelas() : "";

        return new Jadwal(
                fieldIdJadwal.getText().trim(),
                kodeMk,
                nidn,
                idKelas,
                comboHari.getValue(),
                fieldJamMulai.getText().trim(),
                fieldJamSelesai.getText().trim(),
                fieldRuangan.getText().trim(),
                fieldTahunAkademik.getText().trim(),
                comboSemester.getValue());
    }

    /**
     * Validasi field wajib sebelum menyimpan.
     * 
     * @return true jika semua input valid
     */
    private boolean isFormValid() {
        if (comboMataKuliah.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Mata kuliah harus dipilih.");
            return false;
        }
        if (comboDosen.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Dosen harus dipilih.");
            return false;
        }
        if (comboKelas.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Kelas harus dipilih.");
            return false;
        }
        if (comboHari.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Hari harus dipilih.");
            return false;
        }
        if (fieldJamMulai.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Jam mulai tidak boleh kosong. (Format: HH:mm)");
            return false;
        }
        if (fieldJamSelesai.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Jam selesai tidak boleh kosong. (Format: HH:mm)");
            return false;
        }
        if (fieldRuangan.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Ruangan tidak boleh kosong.");
            return false;
        }
        if (fieldTahunAkademik.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Tahun akademik tidak boleh kosong. (Contoh: 2024/2025)");
            return false;
        }
        if (comboSemester.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Semester harus dipilih.");
            return false;
        }
        return true;
    }

    /** Mengosongkan semua field form */
    private void clearForm() {
        fieldIdJadwal.clear();
        comboMataKuliah.setValue(null);
        comboDosen.setValue(null);
        comboKelas.setValue(null);
        comboHari.setValue(null);
        fieldJamMulai.clear();
        fieldJamSelesai.clear();
        fieldRuangan.clear();
        fieldTahunAkademik.clear();
        comboSemester.setValue(null);
    }

    /**
     * Mengatur mode tampilan form (tambah vs edit).
     * 
     * @param editMode true = mode edit aktif
     */
    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;

        // ID Jadwal tidak bisa diubah saat edit (primary key)
        fieldIdJadwal.setDisable(editMode);

        if (editMode) {
            btnEdit.setText("Simpan");
            btnTambah.setDisable(true);
        } else {
            btnEdit.setText("Edit");
            btnTambah.setDisable(false);
        }
    }

    public void selectTab(int index) {
        if (tabPane != null && index >= 0 && index < tabPane.getTabs().size()) {
            tabPane.getSelectionModel().select(index);
        }
    }
}
