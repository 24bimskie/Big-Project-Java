package controller.admin;

import dao.MataKuliahDAO;
import dao.ProdiDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.MataKuliah;
import model.Prodi;
import util.AlertHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk use case: Data Mata Kuliah.
 * Admin dapat menambah, mengedit, menghapus, dan mencari data mata kuliah.
 * Setiap mata kuliah memiliki: kode, nama, SKS, semester, dan program studi.
 */
public class DataMataKuliahController implements Initializable {

    // FXML Bindings — Tab Pane

    @FXML
    private TabPane tabPane;

    // FXML Bindings — TableView

    @FXML
    private TableView<MataKuliah> tblMataKuliah;

    @FXML
    private TableColumn<MataKuliah, String> colKodeMatkul;

    @FXML
    private TableColumn<MataKuliah, String> colNamaMatkul;

    @FXML
    private TableColumn<MataKuliah, Integer> colSks;

    @FXML
    private TableColumn<MataKuliah, Integer> colSemester;

    @FXML
    private TableColumn<MataKuliah, String> colProdi;

    // FXML Bindings — Form Input

    @FXML
    private TextField txtKodeMatkul;

    @FXML
    private TextField txtNamaMatkul;

    @FXML
    private ComboBox<Integer> cmbSks;

    @FXML
    private ComboBox<Integer> cmbSemester;

    @FXML
    private TextField txtProdi;

    // FXML Bindings — Filter & Kontrol

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cmbFilterProdi;

    @FXML
    private ComboBox<Integer> cmbFilterSemester;

    @FXML
    private Button btnResetFilter;

    @FXML
    private Button btnBatal;

    @FXML
    private Button btnSimpan;

    // State
    private final MataKuliahDAO mataKuliahDAO = new MataKuliahDAO();
    private final ProdiDAO prodiDAO = new ProdiDAO();

    private final ObservableList<MataKuliah> mkList = FXCollections.observableArrayList();
    private FilteredList<MataKuliah> filteredList;

    private boolean isEditMode = false;

