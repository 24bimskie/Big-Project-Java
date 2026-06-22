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
 * Controller Data Dosen.
 * Kolom DB: id, nip, nama, gender, alamat, password
 */
public class DataDosenController implements Initializable {

    @FXML
    private TabPane tabPane;

    // ===== TableView =====
    @FXML
    private TableView<Dosen> tableDosen;
    @FXML
    private TableColumn<Dosen, Integer> colNo;
    @FXML
    private TableColumn<Dosen, String> colNip;
    @FXML
    private TableColumn<Dosen, String> colNama;
    @FXML
    private TableColumn<Dosen, String> colGender;
    @FXML
    private TableColumn<Dosen, String> colAlamat;

    // ===== Form Input =====
    @FXML
    private TextField fieldNip;
    @FXML
    private TextField fieldNama;
    @FXML
    private ComboBox<String> comboGender;
    @FXML
    private TextField fieldAlamat;
    @FXML
    private PasswordField fieldPassword;

    // ===== Kontrol =====
    @FXML
    private TextField fieldSearch;
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
    private final DosenDAO dosenDAO = new DosenDAO();
    private final ObservableList<Dosen> dosenList = FXCollections.observableArrayList();
    private FilteredList<Dosen> filteredList;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupComboBox();
        setupSearchAndFilter();
        loadData();
        setupTableSelectionListener();
        setEditMode(false);
    }

    // ===== Setup =====

    private void setupTable() {
        colNo.setCellFactory(col -> new TableCell<Dosen, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNip.setCellValueFactory(new PropertyValueFactory<>("nip"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("jenisKelamin"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));

        tableDosen.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupComboBox() {
        comboGender.setItems(FXCollections.observableArrayList("L", "P"));
    }

    private void setupSearchAndFilter() {
        filteredList = new FilteredList<>(dosenList, p -> true);
        tableDosen.setItems(filteredList);
        fieldSearch.textProperty().addListener((obs, o, n) -> applyFilter());
    }

    private void applyFilter() {
        String keyword = fieldSearch.getText() == null ? "" : fieldSearch.getText().toLowerCase().trim();
        filteredList.setPredicate(d -> {
            if (keyword.isEmpty())
                return true;
            return d.getNip().toLowerCase().contains(keyword)
                    || d.getNama().toLowerCase().contains(keyword)
                    || (d.getAlamat() != null && d.getAlamat().toLowerCase().contains(keyword));
        });
    }

    private void setupTableSelectionListener() {
        tableDosen.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null)
                        populateForm(newSel);
                });
    }

    // ===== Load Data =====

    private void loadData() {
        dosenList.clear();
        List<Dosen> data = dosenDAO.getAll();
        if (data != null)
            dosenList.addAll(data);
    }

    // ===== CRUD Handlers =====

    @FXML
    private void handleTambah(ActionEvent event) {
        if (!isFormValid(true))
            return;
        dosenDAO.insert(buildFromForm());
        AlertHelper.showInfo("Berhasil", "Data dosen berhasil ditambahkan.");
        loadData();
        clearForm();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        if (!isEditMode) {
            if (tableDosen.getSelectionModel().getSelectedItem() == null) {
                AlertHelper.showWarning("Peringatan", "Pilih dosen yang ingin diedit terlebih dahulu.");
                return;
            }
            setEditMode(true);
            return;
        }
        if (!isFormValid(false))
            return;
        dosenDAO.update(buildFromForm());
        AlertHelper.showInfo("Berhasil", "Data dosen berhasil diperbarui.");
        loadData();
        clearForm();
        setEditMode(false);
    }

    @FXML
    private void handleHapus(ActionEvent event) {
        Dosen selected = tableDosen.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih dosen yang ingin dihapus terlebih dahulu.");
            return;
        }
        boolean ok = AlertHelper.showConfirmation("Konfirmasi Hapus",
                "Yakin ingin menghapus \"" + selected.getNama() + "\" (NIP: " + selected.getNip() + ")?");
        if (ok) {
            dosenDAO.delete(selected.getNip());
            AlertHelper.showInfo("Berhasil", "Data dosen berhasil dihapus.");
            loadData();
            clearForm();
            setEditMode(false);
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        clearForm();
        setEditMode(false);
        tableDosen.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleResetFilter(ActionEvent event) {
        fieldSearch.clear();
    }

    // ===== Helpers =====

    private void populateForm(Dosen d) {
        fieldNip.setText(d.getNip());
        fieldNama.setText(d.getNama());
        comboGender.setValue(d.getJenisKelamin());
        fieldAlamat.setText(d.getAlamat() != null ? d.getAlamat() : "");
        fieldPassword.clear();
    }

    private Dosen buildFromForm() {
        return new Dosen(
                fieldNip.getText().trim(),
                fieldNama.getText().trim(),
                comboGender.getValue(),
                fieldAlamat.getText().trim(),
                fieldPassword.getText().trim());
    }

    private boolean isFormValid(boolean cekPassword) {
        if (fieldNip.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "NIP tidak boleh kosong.");
            return false;
        }
        if (fieldNama.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama tidak boleh kosong.");
            return false;
        }
        if (comboGender.getValue() == null) {
            AlertHelper.showWarning("Validasi", "Jenis kelamin harus dipilih.");
            return false;
        }
        if (cekPassword && fieldPassword.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Password tidak boleh kosong.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        fieldNip.clear();
        fieldNama.clear();
        comboGender.setValue(null);
        fieldAlamat.clear();
        fieldPassword.clear();
    }

    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        fieldNip.setDisable(editMode);
        btnEdit.setText(editMode ? "Simpan" : "Edit");
        btnTambah.setDisable(editMode);
    }

    public void selectTab(int index) {
        if (tabPane != null)
            tabPane.getSelectionModel().select(index);
    }
}
