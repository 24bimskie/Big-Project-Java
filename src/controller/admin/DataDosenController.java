package controller.admin;

import dao.DosenDAO;
import dao.UserDAO;
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
 * Kolom DB: id, nidn, nama_lengkap, email, fakultas, foto_profil
 * Password disimpan di tabel user (bukan di tabel dosen).
 */
public class DataDosenController implements Initializable {

    @FXML private TabPane tabPane;

    // ===== TableView =====
    @FXML private TableView<Dosen>            tableDosen;
    @FXML private TableColumn<Dosen, Integer> colNo;
    @FXML private TableColumn<Dosen, String>  colNidn;
    @FXML private TableColumn<Dosen, String>  colNamaLengkap;
    @FXML private TableColumn<Dosen, String>  colEmail;
    @FXML private TableColumn<Dosen, String>  colFakultas;
    @FXML private TableColumn<Dosen, Void>    colAksi;

    // ===== Form Input =====
    @FXML private TextField     fieldNidn;
    @FXML private TextField     fieldNamaLengkap;
    @FXML private TextField     fieldEmail;
    @FXML private TextField     fieldFakultas;
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
    private final DosenDAO dosenDAO = new DosenDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<Dosen> dosenList = FXCollections.observableArrayList();
    private FilteredList<Dosen> filteredList;
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
        colNo.setCellFactory(col -> new TableCell<Dosen, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNidn.setCellValueFactory(new PropertyValueFactory<>("nidn"));
        colNamaLengkap.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colFakultas.setCellValueFactory(new PropertyValueFactory<>("fakultas"));

        setDashCellFactory(colNidn);
        setDashCellFactory(colNamaLengkap);
        setDashCellFactory(colEmail);
        setDashCellFactory(colFakultas);

        colAksi.setCellValueFactory(param -> new javafx.beans.property.ReadOnlyObjectWrapper<>(null));
        colAksi.setCellFactory(param -> new TableCell<Dosen, Void>() {
            private final Button btnEditRow = new Button("Edit");
            private final Button btnHapusRow = new Button("Hapus");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(10, btnEditRow, btnHapusRow);

            {
                btnEditRow.getStyleClass().addAll("btn-primary");
                btnEditRow.setStyle("-fx-background-color: #f59e0b; -fx-padding: 5 10; -fx-font-size: 12px; -fx-pref-height: 25px;");
                btnHapusRow.getStyleClass().addAll("btn-danger");
                btnHapusRow.setStyle("-fx-padding: 5 10; -fx-font-size: 12px; -fx-pref-height: 25px;");

                btnEditRow.setOnAction(event -> {
                    Dosen d = getTableView().getItems().get(getIndex());
                    populateForm(d);
                    setEditMode(true);
                    selectTab(1); // Pindah ke tab Input Data
                });

                btnHapusRow.setOnAction(event -> {
                    Dosen d = getTableView().getItems().get(getIndex());
                    boolean ok = AlertHelper.showConfirmation("Konfirmasi Hapus",
                            "Yakin ingin menghapus \"" + d.getNamaLengkap() + "\" (NIDN: " + d.getNidn() + ")?");
                    if (ok) {
                        dosenDAO.delete(d.getNidn());
                        AlertHelper.showInfo("Berhasil", "Data dosen berhasil dihapus.");
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

        tableDosen.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setDashCellFactory(TableColumn<Dosen, String> column) {
        column.setCellFactory(col -> new TableCell<Dosen, String>() {
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
        filteredList = new FilteredList<>(dosenList, p -> true);
        tableDosen.setItems(filteredList);
        fieldSearch.textProperty().addListener((obs, o, n) -> applyFilter());
    }

    private void applyFilter() {
        String keyword = fieldSearch.getText() == null ? "" : fieldSearch.getText().toLowerCase().trim();
        filteredList.setPredicate(d -> {
            if (keyword.isEmpty()) return true;
            return d.getNidn().toLowerCase().contains(keyword)
                || d.getNamaLengkap().toLowerCase().contains(keyword)
                || (d.getFakultas() != null && d.getFakultas().toLowerCase().contains(keyword))
                || (d.getEmail() != null && d.getEmail().toLowerCase().contains(keyword));
        });
    }

    private void setupTableSelectionListener() {
        tableDosen.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> { if (newSel != null) populateForm(newSel); }
        );
    }

    // ===== Load Data =====

    private void loadData() {
        dosenList.clear();
        List<Dosen> data = dosenDAO.getAll();
        if (data != null) dosenList.addAll(data);
    }

    // ===== CRUD Handlers =====

    @FXML
    private void handleTambah(ActionEvent event) {
        if (!isFormValid(true)) return;

        Dosen dosen = buildFromForm();
        String password = fieldPassword.getText().trim();

        // Simpan profil dosen ke tabel dosen (tanpa password)
        dosenDAO.insert(dosen);

        // Simpan akun ke tabel user (dengan password)
        try {
            userDAO.insertUserOnly(dosen.getNidn(), password, "Dosen");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal membuat akun user: " + e.getMessage());
        }

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
        // Saat edit, tidak perlu validasi password
        if (!isFormValid(false)) return;

        // Update hanya data profil (tanpa password)
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
                "Yakin ingin menghapus \"" + selected.getNamaLengkap() + "\" (NIDN: " + selected.getNidn() + ")?");
        if (ok) {
            dosenDAO.delete(selected.getNidn());
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
        fieldNidn.setText(d.getNidn());
        fieldNamaLengkap.setText(d.getNamaLengkap());
        fieldEmail.setText(d.getEmail() != null ? d.getEmail() : "");
        fieldFakultas.setText(d.getFakultas() != null ? d.getFakultas() : "");
        fieldPassword.clear();
    }

    private Dosen buildFromForm() {
        return new Dosen(
                fieldNidn.getText().trim(),
                fieldNamaLengkap.getText().trim(),
                fieldEmail.getText().trim(),
                fieldFakultas.getText().trim()
        );
    }

    private boolean isFormValid(boolean cekPassword) {
        String nidnStr = fieldNidn.getText().trim();
        if (nidnStr.isEmpty()) {
            AlertHelper.showWarning("Validasi", "NIDN tidak boleh kosong."); return false;
        }
        if (!nidnStr.matches("\\d+")) {
            AlertHelper.showWarning("Validasi", "NIDN harus berupa angka."); return false;
        }
        if (fieldNamaLengkap.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama Lengkap tidak boleh kosong."); return false;
        }
        if (cekPassword && fieldPassword.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Password tidak boleh kosong."); return false;
        }
        return true;
    }

    private void clearForm() {
        fieldNidn.clear();
        fieldNamaLengkap.clear();
        fieldEmail.clear();
        fieldFakultas.clear();
        fieldPassword.clear();
    }

    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        fieldNidn.setDisable(editMode);
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
