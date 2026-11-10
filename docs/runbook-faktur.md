# Runbook: Error di Endpoint Faktur

Untuk siapa pun yang menerima alert `error-rate-api-faktur`.

## 1. Pastikan dampaknya

- Endpoint: `POST /api/faktur/{soId}`. Kalau gagal, faktur untuk SO itu tidak terbit dan sales tidak bisa mengirim barang.
- Catat soId yang gagal dari alert. Cek apakah semua request gagal atau hanya SO tertentu.

## 2. Reproduksi di lokal

```
./mvnw spring-boot:run          # atau: mvn spring-boot:run
curl -i http://localhost:8080/api/sales-orders/<soId>
curl -i -X POST http://localhost:8080/api/faktur/<soId>
```

Data lokal memakai data awal di `src/main/resources/data.sql`, jadi soId yang sama biasanya bisa dicoba langsung. Isi tabel juga bisa dilihat di http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:erp`, user `sa`).

## 3. Perbaiki

- Tulis unit test yang gagal untuk kasus tersebut dulu, baru perbaiki.
- Perubahan sekecil mungkin. Jangan mengubah data awal untuk "menyembunyikan" error.
- Jalankan seluruh test: `mvn -q test`.

## 4. Laporkan

Buat pull request yang berisi ringkasan alert, akar masalah, perubahan, dan hasil test. Catat ringkasannya di halaman bug di Notion.
