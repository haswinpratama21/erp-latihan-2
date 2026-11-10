# erp-latihan

Project contoh untuk workshop Agentic AI: backend ERP sederhana (customer, sales order, faktur) milik perusahaan fiktif PT Nusantara Rasa. Semua data dan kredensial di repo ini **dummy**.

## Mulai

Butuh JDK 17 atau lebih baru dan Maven 3.9+ (atau pakai `./mvnw`, di Windows PowerShell `.\mvnw.cmd`, yang mengunduh Maven sendiri).

```
mvn -q test            # semua test harus lulus
mvn spring-boot:run    # aplikasi di http://localhost:8080
```

Dokumentasi API: http://localhost:8080/swagger-ui.html

## Endpoint

| Method | Path | Isi |
|---|---|---|
| GET | `/api/customers`, `/api/customers/{kode}` | Daftar dan detail customer (filter `?segmen=TOKO`) |
| GET | `/api/produk` | Daftar produk aktif |
| POST | `/api/sales-orders` | Buat sales order |
| GET | `/api/sales-orders`, `/api/sales-orders/{id}` | Daftar dan detail SO (filter `?status=`) |
| POST | `/api/sales-orders/{id}/setujui` | Setujui SO yang menunggu persetujuan atau ditahan |
| POST | `/api/sales-orders/impor` | Impor SO terbuka dari CSV sistem lama |
| POST | `/api/faktur/{soId}` | Terbitkan faktur |
| GET | `/api/faktur` | Daftar faktur |

Contoh membuat SO:

```
curl -X POST http://localhost:8080/api/sales-orders -H "Content-Type: application/json" \
  -d '{"kodeCustomer": "CUS-022", "diskonTambahan": 1.0, "item": [{"kodeProduk": "BSK-001", "qty": 100}]}'
```

Aturan bisnisnya ada di [docs/aturan-bisnis.md](docs/aturan-bisnis.md).

## Folder lain

- `alert/`: contoh alert monitoring, dipakai latihan agent on call
- `scripts/kirim-alert.sh` (Git Bash, macOS, Linux) dan `scripts/kirim-alert.ps1` (PowerShell): mengirim alert ke routine

## Untuk peserta workshop

Repo ini adalah **template**. Klik **Use this template** → **Create a new repository**, beri nama `erp-latihan`, lalu pilih **Private**. Yang tersalin cukup branch `main`; branch solusi tetap bisa dilihat di repo ini.

Bandingkan hasil agent kelompokmu dengan branch solusi. Di GitHub, buka **Compare** (`main...nama-branch`) untuk melihat perubahannya saja.

| Branch | Isi |
|---|---|
| `main` | Titik awal semua latihan |
| `solusi/izin` | `.claude/settings.json` dari mini praktik permission (Sesi 1.1) |
| `solusi/bug-faktur-npe` | Perbaikan bug faktur dari alur on call (Sesi 1.3 Langkah 4), dengan test regresi |
| `solusi/f0-cari-produk` | F0 · Cari produk berdasarkan nama (contoh mentor, Demo D) |
| `solusi/f1-export-excel` | F1 · Export customer ke Excel |
| `solusi/f2-filter-kota` | F2 · Filter customer per kota |
| `solusi/f3-pelunasan-faktur` | F3 · Pelunasan faktur |
| `solusi/f4-jatuh-tempo` | F4 · Faktur jatuh tempo |
| `solusi/f5-piutang-customer` | F5 · Piutang customer |
| `demo-upgrade` | Titik awal Demo C: Spring Boot 3.3 dengan dependency usang |
| `solusi/demo-upgrade` | Hasil akhir Demo C: Spring Boot 3.5, plus `docs/laporan-upgrade.md` |

Setiap branch solusi lulus `mvn -q test`. Solusi ini hanya salah satu cara. Hasil agent kelompokmu boleh berbeda, asal acceptance criteria di PRD terpenuhi dan test-nya lulus.

## Catatan keamanan

Repo ini sengaja memuat beberapa contoh berbahaya untuk latihan keamanan agent. Semua nilainya dummy, dan host-nya memakai domain `.invalid` yang tidak bisa dihubungi. Jangan meniru pola ini di project sungguhan: di project sungguhan, `.env` wajib masuk `.gitignore`, dan skrip dari sumber yang tidak dikenal jangan dijalankan.
