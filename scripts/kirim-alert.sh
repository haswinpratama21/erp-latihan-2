#!/usr/bin/env bash
# Mengirim isi file alert ke routine "On call erp-latihan" lewat pemicu API.
#
#   ./scripts/kirim-alert.sh "<URL>" "<TOKEN>" alert/contoh-alert-faktur.txt
#
# URL dan token didapat dari halaman routine (Edit -> trigger API). Bisa juga lewat variabel
# lingkungan ROUTINE_URL dan ROUTINE_TOKEN supaya token tidak tersimpan di riwayat terminal.
# Hanya butuh bash dan curl (jalan di Git Bash Windows, macOS, Linux).
set -euo pipefail

URL="${1:-${ROUTINE_URL:-}}"
TOKEN="${2:-${ROUTINE_TOKEN:-}}"
FILE="${3:-alert/contoh-alert-faktur.txt}"

if [ -z "$URL" ] || [ -z "$TOKEN" ]; then
    echo "Pemakaian: $0 \"<URL>\" \"<TOKEN>\" [file-alert]" >&2
    exit 1
fi
if [ ! -f "$FILE" ]; then
    echo "File alert tidak ditemukan: $FILE" >&2
    exit 1
fi

# Baca file apa adanya (termasuk baris kosong di akhir), lalu ubah jadi string JSON.
# (tr membuang karakter kontrol selain \t \n \r \f; "x" menjaga baris kosong di akhir file)
isi="$(LC_ALL=C tr -d '\000-\010\013\016-\037' < "$FILE"; printf x)"
isi="${isi%x}"
isi="${isi//\\/\\\\}"
isi="${isi//\"/\\\"}"
isi="${isi//$'\t'/\\t}"
isi="${isi//$'\r'/\\r}"
isi="${isi//$'\f'/\\f}"
isi="${isi//$'\n'/\\n}"
body="{\"text\": \"$isi\"}"

echo "Mengirim $FILE ke routine ..."
respons="$(printf '%s' "$body" | curl -sS -X POST "$URL" \
    -H "Authorization: Bearer $TOKEN" \
    -H "anthropic-beta: experimental-cc-routine-2026-04-01" \
    -H "anthropic-version: 2023-06-01" \
    -H "Content-Type: application/json" \
    --data-binary @- \
    -w $'\n%{http_code}')"

kode="${respons##*$'\n'}"
isi_respons="${respons%$'\n'*}"
echo "HTTP $kode"
echo "$isi_respons"

case "$kode" in
    2*)
        sesi="$(printf '%s' "$isi_respons" | grep -o '"claude_code_session_url" *: *"[^"]*"' | sed 's/.*: *"//; s/"$//' || true)"
        if [ -n "$sesi" ]; then
            echo
            echo "Buka sesi agent: $sesi"
        fi
        ;;
    401|403)
        echo "Token ditolak. Generate token baru di halaman routine, lalu ulangi." >&2
        exit 1
        ;;
    *)
        echo "Gagal mengirim alert." >&2
        exit 1
        ;;
esac
