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
 * Controller untuk use case: Input Mata Kuliah.
 * Admin dapat menambah, mengedit, menghapus data mata kuliah,
 * dengan filter berdasarkan program studi.
 */
public class DataMataKuliahController implements Initializable {

    // ===== FXML Bindings — TableView =====

    @FXML
    private TableView<MataKuliah> tableMataKuliah;

    @FXML
    private TableColumn<MataKuliah, String> colKodeMk;

    @FXML
    private TableColumn<MataKuliah, String> colNamaMk;

    @FXML
    private TableColumn<MataKuliah, Integer> colSks;

    @FXML
    private TableColumn<MataKuliah, Integer> colSemester;

    @FXML
    private TableColumn<MataKuliah, String> colIdProdi;

    // ===== FXML Bindings — Form Input =====

    @FXML
    private TextField fieldKodeMk;

    @FXML
    private TextField fieldNamaMk;

    @FXML
    private Spinner<Integer> spinnerSks;

    @FXML
    private Spinner<Integer> spinnerSemester;

    @FXML
    private ComboBox<Prodi> comboProdi;

    // ===== FXML Bindings — Kontrol =====

    @FXML
    private TextField fieldSearch;

    @FXML
    private ComboBox<Prodi> filterProdi;

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

    private final MataKuliahDAO mataKuliahDAO = new MataKuliahDAO();
    private final ProdiDAO prodiDAO           = new ProdiDAO();

    private final ObservableList<MataKuliah> mkList = FXCollections.observableArrayList();
    private FilteredList<MataKuliah> filteredList;

    private boolean isEditMode = false;

