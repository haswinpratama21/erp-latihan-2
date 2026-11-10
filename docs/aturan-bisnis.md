# Aturan Bisnis Penjualan

Ringkasan aturan yang ditegakkan oleh kode di `penjualan/AturanPenjualan.java`. Sumbernya kebijakan internal PT Nusantara Rasa (perusahaan fiktif): KBJ-PJL-002 Kebijakan Harga dan Diskon, KBJ-KEU-004 Kebijakan Kredit dan Penagihan.

## Segmen customer

| Segmen | Diskon standar | Termin | Limit kredit bawaan |
|---|---|---|---|
| Distributor | 12% | 45 hari | Rp 900.000.000 |
| Retail Modern | 6% | 30 hari | Rp 300.000.000 |
| Toko | 2% | 14 hari | Rp 40.000.000 |

Limit kredit bisa berbeda per customer (kolom `limit_kredit`).

## Diskon tambahan

Diskon tambahan diajukan sales di atas diskon standar, dalam persen.

| Diskon tambahan | Keputusan |
|---|---|
| 0 sampai 1,5% | Wewenang Sales Supervisor, SO langsung `SIAP_DIFAKTURKAN` |
| di atas 1,5% sampai 3% | `MENUNGGU_PERSETUJUAN` Manajer Penjualan |
| di atas 3% | Ditolak sistem. Wajib persetujuan tertulis Direktur Operasional di luar sistem |
| negatif | Ditolak |

Kalau diskon tambahan tidak diisi saat membuat SO, nilainya 0.

## Limit kredit

Piutang customer = total faktur `BELUM_LUNAS`. Kalau piutang ditambah total SO baru melebihi limit kredit, SO berstatus `DITAHAN` sampai disetujui Divisi Finance. Pemeriksaan limit kredit didahulukan dari persetujuan diskon.

## Alur status sales order

```
SIAP_DIFAKTURKAN ──(POST /api/faktur/{soId})──▶ DIFAKTURKAN
MENUNGGU_PERSETUJUAN ─┐
DITAHAN ──────────────┴─(POST /api/sales-orders/{id}/setujui)──▶ SIAP_DIFAKTURKAN
```

## SO hasil impor

SO yang masih terbuka di sistem lama dipindahkan lewat `POST /api/sales-orders/impor` (CSV). Persetujuannya sudah selesai di sistem lama, jadi statusnya langsung `SIAP_DIFAKTURKAN` dan `sumber` = `IMPOR`. Sistem lama tidak mengenal diskon tambahan.

## Faktur

`POST /api/faktur/{soId}` menerbitkan faktur. Total dihitung ulang dari baris SO: harga × qty × (100 − diskon standar − diskon tambahan) / 100, dibulatkan ke 2 desimal. Jatuh tempo = tanggal faktur + termin customer. Satu SO hanya bisa difakturkan sekali.
