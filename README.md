 # LAPORAN APLIKASI PRAKTIKUM PEMROGRAMAN WEB

## 1. JUDUL APLIKASI
Sistem Manajemen Kos "CosCosTan"

## 2. LATAR BELAKANG
Aplikasi ini dikembangkan untuk memenuhi tugas mata kuliah Pemrograman Berorientasi Objek. Aplikasi manajemen kos dirancang untuk membantu pemilik kos dalam mengelola properti mereka dan memberikan informasi yang jelas kepada calon penghuni.

## 3. TUJUAN
- Membantu pemilik kos mengelola data kamar dan penghuni
- Memberikan informasi lengkap kepada calon penghuni
- Mengimplementasikan sistem autentikasi berbasis role
- Menerapkan konsep CRUD dalam manajemen data
- Menggunakan modularitas dalam struktur kode

## 4. TEKNOLOGI YANG DIGUNAKAN
- Bahasa Pemrograman: Java
- IDE: NetBeans
- Database: MySQL
- GUI: Java Swing
- Koneksi Database: JDBC

## 5. STRUKTUR DATABASE

 TABEL ADMIN
- id_admin (INT, PRIMARY KEY, AUTO_INCREMENT)
- admin_name (VARCHAR(50), UNIQUE, NOT NULL)
- password (VARCHAR(255), NOT NULL)
- no_telepon (VARCHAR(15))
- email (VARCHAR(100))

 TABEL KAMARS
- id_kamar (INT, PRIMARY KEY, AUTO_INCREMENT)
- id_tipe_kamar (INT FOREIGN KEY (tipe_kamar))
- nomor_kamar (VARCHAR(10), UNIQUE, NOT NULL)
- nama_kamar (VARCHAR(50))
- lantai (INT)
- status (ENUM: 'tersedia', 'terisi', 'maintenance')

TABEL TIPE KAMAR
- id_tipe_kamar (INT PRIMARY KEY)
- ukuran (VARCHAR(30))
- fasilitas_kamar (TEXT)
- tipe_kamar(VARCHAR(5))
- harga_sewa(DECIMAL(10,2))
- lama_sewa (ENUM: ‘3 bulan’, ‘6 bulan’, ‘12 bulan’)
 TABEL PENGHUNI
- id_penghuni (INT, PRIMARY KEY, AUTO_INCREMENT)
- id_kamar (INT, FOREIGN KEY)
- tanggal_masuk (DATE)
- tanggal_keluar (DATE)
- kontak (VARCHAR(30))

## 6. FITUR APLIKASI

### FITUR UMUM
- Sistem login dengan autentikasi
- Interface berbasis GUI
- Validasi input data
- Konfirmasi aksi kritikal

### FITUR ADMIN
- Kelola data kamar (CRUD)
- Kelola data penghuni (CRUD)
- Lihat dan kelola reviews
- Update status kamar
- Monitoring penghuni aktif

### FITUR USER BIASA
- Lihat informasi kos
- Lihat fasilitas umum
- Lihat kamar tersedia
- Baca dan tulis reviews
- Lihat kontak pemilik

## 7. IMPLEMENTASI CRUD

### CREATE
- Tambah kamar baru
- Tambah penghuni baru
- Tambah review baru
- Registrasi user baru

### READ
- Tampilkan daftar kamar
- Tampilkan data penghuni
- Tampilkan reviews
- Tampilkan statistik

### UPDATE
- Edit informasi kamar
- Update status penghuni
- Edit profile user
- Update status pembayaran

### DELETE
- Hapus data kamar
- Hapus data penghuni
- Hapus reviews
- Hapus user

## 8. STRUKTUR MODULAR

### PACKAGE MODEL
- User.java
- Kamar.java
- Penghuni.java
- Review.java

### PACKAGE DAO
- UserDAO.java
- KamarDAO.java
- PenghuniDAO.java
- ReviewDAO.java

### PACKAGE GUI
- LoginForm.java
- DashboardAdmin.java
- DashboardUser.java
- KamarPanel.java
- PenghuniPanel.java
- ReviewPanel.java

### PACKAGE UTIL
- DateUtil.java
- ValidationUtil.java
- ImageUtil.java
- DatabaseConnection.java

## 9. SISTEM AUTENTIKASI

### ROLE ADMIN
- Akses penuh ke semua fitur
- Hak akses CRUD lengkap
- Management user
- Monitoring sistem

### ROLE USER
- Akses terbatas pada fitur view
- Bisa menulis review
- Tidak bisa modifikasi data master
- Hanya lihat informasi publik

## 10. TAMPILAN GUI

### HALAMAN LOGIN
- Form input username dan password
- Validasi credentials
- Redirect berdasarkan role

### DASHBOARD ADMIN
- Menu navigasi lengkap
- Statistik cepat
- Akses cepat ke fitur CRUD
- Tabel data interaktif

