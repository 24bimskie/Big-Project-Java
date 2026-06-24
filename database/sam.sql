-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jun 24, 2026 at 04:57 AM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sam`
--

-- --------------------------------------------------------

--
-- Table structure for table `dosen`
--

CREATE TABLE `dosen` (
  `id` int NOT NULL,
  `nidn` varchar(30) DEFAULT NULL,
  `nama_lengkap` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `fakultas` varchar(100) DEFAULT NULL,
  `foto_profil` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dosen`
--

INSERT INTO `dosen` (`id`, `nidn`, `nama_lengkap`, `email`, `fakultas`, `foto_profil`) VALUES
(4, '232444142', 'Boy', 'boy@gmail.com', 'Ekonomi', NULL),
(5, '37890485758', 'Maman', 'maman@gmail.com', 'FIS', NULL),
(6, '198501012010', 'Siti Rahayu', 'siti@gmail.com', 'Teknik', NULL),
(7, '197803022009', 'Budi Santoso', 'budi@gmail.com', 'Teknik', NULL),
(8, '199002012015', 'Rina Wati', 'rina@gmail.com', 'FIS', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `jadwal`
--

CREATE TABLE `jadwal` (
  `id` int NOT NULL,
  `hari` varchar(20) NOT NULL,
  `kelas_id` int NOT NULL,
  `dosen_id` int NOT NULL,
  `matkul_id` int NOT NULL,
  `jam_mulai` varchar(10) NOT NULL DEFAULT '',
  `jam_selesai` varchar(10) NOT NULL DEFAULT '',
  `ruangan` varchar(100) NOT NULL DEFAULT '',
  `tahun_akademik` varchar(20) NOT NULL DEFAULT '',
  `semester` varchar(10) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jadwal`
--

INSERT INTO `jadwal` (`id`, `hari`, `kelas_id`, `dosen_id`, `matkul_id`, `jam_mulai`, `jam_selesai`, `ruangan`, `tahun_akademik`, `semester`) VALUES
(17, 'Senin', 1, 4, 13, '08:00', '10:30', 'Lab Komputer 1', '2024/2025', 'Genap'),
(18, 'Selasa', 2, 5, 14, '08:00', '10:30', 'Lab Komputer 2', '2024/2025', 'Genap'),
(19, 'Rabu', 3, 4, 15, '10:00', '12:30', 'Ruang 301', '2024/2025', 'Genap'),
(20, 'Kamis', 4, 5, 18, '13:00', '15:30', 'Ruang 302', '2024/2025', 'Genap'),
(21, 'Jumat', 5, 6, 19, '08:00', '10:30', 'Ruang 201', '2024/2025', 'Genap'),
(22, 'Senin', 6, 8, 20, '13:00', '15:30', 'Ruang 101', '2024/2025', 'Genap'),
(23, 'Rabu', 1, 7, 16, '08:00', '10:30', 'Lab Komputer 3', '2024/2025', 'Genap'),
(24, 'Kamis', 2, 4, 17, '08:00', '10:30', 'Ruang 303', '2024/2025', 'Genap'),
(25, 'Jumat', 7, 6, 21, '13:00', '15:30', 'Ruang 102', '2024/2025', 'Genap'),
(26, 'Senin', 8, 4, 22, '10:00', '12:30', 'Ruang 201', '2024/2025', 'Genap'),
(27, 'Selasa', 9, 5, 23, '13:00', '15:30', 'Ruang 202', '2024/2025', 'Genap'),
(28, 'Jumat', 2, 4, 14, '08.00', '10.30', 'Lab Komputer 1', '2024/2025', 'Ganjil'),
(29, 'Selasa', 2, 4, 19, '08.00', '10.30', 'Lab Komputer 2', '2025/2026', 'Ganjil');

-- --------------------------------------------------------

--
-- Table structure for table `kelas`
--

CREATE TABLE `kelas` (
  `id` int NOT NULL,
  `Prodi` varchar(100) NOT NULL,
  `kelas` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `kelas`
--

INSERT INTO `kelas` (`id`, `Prodi`, `kelas`) VALUES
(8, 'Ekonomi', 'EKO-A'),
(9, 'Ekonomi', 'EKO-B'),
(6, 'Psikologi', 'PSI-A'),
(7, 'Psikologi', 'PSI-B'),
(4, 'Sistem Informasi', 'SI-A'),
(5, 'Sistem Informasi', 'SI-B'),
(1, 'Teknik Informatika', 'TI-A'),
(2, 'Teknik Informatika', 'TI-B'),
(3, 'Teknik Informatika', 'TI-C');

-- --------------------------------------------------------

--
-- Table structure for table `mahasiswa`
--

CREATE TABLE `mahasiswa` (
  `id` int NOT NULL,
  `nim` varchar(15) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `gender` enum('L','P') NOT NULL,
  `alamat` text,
  `kelas` varchar(10) DEFAULT NULL,
  `prodi` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mahasiswa`
--

INSERT INTO `mahasiswa` (`id`, `nim`, `nama`, `gender`, `alamat`, `kelas`, `prodi`) VALUES
(2, '24252324', 'Alex', 'L', 'Lampung', 'TI-C', 'Teknik Informatika'),
(3, '21212121', 'Waluyo', 'P', 'Tanjung Verde', 'PSI-A', 'Psikologi'),
(4, '230492852', 'Boboiboy', 'L', 'Jombang', NULL, NULL),
(5, '22334455', 'Dewi Sartika', 'P', 'Bandung', 'TI-A', 'Teknik Informatika'),
(6, '22334466', 'Rizky Pratama', 'L', 'Jakarta', 'TI-B', 'Teknik Informatika'),
(7, '22334477', 'Siti Aminah', 'P', 'Surabaya', 'TI-C', 'Teknik Informatika'),
(8, '22334488', 'Budi Raharjo', 'L', 'Yogyakarta', 'SI-A', 'Sistem Informasi'),
(9, '22334499', 'Lia Permata', 'P', 'Semarang', 'SI-B', 'Sistem Informasi'),
(10, '22334500', 'Hendra Gunawan', 'L', 'Medan', 'PSI-A', 'Psikologi'),
(11, '22334511', 'Putri Maharani', 'P', 'Makassar', 'EKO-A', 'Ekonomi');

-- --------------------------------------------------------

--
-- Table structure for table `mata_kuliah`
--

CREATE TABLE `mata_kuliah` (
  `id` int NOT NULL,
  `kode_mk` varchar(20) NOT NULL,
  `nama_mk` varchar(100) NOT NULL,
  `sks` int NOT NULL DEFAULT '2',
  `semester` int NOT NULL DEFAULT '1',
  `id_prodi` varchar(100) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mata_kuliah`
--

INSERT INTO `mata_kuliah` (`id`, `kode_mk`, `nama_mk`, `sks`, `semester`, `id_prodi`) VALUES
(13, 'MK001', 'Java', 3, 4, 'Teknik Informatika'),
(14, 'MK002', 'Pemrograman Web', 3, 3, 'Teknik Informatika'),
(15, 'MK003', 'Basis Data', 3, 2, 'Teknik Informatika'),
(16, 'MK004', 'Struktur Data', 2, 3, 'Teknik Informatika'),
(17, 'MK005', 'Jaringan Komputer', 2, 4, 'Teknik Informatika'),
(18, 'SI001', 'Sistem Informasi Manajemen', 3, 3, 'Sistem Informasi'),
(19, 'SI002', 'Analisis Sistem', 2, 4, 'Sistem Informasi'),
(20, 'PSI001', 'Psikologi Dasar', 3, 1, 'Psikologi'),
(21, 'PSI002', 'Psikologi Sosial', 2, 3, 'Psikologi'),
(22, 'EKO001', 'Pengantar Ekonomi', 3, 1, 'Ekonomi'),
(23, 'EKO002', 'Ekonomi Mikro', 3, 2, 'Ekonomi');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `level` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`, `password`, `level`) VALUES
(1, 'alex', 'alex123', 'Admin'),
(2, 'Yanto', 'yanto123', 'Dosen'),
(3, 'Andi', 'andi321', 'Mahasiswa'),
(4, 'admin', 'admin321', 'Admin');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `dosen`
--
ALTER TABLE `dosen`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nip` (`nidn`);

--
-- Indexes for table `jadwal`
--
ALTER TABLE `jadwal`
  ADD PRIMARY KEY (`id`),
  ADD KEY `kelas_id` (`kelas_id`),
  ADD KEY `dosen_id` (`dosen_id`),
  ADD KEY `matkul_id` (`matkul_id`);

--
-- Indexes for table `kelas`
--
ALTER TABLE `kelas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_prodi_kelas` (`Prodi`,`kelas`);

--
-- Indexes for table `mahasiswa`
--
ALTER TABLE `mahasiswa`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nim` (`nim`);

--
-- Indexes for table `mata_kuliah`
--
ALTER TABLE `mata_kuliah`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode_mk` (`kode_mk`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `dosen`
--
ALTER TABLE `dosen`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `jadwal`
--
ALTER TABLE `jadwal`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `kelas`
--
ALTER TABLE `kelas`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `mahasiswa`
--
ALTER TABLE `mahasiswa`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `mata_kuliah`
--
ALTER TABLE `mata_kuliah`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `jadwal`
--
ALTER TABLE `jadwal`
  ADD CONSTRAINT `jadwal_ibfk_1` FOREIGN KEY (`kelas_id`) REFERENCES `kelas` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `jadwal_ibfk_2` FOREIGN KEY (`dosen_id`) REFERENCES `dosen` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `jadwal_ibfk_3` FOREIGN KEY (`matkul_id`) REFERENCES `mata_kuliah` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
