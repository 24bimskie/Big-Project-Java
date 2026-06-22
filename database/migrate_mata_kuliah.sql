-- ============================================================
-- MIGRASI: Rebuild tabel mata_kuliah
-- Jalankan SEMUA sekaligus di phpMyAdmin → tab SQL → klik Go
-- ============================================================

-- Langkah 1: Hapus dulu constraint FK di tabel jadwal yang merujuk ke mata_kuliah
ALTER TABLE `jadwal` DROP FOREIGN KEY `jadwal_ibfk_3`;

-- Langkah 2: Hapus tabel mata_kuliah lama
DROP TABLE IF EXISTS `mata_kuliah`;

-- Langkah 3: Buat ulang tabel mata_kuliah dengan struktur baru yang lengkap
CREATE TABLE `mata_kuliah` (
  `id`       int          NOT NULL AUTO_INCREMENT,
  `kode_mk`  varchar(20)  NOT NULL,
  `nama_mk`  varchar(100) NOT NULL,
  `sks`      int          NOT NULL DEFAULT 2,
  `semester` int          NOT NULL DEFAULT 1,
  `id_prodi` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `kode_mk` (`kode_mk`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Langkah 4: Pasang kembali FK di tabel jadwal ke mata_kuliah yang baru
ALTER TABLE `jadwal`
  ADD CONSTRAINT `jadwal_ibfk_3` FOREIGN KEY (`matkul_id`) REFERENCES `mata_kuliah` (`id`) ON DELETE CASCADE;

-- Langkah 5 (opsional): Masukkan data contoh
-- Ganti nama prodi sesuai data yang ada di tabel kelas Anda
INSERT INTO mata_kuliah (kode_mk, nama_mk, sks, semester, id_prodi) VALUES
('MK001', 'Algoritma dan Pemrograman', 3, 1, 'Teknik Informatika'),
('MK002', 'Basis Data', 3, 2, 'Teknik Informatika'),
('MK003', 'Pemrograman Berorientasi Objek', 3, 3, 'Teknik Informatika');