### DASHBOARD USER
- Informasi kos lengkap
- Gallery kamar tersedia
- Form kontak pemilik
- Sistem review dan rating

## 11. KEUNGGULAN APLIKASI

### DARI SISI TEKNIS
- Kode terstruktur dan modular
- Mudah dikembangkan
- Database design yang normalisasi
- Error handling yang baik

### DARI SISI USER EXPERIENCE
- Interface yang intuitif
- Navigasi yang mudah
- Responsive design
- Feedback yang jelas

### DARI SISI FUNGSIONALITAS
- Fitur lengkap untuk manajemen kos
- Sistem role-based yang aman
- Laporan dan monitoring
- Sistem review yang transparan

## 12. IMPLEMENTASI OOP

### ENCAPSULATION
- Setiap class memiliki method getter dan setter
- Data hiding dengan access modifier private
- Method public untuk interaksi

### INHERITANCE
- Abstract class untuk model dasar
- Inheritance dalam class GUI
- Method overriding untuk custom behavior

### POLYMORPHISM
- Method overloading dalam DAO (untuk menyambungkan 
  mysql ke GUI
- Interface implementation
- Dynamic method dispatch

### ABSTRACTION
- Abstract class untuk koneksi database
- Interface untuk contract DAO
- Abstraction layer untuk database operation

## 13. TESTING

### TESTING YANG DILAKUKAN
- Test koneksi database
- Test validasi login
- Test CRUD operations
- Test input validation
- Test user interface

### HASIL TESTING
- Semua fitur berjalan sesuai spesifikasi
- Tidak ditemukan bug kritikal
- Performance sesuai ekspektasi
- User experience baik

## 14. KENDALA DAN SOLUSI

### KENDALA
- Integrasi database dengan GUI
- Management koneksi database
- Validasi input yang kompleks
- Layout GUI yang responsive

### SOLUSI
- Menggunakan pattern DAO 
- Connection pooling
- Regular expression untuk validasi
- Menggunakan layout manager

## 15. PENGEMBANGAN SELANJUTNYA

### FITUR YANG BISA DITAMBAH
- Sistem pembayaran online
- Notifikasi otomatis
- Mobile application
- Integration dengan payment gateway
- Sistem report yang lebih advance

## 16. KESIMPULAN

Aplikasi Manajemen Kos berhasil dikembangkan dengan menggunakan Java dan MySQL. Aplikasi ini telah memenuhi semua requirement yang diminta, termasuk implementasi GUI, database dengan 3 tabel, autentikasi, CRUD operations, dan modularitas kode. Aplikasi ini dapat digunakan untuk membantu pemilik kos dalam mengelola bisnis mereka secara lebih efisien.

##  17. LAMPIRAN

- Source code lengkap
- Dokumentasi database
- Screenshot aplikasi
- Diagram class
- Instruksi instalasi

Aplikasi Manajemen Kos adalah sistem desktop yang dikembangkan untuk membantu pemilik kos dalam mengelola operasional kosan mereka. Aplikasi ini menyediakan solusi komprehensif bagi dua jenis pengguna: admin (pemilik/pengelola kos) dan visitor (calon penghuni). 

Untuk admin, aplikasi menyediakan fitur lengkap CRUD (Create, Read, Update, Delete) untuk mengelola data kamar, penghuni, dan review. Admin dapat menambah kamar baru, mengupdate status kamar, mengelola data penghuni, serta memantau sistem review. Sedangkan untuk user biasa, aplikasi memberikan akses untuk melihat informasi kos secara lengkap termasuk fasilitas umum, kamar yang tersedia, deskripsi lengkap setiap kamar, serta kontak pemilik kos. User biasa juga dapat memberikan review dan rating berdasarkan pengalaman mereka.

Aplikasi dibangun dengan arsitektur modular yang terbagi dalam package model, DAO, GUI, dan util. Database terdiri dari empat tabel utama: users untuk autentikasi, kamars untuk data kamar, penghunis untuk data penghuni, dan reviews untuk sistem penilaian. Implementasi OOP mencakup encapsulation, inheritance, polymorphism, dan abstraction, sementara fitur keamanan meliputi sistem autentikasi berbasis role dan validasi input.

Dengan antarmuka yang user-friendly dan fungsionalitas yang lengkap, aplikasi ini tidak hanya memenuhi kebutuhan akademis tetapi juga memberikan solusi praktis bagi pengelolaan kos yang efisien dan transparan.



DAO itu apa?
DAO = Data Access Object, bagian dari pola desain Model–DAO–GUI yang kamu pakai.
Fungsi DAO:
Menghubungkan antara model Java dan database MySQL
Menyimpan semua query SQL agar GUI tidak langsung berinteraksi dengan database
Memisahkan logika data dari tampilan



