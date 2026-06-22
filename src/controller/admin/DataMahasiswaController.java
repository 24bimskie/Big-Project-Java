package controller.admin;

import dao.MahasiswaDAO;
import dao.UserDAO;
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
 * Kolom DB: nim, nama, gender, alamat, kelas, prodi
 * Password disimpan di tabel user (bukan di tabel mahasiswa).
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
    @FXML private TableColumn<Mahasiswa, Void>    colAksi;

    // ===== Form Input =====
    @FXML private TextField     fieldNim;
    @FXML private TextField     fieldNama;
    @FXML private RadioButton   radioL;
    @FXML private RadioButton   radioP;
    @FXML private ToggleGroup   genderGroup;
    @FXML private TextField     fieldAlamat;
    @FXML private ComboBox<String> comboKelas;
    @FXML private ComboBox<String> comboProdi;
    @FXML private PasswordField fieldPassword;
    @FXML private TextField     fieldPasswordVisible;
    @FXML private ToggleButton  btnTogglePassword;

    // ===== Kontrol =====
    @FXML private TextField fieldSearch;
    @FXML private Button    btnResetFilter;
    @FXML private Button    btnTambah;
    @FXML private Button    btnEdit;
    @FXML private Button    btnHapus;
    @FXML private Button    btnBatal;

    // ===== State =====
    private final MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<Mahasiswa> mahasiswaList = FXCollections.observableArrayList();
    private FilteredList<Mahasiswa> filteredList;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupPasswordToggle();
        setupSearchAndFilter();
        loadData();
        setupTableSelectionListener();
        setEditMode(false);
    }

    // ===== Setup =====

    private void setupTable() {
        colNo.setCellValueFactory(param -> new javafx.beans.property.ReadOnlyObjectWrapper<>(0));
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

        setDashCellFactory(colNim);
        setDashCellFactory(colNama);
        setDashCellFactory(colGender);
        setDashCellFactory(colAlamat);
        setDashCellFactory(colKelas);
        setDashCellFactory(colProdi);

        colAksi.setCellValueFactory(param -> new javafx.beans.property.ReadOnlyObjectWrapper<>(null));
        colAksi.setCellFactory(param -> new TableCell<Mahasiswa, Void>() {
            private final Button btnEditRow = new Button("Edit");
            private final Button btnHapusRow = new Button("Hapus");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(10, btnEditRow, btnHapusRow);

            {
                btnEditRow.getStyleClass().addAll("btn-primary");
                btnEditRow.setStyle("-fx-background-color: #f59e0b; -fx-padding: 5 10; -fx-font-size: 12px; -fx-pref-height: 25px;");
                btnHapusRow.getStyleClass().addAll("btn-danger");
                btnHapusRow.setStyle("-fx-padding: 5 10; -fx-font-size: 12px; -fx-pref-height: 25px;");

                btnEditRow.setOnAction(event -> {
                    Mahasiswa m = getTableView().getItems().get(getIndex());
                    populateForm(m);
                    setEditMode(true);
                    selectTab(1); // Pindah ke tab Input Data
                });

                btnHapusRow.setOnAction(event -> {
                    Mahasiswa m = getTableView().getItems().get(getIndex());
                    boolean ok = AlertHelper.showConfirmation("Konfirmasi Hapus",
                            "Yakin ingin menghapus \"" + m.getNama() + "\" (NIM: " + m.getNim() + ")?");
                    if (ok) {
                        mahasiswaDAO.delete(m.getNim());
                        AlertHelper.showInfo("Berhasil", "Data mahasiswa berhasil dihapus.");
                        loadData();
                        clearForm();
                        setEditMode(false);
                    }
                });
                pane.setAlignment(javafx.geometry.Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableMahasiswa.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setDashCellFactory(TableColumn<Mahasiswa, String> column) {
        column.setCellFactory(col -> new TableCell<Mahasiswa, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText((item == null || item.trim().isEmpty()) ? "-" : item);
                }
            }
        });
    }

    private void setupPasswordToggle() {
        fieldPasswordVisible.textProperty().bindBidirectional(fieldPassword.textProperty());
        btnTogglePassword.setOnAction(e -> {
            boolean show = btnTogglePassword.isSelected();
            fieldPasswordVisible.setVisible(show);
            fieldPasswordVisible.setManaged(show);
            fieldPassword.setVisible(!show);
            fieldPassword.setManaged(!show);
            btnTogglePassword.setText(show ? "🙈" : "👁");
        });
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

        Mahasiswa mhs = buildFromForm();
        String password = fieldPassword.getText().trim();

        // Simpan profil mahasiswa ke tabel mahasiswa (tanpa password)
        mahasiswaDAO.insert(mhs);

        // Simpan akun ke tabel user (dengan password)
        try {
            userDAO.insertUserOnly(mhs.getNim(), password, "Mahasiswa");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal membuat akun user: " + e.getMessage());
        }

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
        // Saat edit, tidak perlu validasi password
        if (!isFormValid(false)) return;

        // Update hanya data profil (tanpa password)
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
        if ("L".equals(m.getJenisKelamin())) radioL.setSelected(true);
        else if ("P".equals(m.getJenisKelamin())) radioP.setSelected(true);
        else if (genderGroup != null) genderGroup.selectToggle(null);
        fieldAlamat.setText(m.getAlamat());
        comboKelas.setValue(m.getKelas() != null ? m.getKelas() : null);
        comboProdi.setValue(m.getProdi() != null ? m.getProdi() : null);
        fieldPassword.clear();
    }

    private Mahasiswa buildFromForm() {
        return new Mahasiswa(
                fieldNim.getText().trim(),
                fieldNama.getText().trim(),
                radioL.isSelected() ? "L" : (radioP.isSelected() ? "P" : null),
                fieldAlamat.getText().trim(),
                comboKelas.getValue(),
                comboProdi.getValue()
        );
    }

    private boolean isFormValid(boolean cekPassword) {
        String nimStr = fieldNim.getText().trim();
        if (nimStr.isEmpty()) {
            AlertHelper.showWarning("Validasi", "NIM tidak boleh kosong."); return false;
        }
        if (!nimStr.matches("\\d+")) {
            AlertHelper.showWarning("Validasi", "NIM harus berupa angka."); return false;
        }
        if (fieldNama.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama tidak boleh kosong."); return false;
        }
        if (!radioL.isSelected() && !radioP.isSelected()) {
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
        if (genderGroup != null) genderGroup.selectToggle(null);
        fieldAlamat.clear();
        comboKelas.setValue(null);
        comboProdi.setValue(null);
        fieldPassword.clear();
    }

    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        fieldNim.setDisable(editMode);
        btnEdit.setText(editMode ? "Simpan" : "Edit");
        btnTambah.setVisible(!editMode);
        btnTambah.setManaged(!editMode);

        // Sembunyikan password field saat mode edit (password tidak diubah dari sini)
        fieldPassword.setVisible(!editMode);
        fieldPassword.setManaged(!editMode);
        fieldPasswordVisible.setVisible(false);
        fieldPasswordVisible.setManaged(false);
        btnTogglePassword.setVisible(!editMode);
        btnTogglePassword.setManaged(!editMode);
    }

    public void selectTab(int index) {
        if (tabPane != null) tabPane.getSelectionModel().select(index);
    }
}
