-- ============================================================
-- MIGRASI: Tambah data dummy tabel jadwal
-- Sesuai dengan database sam.sql terbaru
-- ============================================================

INSERT INTO `jadwal` (
  `hari`, `kelas_id`, `dosen_id`, `matkul_id`,
  `jam_mulai`, `jam_selesai`, `ruangan`, `tahun_akademik`, `semester`
)
SELECT 'Senin',
       (SELECT `id` FROM `kelas` WHERE `Prodi` = 'Teknik Informatika' AND `kelas` = 'TI-A' LIMIT 1),
       (SELECT `id` FROM `dosen` WHERE `nidn` = '232444142' LIMIT 1),
       (SELECT `id` FROM `mata_kuliah` WHERE `kode_mk` = 'MK001' LIMIT 1),
       '08:00', '10:30', 'Lab Komputer 1', '2024/2025', 'Genap'
WHERE EXISTS (SELECT 1 FROM `kelas` WHERE `Prodi` = 'Teknik Informatika' AND `kelas` = 'TI-A')
  AND EXISTS (SELECT 1 FROM `dosen` WHERE `nidn` = '232444142')
  AND EXISTS (SELECT 1 FROM `mata_kuliah` WHERE `kode_mk` = 'MK001');

INSERT INTO `jadwal` (
  `hari`, `kelas_id`, `dosen_id`, `matkul_id`,
  `jam_mulai`, `jam_selesai`, `ruangan`, `tahun_akademik`, `semester`
)
SELECT 'Selasa',
       (SELECT `id` FROM `kelas` WHERE `Prodi` = 'Teknik Informatika' AND `kelas` = 'TI-B' LIMIT 1),
       (SELECT `id` FROM `dosen` WHERE `nidn` = '37890485758' LIMIT 1),
       (SELECT `id` FROM `mata_kuliah` WHERE `kode_mk` = 'MK002' LIMIT 1),
       '08:00', '10:30', 'Lab Komputer 2', '2024/2025', 'Genap'
WHERE EXISTS (SELECT 1 FROM `kelas` WHERE `Prodi` = 'Teknik Informatika' AND `kelas` = 'TI-B')
  AND EXISTS (SELECT 1 FROM `dosen` WHERE `nidn` = '37890485758')
  AND EXISTS (SELECT 1 FROM `mata_kuliah` WHERE `kode_mk` = 'MK002');

INSERT INTO `jadwal` (
  `hari`, `kelas_id`, `dosen_id`, `matkul_id`,
  `jam_mulai`, `jam_selesai`, `ruangan`, `tahun_akademik`, `semester`
)
SELECT 'Rabu',
       (SELECT `id` FROM `kelas` WHERE `Prodi` = 'Sistem Informasi' AND `kelas` = 'SI-A' LIMIT 1),
       (SELECT `id` FROM `dosen` WHERE `nidn` = '232444142' LIMIT 1),
       (SELECT `id` FROM `mata_kuliah` WHERE `kode_mk` = 'SI001' LIMIT 1),
       '10:00', '12:30', 'Ruang 301', '2024/2025', 'Genap'
WHERE EXISTS (SELECT 1 FROM `kelas` WHERE `Prodi` = 'Sistem Informasi' AND `kelas` = 'SI-A')
  AND EXISTS (SELECT 1 FROM `dosen` WHERE `nidn` = '232444142')
  AND EXISTS (SELECT 1 FROM `mata_kuliah` WHERE `kode_mk` = 'SI001');

INSERT INTO `jadwal` (
  `hari`, `kelas_id`, `dosen_id`, `matkul_id`,
  `jam_mulai`, `jam_selesai`, `ruangan`, `tahun_akademik`, `semester`
)
SELECT 'Kamis',
       (SELECT `id` FROM `kelas` WHERE `Prodi` = 'Psikologi' AND `kelas` = 'PSI-A' LIMIT 1),
       (SELECT `id` FROM `dosen` WHERE `nidn` = '198501012010' LIMIT 1),
       (SELECT `id` FROM `mata_kuliah` WHERE `kode_mk` = 'PSI001' LIMIT 1),
       '13:00', '15:30', 'Ruang 201', '2024/2025', 'Genap'
WHERE EXISTS (SELECT 1 FROM `kelas` WHERE `Prodi` = 'Psikologi' AND `kelas` = 'PSI-A')
  AND EXISTS (SELECT 1 FROM `dosen` WHERE `nidn` = '198501012010')
  AND EXISTS (SELECT 1 FROM `mata_kuliah` WHERE `kode_mk` = 'PSI001');
