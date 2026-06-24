-- ============================================================
-- MIGRASI: Tambah data dummy tabel jadwal
-- Sesuai dengan data kelas, dosen, dan mata kuliah yang ada
-- ============================================================

INSERT INTO `jadwal` (`hari`, `jam`, `kelas_id`, `dosen_id`, `matkul_id`)
SELECT 'Senin', '08:00:00',
       (SELECT `id` FROM `kelas` WHERE `Prodi` = 'Teknik Informatika' AND `kelas` = '1A' LIMIT 1),
       (SELECT `id` FROM `dosen` WHERE `nidn` = '11223344' LIMIT 1),
       (SELECT `id` FROM `mata_kuliah` WHERE `kode_mk` = 'MK001' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM `kelas` WHERE `Prodi` = 'Teknik Informatika' AND `kelas` = '1A')
  AND EXISTS (SELECT 1 FROM `dosen` WHERE `nidn` = '11223344')
  AND EXISTS (SELECT 1 FROM `mata_kuliah` WHERE `kode_mk` = 'MK001');

INSERT INTO `jadwal` (`hari`, `jam`, `kelas_id`, `dosen_id`, `matkul_id`)
SELECT 'Rabu', '10:00:00',
       (SELECT `id` FROM `kelas` WHERE `Prodi` = 'Sistem Informasi' AND `kelas` = '1B' LIMIT 1),
       (SELECT `id` FROM `dosen` WHERE `nidn` = '11223344' LIMIT 1),
       (SELECT `id` FROM `mata_kuliah` WHERE `kode_mk` = 'MK002' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM `kelas` WHERE `Prodi` = 'Sistem Informasi' AND `kelas` = '1B')
  AND EXISTS (SELECT 1 FROM `dosen` WHERE `nidn` = '11223344')
  AND EXISTS (SELECT 1 FROM `mata_kuliah` WHERE `kode_mk` = 'MK002');

INSERT INTO `jadwal` (`hari`, `jam`, `kelas_id`, `dosen_id`, `matkul_id`)
SELECT 'Jumat', '13:00:00',
       (SELECT `id` FROM `kelas` WHERE `Prodi` = 'Teknik Informatika' AND `kelas` = '2A' LIMIT 1),
       (SELECT `id` FROM `dosen` WHERE `nidn` = '232444142' LIMIT 1),
       (SELECT `id` FROM `mata_kuliah` WHERE `kode_mk` = 'MK003' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM `kelas` WHERE `Prodi` = 'Teknik Informatika' AND `kelas` = '2A')
  AND EXISTS (SELECT 1 FROM `dosen` WHERE `nidn` = '232444142')
  AND EXISTS (SELECT 1 FROM `mata_kuliah` WHERE `kode_mk` = 'MK003');
