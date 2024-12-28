# README: Aplikasi Pemesanan Tiket Bus

## Deskripsi
Aplikasi Pemesanan Tiket Bus adalah sebuah program Java berbasis GUI (Graphical User Interface) yang dirancang untuk mempermudah proses pemesanan tiket bus. Aplikasi ini memiliki fitur seperti memilih tujuan, memilih kelas bus, memilih tanggal keberangkatan, memilih kursi, dan menyimpan data pemesanan ke dalam file Word.

---

## Fitur Utama

1. **Memilih Tujuan dan Kelas Bus**
   - Pengguna dapat memilih tujuan dari daftar tujuan yang tersedia.
   - Kelas bus tersedia dalam dua jenis: Eksekutif dan Sleeper.

2. **Pemilihan Tanggal Keberangkatan**
   - Menggunakan komponen `JDateChooser` untuk memilih tanggal keberangkatan.

3. **Pemilihan Kursi**
   - Sistem kursi tergantung pada kelas bus yang dipilih:
     - **Eksekutif**: Kursi dengan tata letak lebih luas.
     - **Sleeper**: Tata letak kursi yang lebih sederhana.
   - Kursi yang sudah dipesan tidak dapat dipilih kembali.

4. **Input Data Diri**
   - Pengguna harus memasukkan nama dan NIK untuk melengkapi pemesanan.

5. **Rangkuman Pemesanan**
   - Tampilkan ringkasan pemesanan, termasuk tujuan, tanggal, kelas, kursi, total harga, nama, dan NIK.

6. **Fungsi CRUD (Create, Read, Update, Delete)**
   - **Tambah Pemesanan Baru**: Pengguna dapat menambah pemesanan baru.
   - **Edit Pemesanan**: Pengguna dapat mengedit data pemesanan yang sudah ada.
   - **Hapus Pemesanan**: Menghapus pemesanan berdasarkan nama dan NIK.

7. **Penyimpanan ke File Word**
   - Pemesanan dapat disimpan ke file Word menggunakan library Apache POI, termasuk dengan gambar ilustrasi bus berdasarkan kelas yang dipilih.

---

## Teknologi yang Digunakan

- **Java Swing**: Untuk membangun antarmuka pengguna.
- **Toedter Calendar**: Untuk pemilihan tanggal.
- **Apache POI**: Untuk menyimpan data ke file Word.

---

## Struktur Kode

### Kelas Utama: `Main`
Panggil aplikasi menggunakan `SwingUtilities` untuk menjalankan GUI.

### Kelas Pendukung: `BookingApp`
- **Variabel Penting**:
  - `fromTo`: Tujuan perjalanan.
  - `busClass`: Kelas bus (Eksekutif/Sleeper).
  - `date`: Tanggal keberangkatan.
  - `selectedSeats`: Daftar kursi yang dipilih.
  - `name` dan `nik`: Data diri pengguna.
  - `bookedSeatsByDate`: HashMap untuk melacak kursi yang sudah dipesan berdasarkan tanggal.

- **Metode Utama**:
  - `showScreen1()`: Halaman untuk memilih tujuan, kelas, dan tanggal.
  - `showScreen2()`: Halaman untuk memilih kursi.
  - `showScreen3()`: Halaman untuk menginput data diri.
  - `showScreen4()`: Halaman ringkasan pemesanan.
  - `saveAllBookingsToWord()`: Simpan pemesanan ke file Word.
  - `showEditScreen()`: Edit pemesanan.
  - `showDeleteScreen()`: Hapus pemesanan.

---

## Cara Menjalankan Program

1. **Persiapan**
   - Pastikan Java Development Kit (JDK) sudah terinstal.
   - Tambahkan library berikut ke classpath:
     - `toedter-calendar.jar`
     - `apache-poi.jar` dan dependensi terkait.

2. **Menjalankan Program**
   - Kompilasi dan jalankan file `Main.java`.

3. **Fungsi Utama Program**
   - Pilih tujuan, tanggal, dan kelas bus.
   - Pilih kursi sesuai preferensi.
   - Masukkan data diri.
   - Tinjau ringkasan pemesanan dan simpan ke file Word jika diperlukan.

---

## Dependensi

- `JDateChooser` dari library Toedter Calendar.
- Apache POI untuk memanipulasi file Word:
  - `org.apache.poi.xwpf.usermodel.*`
  - `org.apache.poi.openxml4j.exceptions.InvalidFormatException`
  - `org.apache.poi.util.Units`

---

## Catatan Penggunaan

1. **Format Gambar**:
   - Pastikan gambar bus tersedia pada direktori `gambar/` dengan nama:
     - `bus eksekutif.jpg`
     - `bus sleeper.jpg`

2. **Validasi Input**:
   - Pastikan semua input (tanggal, nama, NIK) diisi dengan benar sebelum melanjutkan.

3. **File Output**:
   - Data pemesanan akan disimpan dalam file `booking_summary.docx` di direktori kerja saat ini.

---

## Pengembangan Lebih Lanjut

1. Integrasi dengan database untuk menyimpan data pemesanan secara permanen.
2. Penambahan fitur pembayaran online.
3. Penambahan validasi tanggal agar tidak memilih tanggal yang sudah lewat.
4. Pengembangan antarmuka yang lebih responsif dengan JavaFX.

---

## Penulis
Program ini dikembangkan sebagai solusi untuk mempermudah proses pemesanan tiket bus dengan antarmuka pengguna yang ramah.

