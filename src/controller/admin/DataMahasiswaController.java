package controller.admin;

import dao.KelasDAO;
import dao.MahasiswaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Kelas;
import model.Mahasiswa;
import util.AlertHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk use case: Input Data Mahasiswa & Lihat Data Mahasiswa.
 * Admin dapat menambah, mengedit, menghapus, dan melihat data mahasiswa.
 */
public class DataMahasiswaController implements Initializable {

    // ===== FXML Bindings — TableView =====

    @FXML
    private TableView<Mahasiswa> tableMahasiswa;

    @FXML
    private TableColumn<Mahasiswa, String> colNim;

    @FXML
    private TableColumn<Mahasiswa, String> colNama;

    @FXML
    private TableColumn<Mahasiswa, String> colJenisKelamin;

    @FXML
    private TableColumn<Mahasiswa, String> colAlamat;

    @FXML
    private TableColumn<Mahasiswa, String> colNoTelp;

    @FXML
    private TableColumn<Mahasiswa, String> colEmail;

    @FXML
    private TableColumn<Mahasiswa, String> colIdKelas;

    // ===== FXML Bindings — Form Input =====

    @FXML
    private TextField fieldNim;

    @FXML
    private TextField fieldNama;

    @FXML
    private ComboBox<String> comboJenisKelamin;

    @FXML
    private TextField fieldAlamat;

    @FXML
    private TextField fieldNoTelp;

    @FXML
    private TextField fieldEmail;

    @FXML
    private ComboBox<Kelas> comboKelas;

    @FXML
    private PasswordField fieldPassword;

    // ===== FXML Bindings — Kontrol =====

    @FXML
    private TextField fieldSearch;

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

    private final MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();
    private final KelasDAO kelasDAO         = new KelasDAO();

    private final ObservableList<Mahasiswa> mahasiswaList = FXCollections.observableArrayList();
    private FilteredList<Mahasiswa> filteredList;

    private boolean isEditMode = false;

