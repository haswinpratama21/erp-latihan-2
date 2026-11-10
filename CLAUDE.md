# erp-latihan

Backend ERP latihan PT Nusantara Rasa (perusahaan fiktif): customer, produk, sales order, faktur. Java 17, Spring Boot 3.x, Maven, database H2 in-memory yang diisi ulang setiap aplikasi jalan.

## Perintah

- Test: `mvn -q test` (atau `./mvnw -q test` kalau Maven belum terpasang)
- Jalankan: `mvn spring-boot:run`, lalu buka http://localhost:8080/swagger-ui.html
- Konsol database: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:erp`, user `sa`, password kosong)

## Struktur

- `src/main/java/id/nusantararasa/erp/`
  - `customer/`, `produk/`: data master, hanya baca
  - `penjualan/`: sales order. Aturan diskon dan limit kredit ada di `AturanPenjualan.java`
  - `faktur/`: penerbitan faktur dari SO yang `SIAP_DIFAKTURKAN`
  - `umum/`: exception, penanganan error, penomoran dokumen
- `src/main/resources/schema.sql`, `data.sql`: skema dan data awal
- `docs/aturan-bisnis.md`: aturan bisnis lengkap. Baca sebelum mengubah logika penjualan atau faktur
- `docs/runbook-faktur.md`: langkah menangani error di endpoint faktur
- `alert/`: contoh alert dari monitoring

## Aturan kode

- Uang dan persen memakai `BigDecimal`, dibulatkan 2 desimal `HALF_UP`. Jangan memakai `double`.
- Pelanggaran aturan bisnis dilempar sebagai `AturanBisnisException` (HTTP 422), data tidak ada sebagai `DataTidakDitemukanException` (HTTP 404).
- Controller hanya memanggil service dan mengembalikan DTO (record), bukan entity.
- Nama kelas, method, dan pesan error dalam bahasa Indonesia, mengikuti kode yang sudah ada.
- Setiap perbaikan bug disertai test yang gagal sebelum perbaikan. Setiap fitur baru disertai test.
- Jangan mengubah `data.sql` untuk membuat test lulus.
- Commit kecil dengan pesan yang menjelaskan alasan perubahan.