    // ===== Lifecycle =====

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupComboBoxes();
        setupSpinners();
        setupSearchAndFilter();
        loadData();
        setupTableSelectionListener();
        setEditMode(false);
    }

    // ===== Setup =====

    /** Mapping kolom TableView ke properti model MataKuliah */
    private void setupTable() {
        colKodeMk.setCellValueFactory(new PropertyValueFactory<>("kodeMk"));
        colNamaMk.setCellValueFactory(new PropertyValueFactory<>("namaMk"));
        colSks.setCellValueFactory(new PropertyValueFactory<>("sks"));
        colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        colIdProdi.setCellValueFactory(new PropertyValueFactory<>("idProdi"));
    }

    /** Mengisi ComboBox prodi dari database */
    private void setupComboBoxes() {
        List<Prodi> prodiList = prodiDAO.getAll();
        ObservableList<Prodi> prodiObs = prodiList != null
                ? FXCollections.observableArrayList(prodiList)
                : FXCollections.observableArrayList();

        javafx.util.StringConverter<Prodi> converter = new javafx.util.StringConverter<Prodi>() {
            @Override public String toString(Prodi p) {
                return p == null ? "Semua Prodi" : p.getIdProdi() + " - " + p.getNamaProdi();
            }
            @Override public Prodi fromString(String s) { return null; }
        };

        comboProdi.setItems(prodiObs);
        comboProdi.setConverter(converter);

        filterProdi.setItems(prodiObs);
        filterProdi.setConverter(converter);
    }

    /** Inisialisasi Spinner SKS (1–6) dan Semester (1–8) */
    private void setupSpinners() {
        spinnerSks.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 2)
        );
        spinnerSemester.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 8, 1)
        );
    }

    /** Search real-time (kode/nama MK) + filter prodi */
    private void setupSearchAndFilter() {
        filteredList = new FilteredList<>(mkList, p -> true);
        tableMataKuliah.setItems(filteredList);

        fieldSearch.textProperty().addListener((obs, o, n) -> applyFilter());
        filterProdi.valueProperty().addListener((obs, o, n) -> applyFilter());
    }

    private void applyFilter() {
        String keyword    = fieldSearch.getText() == null ? "" : fieldSearch.getText().toLowerCase().trim();
        Prodi prodiFilter = filterProdi.getValue();

        filteredList.setPredicate(mk -> {
            boolean matchSearch = keyword.isEmpty()
                    || mk.getKodeMk().toLowerCase().contains(keyword)
                    || mk.getNamaMk().toLowerCase().contains(keyword);
            boolean matchProdi = prodiFilter == null
                    || mk.getIdProdi().equals(prodiFilter.getIdProdi());
            return matchSearch && matchProdi;
        });
    }

    /** Listener saat baris tabel dipilih */
    private void setupTableSelectionListener() {
        tableMataKuliah.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) populateForm(newSel);
                }
        );
    }

    // ===== Load Data =====

    private void loadData() {
        mkList.clear();
        List<MataKuliah> data = mataKuliahDAO.getAll();
        if (data != null) mkList.addAll(data);
    }

    // ===== CRUD Handlers =====

    @FXML
    private void handleTambah(ActionEvent event) {
        if (!isFormValid()) return;
        mataKuliahDAO.insert(buildFromForm());
        AlertHelper.showInfo("Berhasil", "Data mata kuliah berhasil ditambahkan.");
        loadData();
        clearForm();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        if (!isEditMode) {
            if (tableMataKuliah.getSelectionModel().getSelectedItem() == null) {
                AlertHelper.showWarning("Peringatan", "Pilih mata kuliah yang ingin diedit terlebih dahulu.");
                return;
            }
            setEditMode(true);
            return;
        }
        if (!isFormValid()) return;
        mataKuliahDAO.update(buildFromForm());
        AlertHelper.showInfo("Berhasil", "Data mata kuliah berhasil diperbarui.");
        loadData();
        clearForm();
        setEditMode(false);
    }

    @FXML
    private void handleHapus(ActionEvent event) {
        MataKuliah selected = tableMataKuliah.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih mata kuliah yang ingin dihapus terlebih dahulu.");
            return;
        }
        boolean konfirmasi = AlertHelper.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus mata kuliah \"" + selected.getNamaMk()
                        + "\" (" + selected.getKodeMk() + ")?"
        );
        if (konfirmasi) {
            mataKuliahDAO.delete(selected.getKodeMk());
            AlertHelper.showInfo("Berhasil", "Data mata kuliah berhasil dihapus.");
            loadData();
            clearForm();
            setEditMode(false);
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        clearForm();
        setEditMode(false);
        tableMataKuliah.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleResetFilter(ActionEvent event) {
        fieldSearch.clear();
        filterProdi.setValue(null);
    }

    // ===== Helper Methods =====

    private void populateForm(MataKuliah mk) {
        fieldKodeMk.setText(mk.getKodeMk());
        fieldNamaMk.setText(mk.getNamaMk());
        spinnerSks.getValueFactory().setValue(mk.getSks());
        spinnerSemester.getValueFactory().setValue(mk.getSemester());
        comboProdi.getItems().stream()
                .filter(p -> p.getIdProdi().equals(mk.getIdProdi()))
                .findFirst()
                .ifPresent(comboProdi::setValue);
    }

    private MataKuliah buildFromForm() {
        String idProdi = comboProdi.getValue() != null ? comboProdi.getValue().getIdProdi() : "";
        return new MataKuliah(
                fieldKodeMk.getText().trim(),
                fieldNamaMk.getText().trim(),
                spinnerSks.getValue(),
                spinnerSemester.getValue(),
                idProdi
        );
    }

    private boolean isFormValid() {
        if (fieldKodeMk.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Kode mata kuliah tidak boleh kosong.");
            return false;
        }
        if (fieldNamaMk.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama mata kuliah tidak boleh kosong.");
            return false;
        }
        if (comboProdi.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Program studi harus dipilih.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        fieldKodeMk.clear();
        fieldNamaMk.clear();
        spinnerSks.getValueFactory().setValue(2);
        spinnerSemester.getValueFactory().setValue(1);
        comboProdi.setValue(null);
    }

    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        fieldKodeMk.setDisable(editMode); // kode MK = primary key
        if (editMode) {
            btnEdit.setText("Simpan");
            btnTambah.setDisable(true);
        } else {
            btnEdit.setText("Edit");
            btnTambah.setDisable(false);
        }
    }

    public void selectTab(int index) {
        // Placeholder for future tab integration
    }
}
