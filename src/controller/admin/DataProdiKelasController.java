package controller.admin;

import dao.KelasDAO;
import dao.ProdiDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Kelas;
import model.Prodi;
import util.AlertHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk use case: Input Data Prodi & Input Kelas.
 * Admin mengelola program studi; kelas merupakan «include» dari prodi.
 * Halaman ini dibagi dua panel: panel Prodi (kiri) dan panel Kelas (kanan).
 * Memilih prodi di tabel akan menampilkan kelas-kelas milik prodi tersebut.
 */
public class DataProdiKelasController implements Initializable {

    // ===== FXML Bindings — Tab Pane =====

    @FXML
    private TabPane tabPane;

    // ===== FXML Bindings — Tabel Prodi =====

    @FXML
    private TableView<Prodi> tableProdi;

    @FXML
    private TableColumn<Prodi, String> colIdProdi;

    @FXML
    private TableColumn<Prodi, String> colNamaProdi;

    @FXML
    private TableColumn<Prodi, String> colFakultas;

    // ===== FXML Bindings — Form Prodi =====

    @FXML
    private TextField fieldIdProdi;

    @FXML
    private TextField fieldNamaProdi;

    @FXML
    private TextField fieldFakultas;

    @FXML
    private Button btnTambahProdi;

    @FXML
    private Button btnEditProdi;

    @FXML
    private Button btnHapusProdi;

    @FXML
    private Button btnBatalProdi;

    // ===== FXML Bindings — Tabel Kelas =====

    @FXML
    private TableView<Kelas> tableKelas;

    @FXML
    private TableColumn<Kelas, String> colIdKelas;

    @FXML
    private TableColumn<Kelas, String> colNamaKelas;

    @FXML
    private TableColumn<Kelas, String> colTahunAkademik;

    // ===== FXML Bindings — Form Kelas =====

    @FXML
    private TextField fieldIdKelas;

    @FXML
    private TextField fieldNamaKelas;

    @FXML
    private TextField fieldTahunAkademik;

    /** Label yang menunjukkan prodi aktif saat menambah/edit kelas */
    @FXML
    private Label labelProdiKelas;

    @FXML
    private Button btnTambahKelas;

    @FXML
    private Button btnEditKelas;

    @FXML
    private Button btnHapusKelas;

    @FXML
    private Button btnBatalKelas;

    // ===== State =====

    private final ProdiDAO prodiDAO = new ProdiDAO();
    private final KelasDAO kelasDAO = new KelasDAO();

    private final ObservableList<Prodi> prodiList = FXCollections.observableArrayList();
    private final ObservableList<Kelas> kelasList = FXCollections.observableArrayList();
    private FilteredList<Kelas> filteredKelasList;

    /** Prodi yang sedang aktif/dipilih di tabel */
    private Prodi selectedProdi = null;

    private boolean isEditModeProdi = false;
    private boolean isEditModeKelas = false;

