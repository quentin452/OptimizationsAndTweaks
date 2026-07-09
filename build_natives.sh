#!/usr/bin/env bash
# Thin wrapper — the real logic lives in the SHARED script that travels with matoulib_native_shared
# (this repo already path-deps that crate as a sibling clone). Edit natives.config, not this file.
set -euo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$HERE/../matoulib/matoulib_native_shared/tools/build_natives.sh" "$HERE/natives.config"
