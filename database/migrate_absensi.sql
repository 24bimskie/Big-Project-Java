-- Dummy data untuk tabel absensi
-- Jalankan file ini di database Anda untuk menambah sample data absensi

CREATE TABLE IF NOT EXISTS absensi (
    id_absensi INT AUTO_INCREMENT PRIMARY KEY,
    tanggal DATE NOT NULL,
    hari_jam VARCHAR(50) NOT NULL,
    mata_kuliah VARCHAR(100) NOT NULL,
    kelas VARCHAR(20) NOT NULL,
    nim VARCHAR(20) NOT NULL,
    nama_lengkap VARCHAR(150) NOT NULL,
    kehadiran ENUM('Hadir', 'Sakit', 'Izin', 'Alfa') NOT NULL DEFAULT 'Hadir',
    keterangan TEXT NULL
);

INSERT INTO absensi (tanggal, hari_jam, mata_kuliah, kelas, nim, nama_lengkap, kehadiran, keterangan) VALUES
('2026-06-24', 'Selasa, 08.00', 'Jaringan Komputer', 'TI-C', '202310001', 'Ahmad Fauzi', 'Hadir', ''),
('2026-06-24', 'Selasa, 08.00', 'Jaringan Komputer', 'TI-C', '202310002', 'Bunga Lestari', 'Sakit', 'Demam ringan'),
('2026-06-24', 'Selasa, 08.00', 'Jaringan Komputer', 'TI-C', '202310003', 'Candra Wijaya', 'Izin', 'Ada acara keluarga'),
('2026-06-24', 'Selasa, 08.00', 'Jaringan Komputer', 'TI-C', '202310004', 'Dewi Anggraini', 'Alfa', 'Tidak masuk tanpa pemberitahuan'),
('2026-06-24', 'Selasa, 08.00', 'Jaringan Komputer', 'TI-C', '202310005', 'Eko Prabowo', 'Hadir', ''),
('2026-06-24', 'Selasa, 08.00', 'Jaringan Komputer', 'TI-C', '202310006', 'Fira Safitri', 'Hadir', ''),
('2026-06-24', 'Selasa, 08.00', 'Jaringan Komputer', 'TI-C', '202310007', 'Gilang Ramadhan', 'Sakit', 'Flu ringan'),
('2026-06-24', 'Selasa, 08.00', 'Jaringan Komputer', 'TI-C', '202310008', 'Hana Permata', 'Izin', 'Rapat kampus');