    // Lifecycle

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupComboBoxes();
        setupSearchAndFilter();
        loadData();
        setupTableSelectionListener();
        setEditMode(false);
    }

    // Setup

    // Mapping kolom TableView ke properti model MataKuliah
    private void setupTable() {
        colKodeMatkul.setCellValueFactory(new PropertyValueFactory<>("kodeMk"));
        colNamaMatkul.setCellValueFactory(new PropertyValueFactory<>("namaMk"));
        colSks.setCellValueFactory(new PropertyValueFactory<>("sks"));
        colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        colProdi.setCellValueFactory(new PropertyValueFactory<>("idProdi"));
        tblMataKuliah.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Mengisi ComboBox dengan data SKS, Semester, dan Prodi
    private void setupComboBoxes() {
        // SKS: hanya 2 dan 3
        ObservableList<Integer> sksList = FXCollections.observableArrayList(2, 3);
        cmbSks.setItems(sksList);

        // Semester: 1–8
        ObservableList<Integer> semesterList = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        cmbSemester.setItems(semesterList);
        cmbFilterSemester.setItems(semesterList);

        // Tidak perlu lagi load ComboBox untuk prodi, karena user akan mengetik
        // langsung.
    }

    // Filter real-time berdasarkan pencarian teks + filter prodi + filter semester
    private void setupSearchAndFilter() {
        filteredList = new FilteredList<>(mkList, p -> true);
        tblMataKuliah.setItems(filteredList);

        txtSearch.textProperty().addListener((obs, o, n) -> applyFilter());
        cmbFilterSemester.valueProperty().addListener((obs, o, n) -> applyFilter());
    }

    // Menerapkan predicate filter ke FilteredList
    private void applyFilter() {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        Integer semesterFilter = cmbFilterSemester.getValue();

        filteredList.setPredicate(mk -> {
            boolean matchSearch = keyword.isEmpty()
                    || mk.getKodeMk().toLowerCase().contains(keyword)
                    || mk.getNamaMk().toLowerCase().contains(keyword)
                    || mk.getIdProdi().toLowerCase().contains(keyword);
            boolean matchSemester = semesterFilter == null || semesterFilter == mk.getSemester();
            return matchSearch && matchSemester;
        });
    }

    // Listener saat baris tabel dipilih → isi form
    private void setupTableSelectionListener() {
        tblMataKuliah.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null)
                        populateForm(newSel);
                });
    }

    // Load Data

    private void loadData() {
        mkList.clear();
        List<MataKuliah> data = mataKuliahDAO.getAll();
        if (data != null)
            mkList.addAll(data);
    }

    // CRUD Handlers
    // Tombol Simpan: mode Tambah atau mode Edit
    @FXML
    private void handleSimpan(ActionEvent event) {
        if (!isFormValid())
            return;

        MataKuliah mk = buildFromForm();
        boolean success;
        if (isEditMode) {
            success = mataKuliahDAO.update(mk);
            if (!success) {
                String detail = mataKuliahDAO.getLastError() != null ? "\n\nDetail: " + mataKuliahDAO.getLastError()
                        : "";
                AlertHelper.showError("Gagal",
                        "Data gagal diperbarui. Periksa koneksi database atau data yang dipilih." + detail);
                return;
            }
            AlertHelper.showInfo("Berhasil", "Data mata kuliah berhasil diperbarui.");
        } else {
            if (mataKuliahDAO.getByKode(mk.getKodeMk()) != null) {
                AlertHelper.showWarning("Duplikasi", "Kode mata kuliah \"" + mk.getKodeMk() + "\" sudah ada.");
                return;
            }
            success = mataKuliahDAO.insert(mk);
            if (!success) {
                String detail = mataKuliahDAO.getLastError() != null ? "\n\nDetail: " + mataKuliahDAO.getLastError()
                        : "";
                AlertHelper.showError("Gagal", "Data gagal disimpan. Periksa koneksi database." + detail);
                return;
            }
            AlertHelper.showInfo("Berhasil", "Data mata kuliah berhasil ditambahkan.");
        }
        loadData();
        clearForm();
        setEditMode(false);
    }

    /** Tombol Batal: bersihkan form dan keluar dari edit mode */
    @FXML
    private void handleBatal(ActionEvent event) {
        clearForm();
        setEditMode(false);
        tblMataKuliah.getSelectionModel().clearSelection();
    }

    // Tombol Cari (opsional trigger manual)
    @FXML
    private void handleCari(ActionEvent event) {
        applyFilter();
    }

    // Tombol Reset Filter
    @FXML
    private void handleResetFilter(ActionEvent event) {
        txtSearch.clear();
        cmbFilterProdi.setValue(null);
        cmbFilterSemester.setValue(null);
    }

    // Klik kanan / tombol Edit dari luar (opsional)
    @FXML
    private void handleEdit(ActionEvent event) {
        MataKuliah selected = tblMataKuliah.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih mata kuliah yang ingin diedit terlebih dahulu.");
            return;
        }
        populateForm(selected);
        setEditMode(true);
    }

    /** Tombol Hapus */
    @FXML
    private void handleHapus(ActionEvent event) {
        MataKuliah selected = tblMataKuliah.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih mata kuliah yang ingin dihapus.");
            return;
        }
        boolean ok = AlertHelper.showConfirmation("Konfirmasi Hapus",
                "Yakin ingin menghapus \"" + selected.getNamaMk() + "\" (" + selected.getKodeMk() + ")?");
        if (ok) {
            boolean success = mataKuliahDAO.delete(selected.getKodeMk());
            if (!success) {
                String detail = mataKuliahDAO.getLastError() != null ? "\n\nDetail: " + mataKuliahDAO.getLastError()
                        : "";
                AlertHelper.showError("Gagal", "Data gagal dihapus. Periksa koneksi database." + detail);
                return;
            }
            AlertHelper.showInfo("Berhasil", "Data mata kuliah berhasil dihapus.");
            loadData();
            clearForm();
            setEditMode(false);
        }
    }

    // Helper Methods
    // Mengisi form input dari data yang dipilih di tabel
    private void populateForm(MataKuliah mk) {
        txtKodeMatkul.setText(mk.getKodeMk());
        txtNamaMatkul.setText(mk.getNamaMk());
        cmbSks.setValue(mk.getSks());
        cmbSemester.setValue(mk.getSemester());
        txtProdi.setText(mk.getIdProdi());
    }

    // Membangun objek MataKuliah dari input form
    private MataKuliah buildFromForm() {
        return new MataKuliah(
                txtKodeMatkul.getText().trim().toUpperCase(),
                txtNamaMatkul.getText().trim(),
                cmbSks.getValue() != null ? cmbSks.getValue() : 0,
                cmbSemester.getValue() != null ? cmbSemester.getValue() : 0,
                txtProdi.getText().trim());
    }

    // Validasi form sebelum simpan
    private boolean isFormValid() {
        if (txtKodeMatkul.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Kode mata kuliah tidak boleh kosong.");
            return false;
        }
        if (txtNamaMatkul.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama mata kuliah tidak boleh kosong.");
            return false;
        }
        if (cmbSks.getValue() == null) {
            AlertHelper.showWarning("Validasi", "SKS harus dipilih.");
            return false;
        }
        if (cmbSemester.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Semester harus dipilih.");
            return false;
        }
        if (txtProdi.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Program studi harus diisi.");
            return false;
        }
        return true;
    }

    // Mengosongkan semua field form
    private void clearForm() {
        txtKodeMatkul.clear();
        txtNamaMatkul.clear();
        cmbSks.setValue(null);
        cmbSemester.setValue(null);
        txtProdi.clear();
    }

    // Mengatur mode Edit/Tambah pada form
    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        txtKodeMatkul.setDisable(editMode); // kode MK = primary key, tidak bisa diubah
        btnSimpan.setText(editMode ? "Update" : "Simpan");
    }

    public void selectTab(int index) {
        if (tabPane != null && index >= 0 && index < tabPane.getTabs().size()) {
            tabPane.getSelectionModel().select(index);
        }
    }
}
