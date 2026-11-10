#!/usr/bin/env bash
# Melindungi branch main: semua perubahan wajib lewat pull request.
# Dipakai di Langkah 5 Sesi 1.3 (batas akses: agent tidak bisa langsung menulis ke main).
#
#   GH_OWNER=<username> ./scripts/lindungi-main.sh
#   # atau untuk repo lain: ./scripts/lindungi-main.sh <owner>/<repo>
set -euo pipefail

REPO="${1:-${GH_OWNER:?Set GH_OWNER atau beri argumen <owner>/<repo>}/erp-latihan}"

# Body ruleset dikirim sebagai JSON utuh. Rule pull_request wajib punya objek
# parameters (API rulesets menolak dengan 422 kalau tidak ada). Jumlah approval 0:
# PR tetap wajib, tapi pemilik repo yang bekerja sendiri masih bisa merge PR-nya.
# Tanpa bypass_actors, jadi admin pun tidak bisa push langsung ke main.
echo "Memasang ruleset 'lindungi-main' di $REPO ..."
gh api -X POST "repos/$REPO/rulesets" --input - >/dev/null <<'JSON'
{
  "name": "lindungi-main",
  "target": "branch",
  "enforcement": "active",
  "conditions": {
    "ref_name": { "include": ["~DEFAULT_BRANCH"], "exclude": [] }
  },
  "rules": [
    {
      "type": "pull_request",
      "parameters": {
        "required_approving_review_count": 0,
        "dismiss_stale_reviews_on_push": false,
        "require_code_owner_review": false,
        "require_last_push_approval": false,
        "required_review_thread_resolution": false,
        "allowed_merge_methods": ["merge", "squash", "rebase"]
      }
    },
    { "type": "deletion" },
    { "type": "non_fast_forward" }
  ]
}
JSON

echo "Selesai. Perubahan ke main sekarang wajib lewat pull request."
