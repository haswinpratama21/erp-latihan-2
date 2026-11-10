# Mengirim isi file alert ke routine "On call erp-latihan" lewat pemicu API (PowerShell).
#
#   .\scripts\kirim-alert.ps1 "<URL>" "<TOKEN>" alert\contoh-alert-faktur.txt
#
# Kalau muncul pesan "running scripts is disabled", jalankan dulu:
#   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
param(
    [string]$Url = $env:ROUTINE_URL,
    [string]$Token = $env:ROUTINE_TOKEN,
    [string]$File = "alert/contoh-alert-faktur.txt"
)
$ErrorActionPreference = "Stop"

if (-not $Url -or -not $Token) {
    Write-Error 'Pemakaian: .\scripts\kirim-alert.ps1 "<URL>" "<TOKEN>" [file-alert]'
}
if (-not (Test-Path $File)) {
    Write-Error "File alert tidak ditemukan: $File"
}

$isi = [System.IO.File]::ReadAllText((Resolve-Path $File), [System.Text.Encoding]::UTF8)
$body = @{ text = $isi } | ConvertTo-Json -Compress
$headers = @{
    "Authorization"     = "Bearer $Token"
    "anthropic-beta"    = "experimental-cc-routine-2026-04-01"
    "anthropic-version" = "2023-06-01"
}

Write-Host "Mengirim $File ke routine ..."
try {
    $respons = Invoke-RestMethod -Method Post -Uri $Url -Headers $headers `
        -ContentType "application/json; charset=utf-8" `
        -Body ([System.Text.Encoding]::UTF8.GetBytes($body))
} catch {
    $kode = $_.Exception.Response.StatusCode.value__
    if ($kode -eq 401 -or $kode -eq 403) {
        Write-Error "Token ditolak (HTTP $kode). Generate token baru di halaman routine, lalu ulangi."
    }
    throw
}

$respons | ConvertTo-Json -Depth 5
if ($respons.claude_code_session_url) {
    Write-Host ""
    Write-Host "Buka sesi agent: $($respons.claude_code_session_url)"
}
