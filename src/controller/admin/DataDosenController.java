package controller.admin;

import dao.DosenDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Dosen;
import util.AlertHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk use case: Input Data Dosen & Lihat Data Dosen.
 * Admin dapat menambah, mengedit, menghapus, dan melihat data dosen.
 */
public class DataDosenController implements Initializable {

    // ===== FXML Bindings — TableView =====

    @FXML
    private TableView<Dosen> tableDosen;

    @FXML
    private TableColumn<Dosen, String> colNidn;

    @FXML
    private TableColumn<Dosen, String> colNama;

    @FXML
    private TableColumn<Dosen, String> colJenisKelamin;

    @FXML
    private TableColumn<Dosen, String> colAlamat;

    @FXML
    private TableColumn<Dosen, String> colNoTelp;

    @FXML
    private TableColumn<Dosen, String> colEmail;

    // ===== FXML Bindings — Form Input =====

    @FXML
    private TextField fieldNidn;

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
    private PasswordField fieldPassword;

    // ===== FXML Bindings — Kontrol =====

    @FXML
    private TextField fieldSearch;

    @FXML
    private Button btnTambah;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnHapus;

    @FXML
    private Button btnBatal;

    // ===== State =====

    private final DosenDAO dosenDAO = new DosenDAO();
    private final ObservableList<Dosen> dosenList = FXCollections.observableArrayList();
    private FilteredList<Dosen> filteredList;

    /** true = mode edit, false = mode tambah */
    private boolean isEditMode = false;

    // ===== Lifecycle =====

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupComboBox();
        setupSearch();
        loadData();
        setupTableSelectionListener();
        setEditMode(false);
    }

    // ===== Setup =====

    /** Mengatur kolom-kolom TableView dan mapping ke properti model Dosen */
    private void setupTable() {
        colNidn.setCellValueFactory(new PropertyValueFactory<>("nidn"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colJenisKelamin.setCellValueFactory(new PropertyValueFactory<>("jenisKelamin"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        colNoTelp.setCellValueFactory(new PropertyValueFactory<>("noTelp"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    /** Mengisi pilihan jenis kelamin pada ComboBox */
    private void setupComboBox() {
        comboJenisKelamin.setItems(FXCollections.observableArrayList("Laki-laki", "Perempuan"));
    }

    /** Menyambungkan search field dengan FilteredList untuk filter real-time */
    private void setupSearch() {
        filteredList = new FilteredList<>(dosenList, p -> true);
        fieldSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(dosen -> {
                if (newVal == null || newVal.trim().isEmpty()) return true;
                String keyword = newVal.toLowerCase().trim();
                return dosen.getNidn().toLowerCase().contains(keyword)
                        || dosen.getNama().toLowerCase().contains(keyword)
                        || dosen.getEmail().toLowerCase().contains(keyword);
            });
        });
        tableDosen.setItems(filteredList);
    }

    /** Listener saat baris tabel dipilih — mengisi form dengan data dosen terpilih */
    private void setupTableSelectionListener() {
        tableDosen.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        populateForm(newSel);
                    }
                }
        );
    }

    // ===== Load Data =====

    /** Mengambil semua data dosen dari database dan menampilkannya ke tabel */
    private void loadData() {
        dosenList.clear();
        List<Dosen> data = dosenDAO.getAll();
        if (data != null) {
            dosenList.addAll(data);
        }
    }

    // ===== CRUD Handlers =====

    /** Tombol Tambah — menyimpan dosen baru ke database */
    @FXML
    private void handleTambah(ActionEvent event) {
        if (!isFormValid(true)) return;

        Dosen dosen = buildDosenFromForm();
        dosenDAO.insert(dosen);
        AlertHelper.showInfo("Berhasil", "Data dosen berhasil ditambahkan.");
        loadData();
        clearForm();
    }

    /** Tombol Edit/Simpan — memperbarui data dosen yang dipilih */
    @FXML
    private void handleEdit(ActionEvent event) {
        if (!isEditMode) {
            // Pertama kali klik Edit: aktifkan mode edit
            Dosen selected = tableDosen.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Peringatan", "Pilih dosen yang ingin diedit terlebih dahulu.");
                return;
            }
            setEditMode(true);
            return;
        }

        // Klik Simpan saat mode edit aktif
        if (!isFormValid(false)) return;

        Dosen dosen = buildDosenFromForm();
        dosenDAO.update(dosen);
        AlertHelper.showInfo("Berhasil", "Data dosen berhasil diperbarui.");
        loadData();
        clearForm();
        setEditMode(false);
    }

    /** Tombol Hapus — menghapus dosen yang dipilih setelah konfirmasi */
    @FXML
    private void handleHapus(ActionEvent event) {
        Dosen selected = tableDosen.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih dosen yang ingin dihapus terlebih dahulu.");
            return;
        }

        boolean konfirmasi = AlertHelper.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus dosen \"" + selected.getNama() + "\" (NIDN: " + selected.getNidn() + ")?"
        );

        if (konfirmasi) {
            dosenDAO.delete(selected.getNidn());
            AlertHelper.showInfo("Berhasil", "Data dosen berhasil dihapus.");
            loadData();
            clearForm();
            setEditMode(false);
        }
    }

    /** Tombol Batal — membatalkan aksi dan mereset form */
    @FXML
    private void handleBatal(ActionEvent event) {
        clearForm();
        setEditMode(false);
        tableDosen.getSelectionModel().clearSelection();
    }

    // ===== Helper Methods =====

    /**
     * Mengisi field form dengan data dari dosen yang dipilih di tabel.
     * @param dosen Dosen yang dipilih
     */
    private void populateForm(Dosen dosen) {
        fieldNidn.setText(dosen.getNidn());
        fieldNama.setText(dosen.getNama());
        comboJenisKelamin.setValue(dosen.getJenisKelamin());
        fieldAlamat.setText(dosen.getAlamat());
        fieldNoTelp.setText(dosen.getNoTelp());
        fieldEmail.setText(dosen.getEmail());
        fieldPassword.clear(); // password tidak ditampilkan untuk keamanan
    }

    /**
     * Membuat objek Dosen dari isian form.
     * @return Dosen baru berdasarkan input form
     */
    private Dosen buildDosenFromForm() {
        return new Dosen(
                fieldNidn.getText().trim(),
                fieldNama.getText().trim(),
                comboJenisKelamin.getValue(),
                fieldAlamat.getText().trim(),
                fieldNoTelp.getText().trim(),
                fieldEmail.getText().trim(),
                fieldPassword.getText().trim()
        );
    }

    /**
     * Memvalidasi input form sebelum simpan/update.
     * @param cekPassword true jika password wajib diisi (mode tambah)
     * @return true jika semua input valid
     */
    private boolean isFormValid(boolean cekPassword) {
        if (fieldNidn.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "NIDN tidak boleh kosong.");
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

    /** Mengosongkan semua field form */
    private void clearForm() {
        fieldNidn.clear();
        fieldNama.clear();
        comboJenisKelamin.setValue(null);
        fieldAlamat.clear();
        fieldNoTelp.clear();
        fieldEmail.clear();
        fieldPassword.clear();
    }

    /**
     * Mengatur mode tampilan form (tambah vs edit).
     * @param editMode true = mode edit aktif
     */
    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;

        // NIDN tidak bisa diubah saat edit (primary key)
        fieldNidn.setDisable(editMode);

        if (editMode) {
            btnEdit.setText("Simpan");
            btnTambah.setDisable(true);
        } else {
            btnEdit.setText("Edit");
            btnTambah.setDisable(false);
        }
    }
}