    // ===== Inisialisasi =====

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableProdi();
        setupTableKelas();
        setupFilterKelas();
        loadProdi();
        setupProdiSelectionListener();
        setEditModeProdi(false);
        setEditModeKelas(false);
        // Nonaktifkan panel kelas sampai prodi dipilih
        setPanelKelasEnabled(false);
    }

    // ===== Mapping kolom tabel =====

    private void setupTableProdi() {
        colIdProdi.setCellValueFactory(new PropertyValueFactory<>("idProdi"));
        colNamaProdi.setCellValueFactory(new PropertyValueFactory<>("namaProdi"));
        colFakultas.setCellValueFactory(new PropertyValueFactory<>("fakultas"));
        tableProdi.setItems(prodiList);
    }

    private void setupTableKelas() {
        colIdKelas.setCellValueFactory(new PropertyValueFactory<>("idKelas"));
        colNamaKelas.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
        colTahunAkademik.setCellValueFactory(new PropertyValueFactory<>("tahunAkademik"));
    }

    /** Kelas di-filter otomatis berdasarkan prodi yang dipilih */
    private void setupFilterKelas() {
        filteredKelasList = new FilteredList<>(kelasList, k -> true);
        tableKelas.setItems(filteredKelasList);
    }

    /**
     * Saat prodi dipilih di tabel:
     * - Isi form prodi
     * - Load dan tampilkan kelas milik prodi tersebut
     * - Aktifkan panel kelas
     */
    private void setupProdiSelectionListener() {
        tableProdi.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        selectedProdi = newSel;
                        populateFormProdi(newSel);
                        loadKelasByProdi(newSel.getIdProdi());
                        setPanelKelasEnabled(true);
                        clearFormKelas();
                        setEditModeKelas(false);
                        if (labelProdiKelas != null) {
                            labelProdiKelas.setText("Kelas untuk Prodi: " + newSel.getNamaProdi());
                        }
                    }
                });

        // Saat kelas dipilih → isi form kelas
        tableKelas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null)
                        populateFormKelas(newSel);
                });
    }

    // ===== Load Data =====

    private void loadProdi() {
        prodiList.clear();
        List<Prodi> data = prodiDAO.getAll();
        if (data != null)
            prodiList.addAll(data);
    }

    private void loadKelasByProdi(String idProdi) {
        kelasList.clear();
        List<Kelas> data = kelasDAO.getByProdi(idProdi);
        if (data != null)
            kelasList.addAll(data);
        // Reset predicate agar semua kelas prodi ini ditampilkan
        filteredKelasList.setPredicate(k -> k.getIdProdi().equals(idProdi));
    }

    // ===== CRUD Prodi =====

    @FXML
    private void handleTambahProdi(ActionEvent event) {
        if (!isFormProdiValid())
            return;
        prodiDAO.insert(buildProdiFromForm());
        AlertHelper.showInfo("Berhasil", "Data prodi berhasil ditambahkan.");
        loadProdi();
        clearFormProdi();
    }

    @FXML
    private void handleEditProdi(ActionEvent event) {
        if (!isEditModeProdi) {
            if (tableProdi.getSelectionModel().getSelectedItem() == null) {
                AlertHelper.showWarning("Peringatan", "Pilih prodi yang ingin diedit terlebih dahulu.");
                return;
            }
            setEditModeProdi(true);
            return;
        }
        if (!isFormProdiValid())
            return;
        prodiDAO.update(buildProdiFromForm());
        AlertHelper.showInfo("Berhasil", "Data prodi berhasil diperbarui.");
        loadProdi();
        clearFormProdi();
        setEditModeProdi(false);
    }

    @FXML
    private void handleHapusProdi(ActionEvent event) {
        Prodi selected = tableProdi.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih prodi yang ingin dihapus terlebih dahulu.");
            return;
        }
        boolean konfirmasi = AlertHelper.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus prodi \"" + selected.getNamaProdi() + "\"?\n"
                        + "Semua kelas dalam prodi ini juga akan terhapus.");
        if (konfirmasi) {
            prodiDAO.delete(selected.getIdProdi());
            AlertHelper.showInfo("Berhasil", "Data prodi berhasil dihapus.");
            loadProdi();
            clearFormProdi();
            clearFormKelas();
            kelasList.clear();
            setPanelKelasEnabled(false);
            selectedProdi = null;
            setEditModeProdi(false);
        }
    }

    @FXML
    private void handleBatalProdi(ActionEvent event) {
        clearFormProdi();
        setEditModeProdi(false);
        tableProdi.getSelectionModel().clearSelection();
    }

    // ===== CRUD Kelas =====

    @FXML
    private void handleTambahKelas(ActionEvent event) {
        if (selectedProdi == null) {
            AlertHelper.showWarning("Peringatan", "Pilih prodi terlebih dahulu sebelum menambah kelas.");
            return;
        }
        if (!isFormKelasValid())
            return;
        Kelas kelas = buildKelasFromForm(selectedProdi.getIdProdi());
        kelasDAO.insert(kelas);
        AlertHelper.showInfo("Berhasil", "Data kelas berhasil ditambahkan.");
        loadKelasByProdi(selectedProdi.getIdProdi());
        clearFormKelas();
    }

    @FXML
    private void handleEditKelas(ActionEvent event) {
        if (!isEditModeKelas) {
            if (tableKelas.getSelectionModel().getSelectedItem() == null) {
                AlertHelper.showWarning("Peringatan", "Pilih kelas yang ingin diedit terlebih dahulu.");
                return;
            }
            setEditModeKelas(true);
            return;
        }
        if (!isFormKelasValid())
            return;
        String idProdi = selectedProdi != null ? selectedProdi.getIdProdi() : "";
        kelasDAO.update(buildKelasFromForm(idProdi));
        AlertHelper.showInfo("Berhasil", "Data kelas berhasil diperbarui.");
        loadKelasByProdi(idProdi);
        clearFormKelas();
        setEditModeKelas(false);
    }

    @FXML
    private void handleHapusKelas(ActionEvent event) {
        Kelas selected = tableKelas.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih kelas yang ingin dihapus terlebih dahulu.");
            return;
        }
        boolean konfirmasi = AlertHelper.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus kelas \"" + selected.getNamaKelas() + "\" ("
                        + selected.getIdKelas() + ")?");
        if (konfirmasi) {
            kelasDAO.delete(selected.getIdKelas());
            AlertHelper.showInfo("Berhasil", "Data kelas berhasil dihapus.");
            if (selectedProdi != null)
                loadKelasByProdi(selectedProdi.getIdProdi());
            clearFormKelas();
            setEditModeKelas(false);
        }
    }

    @FXML
    private void handleBatalKelas(ActionEvent event) {
        clearFormKelas();
        setEditModeKelas(false);
        tableKelas.getSelectionModel().clearSelection();
    }

    // ===== Helper — Prodi =====

    private void populateFormProdi(Prodi p) {
        fieldIdProdi.setText(p.getIdProdi());
        fieldNamaProdi.setText(p.getNamaProdi());
        fieldFakultas.setText(p.getFakultas());
    }

    private Prodi buildProdiFromForm() {
        return new Prodi(
                fieldIdProdi.getText().trim(),
                fieldNamaProdi.getText().trim(),
                fieldFakultas.getText().trim());
    }

    private boolean isFormProdiValid() {
        if (fieldIdProdi.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "ID Prodi tidak boleh kosong.");
            return false;
        }
        if (fieldNamaProdi.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama prodi tidak boleh kosong.");
            return false;
        }
        return true;
    }

    private void clearFormProdi() {
        fieldIdProdi.clear();
        fieldNamaProdi.clear();
        fieldFakultas.clear();
    }

    private void setEditModeProdi(boolean editMode) {
        this.isEditModeProdi = editMode;
        fieldIdProdi.setDisable(editMode); // ID prodi = primary key
        if (editMode) {
            btnEditProdi.setText("Simpan");
            btnTambahProdi.setDisable(true);
        } else {
            btnEditProdi.setText("Edit");
            btnTambahProdi.setDisable(false);
        }
    }

    // ===== Helper — Kelas =====

    private void populateFormKelas(Kelas k) {
        fieldIdKelas.setText(k.getIdKelas());
        fieldNamaKelas.setText(k.getNamaKelas());
        fieldTahunAkademik.setText(k.getTahunAkademik());
    }

    private Kelas buildKelasFromForm(String idProdi) {
        return new Kelas(
                fieldIdKelas.getText().trim(),
                fieldNamaKelas.getText().trim(),
                idProdi,
                fieldTahunAkademik.getText().trim());
    }

    private boolean isFormKelasValid() {
        if (fieldIdKelas.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "ID Kelas tidak boleh kosong.");
            return false;
        }
        if (fieldNamaKelas.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama kelas tidak boleh kosong.");
            return false;
        }
        if (fieldTahunAkademik.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Tahun akademik tidak boleh kosong. (Contoh: 2024/2025)");
            return false;
        }
        return true;
    }

    private void clearFormKelas() {
        fieldIdKelas.clear();
        fieldNamaKelas.clear();
        fieldTahunAkademik.clear();
    }

    private void setEditModeKelas(boolean editMode) {
        this.isEditModeKelas = editMode;
        fieldIdKelas.setDisable(editMode); // ID kelas = primary key
        if (editMode) {
            btnEditKelas.setText("Simpan");
            btnTambahKelas.setDisable(true);
        } else {
            btnEditKelas.setText("Edit");
            btnTambahKelas.setDisable(false);
        }
    }

    /**
     * Mengaktifkan atau menonaktifkan seluruh panel kelas.
     * Panel kelas hanya bisa digunakan setelah prodi dipilih.
     */
    private void setPanelKelasEnabled(boolean enabled) {
        tableKelas.setDisable(!enabled);
        fieldIdKelas.setDisable(!enabled);
        fieldNamaKelas.setDisable(!enabled);
        fieldTahunAkademik.setDisable(!enabled);
        btnTambahKelas.setDisable(!enabled);
        btnEditKelas.setDisable(!enabled);
        btnHapusKelas.setDisable(!enabled);
        btnBatalKelas.setDisable(!enabled);
    }

    public void selectTab(int index) {
        if (tabPane != null && index >= 0 && index < tabPane.getTabs().size()) {
            tabPane.getSelectionModel().select(index);
        }
    }
}
