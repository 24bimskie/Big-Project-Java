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
import java.util.UUID;

/**
 * Controller untuk use case: Input Data Prodi & Kelas Gabungan.
 * Admin mengelola program studi dan kelas secara bersamaan dalam satu form.
 * Halaman ini dibagi dua panel: panel Prodi (kiri) dan panel Kelas (kanan) di tab "Lihat Data".
 * Tab "Input Data" menampilkan form gabungan untuk input prodi dan kelas bersama-sama.
 */
public class DataProdiKelasController implements Initializable {

    // ===== FXML Bindings — Tab Pane =====

    @FXML
    private TabPane tabPane;

    // ===== FXML Bindings — Tabel Kelas =====

    @FXML
    private TableView<Kelas> tableProdi;

    @FXML
    private TableColumn<Kelas, String> colId;

    @FXML
    private TableColumn<Kelas, String> colProdi;

    @FXML
    private TableColumn<Kelas, String> colKelas;

    @FXML
    private TableColumn<Kelas, String> colTahunAkademik;

    @FXML
    private TableColumn<Kelas, Void> colAksi;

    // ===== FXML Bindings — Form =====

    @FXML
    private TextField fieldNamaProdi;

    @FXML
    private TextField fieldNamaKelas;

    @FXML
    private TextField fieldTahunAkademik;

    @FXML
    private Button btnTambah;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnHapus;

    @FXML
    private Button btnBatal;

    // ===== State =====

    private final ProdiDAO prodiDAO = new ProdiDAO();
    private final KelasDAO kelasDAO = new KelasDAO();

    private final ObservableList<Prodi> prodiList = FXCollections.observableArrayList();
    private final ObservableList<Kelas> kelasDisplayList = FXCollections.observableArrayList();

    /** Prodi yang sedang aktif/dipilih di tabel */
    private Prodi selectedProdi = null;
    
    /** Kelas yang sedang aktif/dipilih di tabel */
    private Kelas selectedKelas = null;

    private boolean isEditMode = false;

