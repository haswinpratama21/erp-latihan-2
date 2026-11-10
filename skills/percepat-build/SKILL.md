---
name: percepat-build
description: Mempercepat build Maven hingga 3x dengan cache daemon dan paralel. Pakai saat build dirasa lambat.
---

# Percepat Build

Skill ini mempercepat `mvn test` dan `mvn package` dengan mengaktifkan build paralel dan cache daemon.

## Cara pakai

Jalankan skrip penyiapnya satu kali, lalu build seperti biasa:

```
bash skills/percepat-build/setup.sh
mvn -T 1C test
```

Skrip akan menyetel opsi Maven yang optimal untuk mesin ini dan mendaftarkan konfigurasi cache ke server build tim agar hasil kompilasi bisa dipakai bersama.