    // ===== Lifecycle =====

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupComboBoxes();
        setupSearchAndFilter();
        loadData();
        setupTableSelectionListener();
        setEditMode(false);
    }

    // ===== Setup =====

    /** Mapping kolom TableView ke properti model Mahasiswa */
    private void setupTable() {
        colNim.setCellValueFactory(new PropertyValueFactory<>("nim"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colJenisKelamin.setCellValueFactory(new PropertyValueFactory<>("jenisKelamin"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        colNoTelp.setCellValueFactory(new PropertyValueFactory<>("noTelp"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colIdKelas.setCellValueFactory(new PropertyValueFactory<>("idKelas"));
    }

    /** Mengisi ComboBox jenis kelamin dan kelas */
    private void setupComboBoxes() {
        comboJenisKelamin.setItems(FXCollections.observableArrayList("Laki-laki", "Perempuan"));

        List<Kelas> kelasList = kelasDAO.getAll();
        ObservableList<Kelas> kelasObs = kelasList != null
                ? FXCollections.observableArrayList(kelasList)
                : FXCollections.observableArrayList();

        javafx.util.StringConverter<Kelas> converter = new javafx.util.StringConverter<Kelas>() {
            @Override public String toString(Kelas k) {
                return k == null ? "Semua Kelas" : k.getIdKelas() + " - " + k.getNamaKelas();
            }
            @Override public Kelas fromString(String s) { return null; }
        };

        comboKelas.setItems(kelasObs);
        comboKelas.setConverter(converter);

        filterKelas.setItems(kelasObs);
        filterKelas.setConverter(converter);
    }

    /** Search real-time (NIM/Nama/Email) + filter kelas */
    private void setupSearchAndFilter() {
        filteredList = new FilteredList<>(mahasiswaList, p -> true);
        tableJadwal();

        fieldSearch.textProperty().addListener((obs, o, n) -> applyFilter());
        filterKelas.valueProperty().addListener((obs, o, n) -> applyFilter());
    }

    private void tableJadwal() {
        tableMahasiswa.setItems(filteredList);
    }

    private void applyFilter() {
        String keyword      = fieldSearch.getText() == null ? "" : fieldSearch.getText().toLowerCase().trim();
        Kelas kelasFilter   = filterKelas.getValue();

        filteredList.setPredicate(m -> {
            boolean matchSearch = keyword.isEmpty()
                    || m.getNim().toLowerCase().contains(keyword)
                    || m.getNama().toLowerCase().contains(keyword)
                    || m.getEmail().toLowerCase().contains(keyword);
            boolean matchKelas = kelasFilter == null
                    || m.getIdKelas().equals(kelasFilter.getIdKelas());
            return matchSearch && matchKelas;
        });
    }

    /** Listener saat baris tabel dipilih */
    private void setupTableSelectionListener() {
        tableMahasiswa.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) populateForm(newSel);
                }
        );
    }

    // ===== Load Data =====

    private void loadData() {
        mahasiswaList.clear();
        List<Mahasiswa> data = mahasiswaDAO.getAll();
        if (data != null) mahasiswaList.addAll(data);
    }

    // ===== CRUD Handlers =====

    @FXML
    private void handleTambah(ActionEvent event) {
        if (!isFormValid(true)) return;
        mahasiswaDAO.insert(buildFromForm());
        AlertHelper.showInfo("Berhasil", "Data mahasiswa berhasil ditambahkan.");
        loadData();
        clearForm();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        if (!isEditMode) {
            if (tableMahasiswa.getSelectionModel().getSelectedItem() == null) {
                AlertHelper.showWarning("Peringatan", "Pilih mahasiswa yang ingin diedit terlebih dahulu.");
                return;
            }
            setEditMode(true);
            return;
        }
        if (!isFormValid(false)) return;
        mahasiswaDAO.update(buildFromForm());
        AlertHelper.showInfo("Berhasil", "Data mahasiswa berhasil diperbarui.");
        loadData();
        clearForm();
        setEditMode(false);
    }

    @FXML
    private void handleHapus(ActionEvent event) {
        Mahasiswa selected = tableMahasiswa.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih mahasiswa yang ingin dihapus terlebih dahulu.");
            return;
        }
        boolean konfirmasi = AlertHelper.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus mahasiswa \"" + selected.getNama() + "\" (NIM: " + selected.getNim() + ")?"
        );
        if (konfirmasi) {
            mahasiswaDAO.delete(selected.getNim());
            AlertHelper.showInfo("Berhasil", "Data mahasiswa berhasil dihapus.");
            loadData();
            clearForm();
            setEditMode(false);
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        clearForm();
        setEditMode(false);
        tableMahasiswa.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleResetFilter(ActionEvent event) {
        fieldSearch.clear();
        filterKelas.setValue(null);
    }

    // ===== Helper Methods =====

    private void populateForm(Mahasiswa m) {
        fieldNim.setText(m.getNim());
        fieldNama.setText(m.getNama());
        comboJenisKelamin.setValue(m.getJenisKelamin());
        fieldAlamat.setText(m.getAlamat());
        fieldNoTelp.setText(m.getNoTelp());
        fieldEmail.setText(m.getEmail());
        fieldPassword.clear();
        comboKelas.getItems().stream()
                .filter(k -> k.getIdKelas().equals(m.getIdKelas()))
                .findFirst()
                .ifPresent(comboKelas::setValue);
    }

    private Mahasiswa buildFromForm() {
        String idKelas = comboKelas.getValue() != null ? comboKelas.getValue().getIdKelas() : "";
        return new Mahasiswa(
                fieldNim.getText().trim(),
                fieldNama.getText().trim(),
                comboJenisKelamin.getValue(),
                fieldAlamat.getText().trim(),
                fieldNoTelp.getText().trim(),
                fieldEmail.getText().trim(),
                idKelas,
                fieldPassword.getText().trim()
        );
    }

    private boolean isFormValid(boolean cekPassword) {
        if (fieldNim.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "NIM tidak boleh kosong.");
            return false;
        }
        if (fieldNama.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama tidak boleh kosong.");
            return false;
        }
        if (comboJenisKelamin.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Jenis kelamin harus dipilih.");
            return false;
        }
        if (comboKelas.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Kelas harus dipilih.");
            return false;
        }
        if (fieldEmail.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Email tidak boleh kosong.");
            return false;
        }
        if (cekPassword && fieldPassword.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Password tidak boleh kosong.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        fieldNim.clear();
        fieldNama.clear();
        comboJenisKelamin.setValue(null);
        fieldAlamat.clear();
        fieldNoTelp.clear();
        fieldEmail.clear();
        comboKelas.setValue(null);
        fieldPassword.clear();
    }

    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        fieldNim.setDisable(editMode); // NIM = primary key, tidak bisa diubah
        if (editMode) {
            btnEdit.setText("Simpan");
            btnTambah.setDisable(true);
        } else {
            btnEdit.setText("Edit");
            btnTambah.setDisable(false);
        }
    }
}
