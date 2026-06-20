package controller.admin;

import dao.MahasiswaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Mahasiswa;
import util.AlertHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller Data Mahasiswa.
 * Kolom DB: nim, nama, gender, alamat, password, kelas, prodi
 */
public class DataMahasiswaController implements Initializable {

    @FXML private TabPane tabPane;

    // ===== TableView =====
    @FXML private TableView<Mahasiswa>         tableMahasiswa;
    @FXML private TableColumn<Mahasiswa, Integer> colNo;
    @FXML private TableColumn<Mahasiswa, String>  colNim;
    @FXML private TableColumn<Mahasiswa, String>  colNama;
    @FXML private TableColumn<Mahasiswa, String>  colGender;
    @FXML private TableColumn<Mahasiswa, String>  colAlamat;
    @FXML private TableColumn<Mahasiswa, String>  colKelas;
    @FXML private TableColumn<Mahasiswa, String>  colProdi;

    // ===== Form Input =====
    @FXML private TextField     fieldNim;
    @FXML private TextField     fieldNama;
    @FXML private ComboBox<String> comboGender;
    @FXML private TextField     fieldAlamat;
    @FXML private TextField     fieldKelas;
    @FXML private TextField     fieldProdi;
    @FXML private PasswordField fieldPassword;

    // ===== Kontrol =====
    @FXML private TextField fieldSearch;
    @FXML private Button    btnResetFilter;
    @FXML private Button    btnTambah;
    @FXML private Button    btnEdit;
    @FXML private Button    btnHapus;
    @FXML private Button    btnBatal;

    // ===== State =====
    private final MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();
    private final ObservableList<Mahasiswa> mahasiswaList = FXCollections.observableArrayList();
    private FilteredList<Mahasiswa> filteredList;
    private boolean isEditMode = false;

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

    private void setupTable() {
        colNo.setCellFactory(col -> new TableCell<Mahasiswa, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNim.setCellValueFactory(new PropertyValueFactory<>("nim"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("jenisKelamin"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        colKelas.setCellValueFactory(new PropertyValueFactory<>("kelas"));
        colProdi.setCellValueFactory(new PropertyValueFactory<>("prodi"));

        tableMahasiswa.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupComboBoxes() {
        comboGender.setItems(FXCollections.observableArrayList("L", "P"));
    }

    private void setupSearchAndFilter() {
        filteredList = new FilteredList<>(mahasiswaList, p -> true);
        tableMahasiswa.setItems(filteredList);
        fieldSearch.textProperty().addListener((obs, o, n) -> applyFilter());
    }

    private void applyFilter() {
        String keyword = fieldSearch.getText() == null ? "" : fieldSearch.getText().toLowerCase().trim();
        filteredList.setPredicate(m -> {
            if (keyword.isEmpty()) return true;
            return m.getNim().toLowerCase().contains(keyword)
                || m.getNama().toLowerCase().contains(keyword)
                || (m.getKelas()  != null && m.getKelas().toLowerCase().contains(keyword))
                || (m.getProdi()  != null && m.getProdi().toLowerCase().contains(keyword))
                || (m.getAlamat() != null && m.getAlamat().toLowerCase().contains(keyword));
        });
    }

    private void setupTableSelectionListener() {
        tableMahasiswa.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> { if (newSel != null) populateForm(newSel); }
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
        boolean ok = AlertHelper.showConfirmation("Konfirmasi Hapus",
                "Yakin ingin menghapus \"" + selected.getNama() + "\" (NIM: " + selected.getNim() + ")?");
        if (ok) {
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
    }

    // ===== Helpers =====

    private void populateForm(Mahasiswa m) {
        fieldNim.setText(m.getNim());
        fieldNama.setText(m.getNama());
        comboGender.setValue(m.getJenisKelamin());
        fieldAlamat.setText(m.getAlamat());
        fieldKelas.setText(m.getKelas() != null ? m.getKelas() : "");
        fieldProdi.setText(m.getProdi() != null ? m.getProdi() : "");
        fieldPassword.clear();
    }

    private Mahasiswa buildFromForm() {
        return new Mahasiswa(
                fieldNim.getText().trim(),
                fieldNama.getText().trim(),
                comboGender.getValue(),
                fieldAlamat.getText().trim(),
                fieldKelas.getText().trim(),
                fieldProdi.getText().trim(),
                fieldPassword.getText().trim()
        );
    }

    private boolean isFormValid(boolean cekPassword) {
        if (fieldNim.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "NIM tidak boleh kosong."); return false;
        }
        if (fieldNama.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama tidak boleh kosong."); return false;
        }
        if (comboGender.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Jenis kelamin harus dipilih."); return false;
        }
        if (cekPassword && fieldPassword.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Password tidak boleh kosong."); return false;
        }
        return true;
    }

    private void clearForm() {
        fieldNim.clear();
        fieldNama.clear();
        comboGender.setValue(null);
        fieldAlamat.clear();
        fieldKelas.clear();
        fieldProdi.clear();
        fieldPassword.clear();
    }

    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        fieldNim.setDisable(editMode);
        btnEdit.setText(editMode ? "Simpan" : "Edit");
        btnTambah.setDisable(editMode);
    }

    public void selectTab(int index) {
        if (tabPane != null) tabPane.getSelectionModel().select(index);
    }
}