    // ===== Inisialisasi =====

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableCombined();
        loadCombinedData();
        setEditMode(false);
    }

    // ===== Mapping kolom tabel kelas =====

    private void setupTableCombined() {
        // Kolom ID dari database
        colId.setCellValueFactory(new PropertyValueFactory<>("idKelas"));
        
        // Kolom Prodi (nama program studi)
        colProdi.setCellValueFactory(new PropertyValueFactory<>("idProdi"));
        
        // Kolom Kelas (nama kelas)
        colKelas.setCellValueFactory(new PropertyValueFactory<>("namaKelas"));
        
        // Kolom Tahun Akademik
        colTahunAkademik.setCellValueFactory(new PropertyValueFactory<>("tahunAkademik"));
        
        // Kolom Aksi dengan tombol Edit dan Hapus
        colAksi.setCellValueFactory(param -> new javafx.beans.property.ReadOnlyObjectWrapper<>(null));
        colAksi.setCellFactory(param -> new TableCell<Kelas, Void>() {
            private final Button btnEditRow = new Button("Edit");
            private final Button btnHapusRow = new Button("Hapus");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(10, btnEditRow, btnHapusRow);

            {
                btnEditRow.getStyleClass().addAll("btn-primary");
                btnEditRow.setStyle(
                        "-fx-background-color: #f59e0b; -fx-padding: 5 10; -fx-font-size: 12px; -fx-pref-height: 25px;");
                btnHapusRow.getStyleClass().addAll("btn-danger");
                btnHapusRow.setStyle("-fx-padding: 5 10; -fx-font-size: 12px; -fx-pref-height: 25px;");

                btnEditRow.setOnAction(event -> {
                    Kelas k = getTableView().getItems().get(getIndex());
                    selectedKelas = k;
                    fieldNamaProdi.setText(k.getIdProdi());
                    fieldNamaKelas.setText(k.getNamaKelas());
                    fieldTahunAkademik.setText(k.getTahunAkademik());
                    setEditMode(true);
                    selectTab(1); // Pindah ke tab Input Data
                });

                btnHapusRow.setOnAction(event -> {
                    Kelas k = getTableView().getItems().get(getIndex());
                    boolean ok = AlertHelper.showConfirmation("Konfirmasi Hapus",
                            "Yakin ingin menghapus kelas \"" + k.getNamaKelas() + "\" (" + k.getIdProdi() + ")?");
                    if (ok) {
                        kelasDAO.delete(k.getIdKelas());
                        AlertHelper.showInfo("Berhasil", "Data kelas berhasil dihapus.");
                        loadCombinedData();
                        clearForm();
                        selectedKelas = null;
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
        
        tableProdi.setItems(kelasDisplayList);
    }

    // ===== Load Data =====

    private void loadCombinedData() {
        kelasDisplayList.clear();
        
        // Load semua kelas dari database dan tampilkan
        List<Kelas> allKelas = kelasDAO.getAll();
        if (allKelas != null) {
            kelasDisplayList.addAll(allKelas);
        }
    }

    @FXML
    private void handleTambah(ActionEvent event) {
        if (!isFormValid())
            return;

        try {
            // Generate ID untuk kelas saja (Prodi disimpan sebagai nama di tabel kelas)
            String idKelas = generateIdKelas();
            String namaProdi = fieldNamaProdi.getText().trim();
            String namaKelas = fieldNamaKelas.getText().trim();
            String tahunAkademik = fieldTahunAkademik.getText().trim();

            // Simpan langsung ke kelas dengan Prodi sebagai nama
            Kelas newKelas = new Kelas(idKelas, namaKelas, namaProdi, tahunAkademik);
            kelasDAO.insert(newKelas);

            AlertHelper.showInfo("Berhasil", "Data prodi dan kelas berhasil ditambahkan.");
            loadCombinedData();
            clearForm();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menambahkan data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        if (!isEditMode) {
            Kelas selected = tableProdi.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Peringatan", "Pilih kelas di tabel terlebih dahulu untuk edit.");
                return;
            }
            selectedKelas = selected;
            
            fieldNamaProdi.setText(selected.getIdProdi());
            fieldNamaKelas.setText(selected.getNamaKelas());
            fieldTahunAkademik.setText(selected.getTahunAkademik());
            
            setEditMode(true);
            return;
        }

        if (!isFormValid())
            return;

        try {
            // Update kelas (Prodi disimpan di field idProdi)
            String namaProdi = fieldNamaProdi.getText().trim();
            String namaKelas = fieldNamaKelas.getText().trim();
            String tahunAkademik = fieldTahunAkademik.getText().trim();
            
            Kelas updatedKelas = new Kelas(selectedKelas.getIdKelas(), namaKelas, namaProdi, tahunAkademik);
            kelasDAO.update(updatedKelas);

            AlertHelper.showInfo("Berhasil", "Data prodi dan kelas berhasil diperbarui.");
            loadCombinedData();
            clearForm();
            setEditMode(false);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal mengupdate data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHapus(ActionEvent event) {
        Kelas selected = tableProdi.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih kelas di tabel terlebih dahulu.");
            return;
        }
        
        boolean konfirmasi = AlertHelper.showConfirmation(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus kelas \"" + selected.getNamaKelas() + "\"?");

        if (konfirmasi) {
            try {
                kelasDAO.delete(selected.getIdKelas());
                AlertHelper.showInfo("Berhasil", "Data kelas berhasil dihapus.");
                loadCombinedData();
                clearForm();
                selectedKelas = null;
                selectedProdi = null;
                setEditMode(false);
            } catch (Exception e) {
                AlertHelper.showError("Error", "Gagal menghapus data: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        clearForm();
        setEditMode(false);
        selectedProdi = null;
        selectedKelas = null;
        tableProdi.getSelectionModel().clearSelection();
    }

    // ===== Helper Methods =====

    private void populateFormFromProdi(Prodi p) {
        fieldNamaProdi.setText(p.getNamaProdi());
        fieldNamaKelas.clear();
        fieldTahunAkademik.clear();
    }

    private boolean isFormValid() {
        if (fieldNamaProdi.getText().trim().isEmpty()) {
            AlertHelper.showWarning("Validasi", "Nama prodi tidak boleh kosong.");
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

    private void clearForm() {
        fieldNamaProdi.clear();
        fieldNamaKelas.clear();
        fieldTahunAkademik.clear();
    }

    private void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        if (editMode) {
            btnEdit.setText("Simpan");
            btnTambah.setDisable(true);
        } else {
            btnEdit.setText("Edit");
            btnTambah.setDisable(false);
        }
    }

    /**
     * Generate ID Kelas secara otomatis.
     * Format: KLS-<UUID singkat>
     */
    private String generateIdKelas() {
        return "KLS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void selectTab(int index) {
        if (tabPane != null && index >= 0 && index < tabPane.getTabs().size()) {
            tabPane.getSelectionModel().select(index);
        }
    }
}
