#!/usr/bin/env bash
# build_natives.sh — rebuild this repo's Rust JNI cdylib natives and copy them into the
# assets/<modid>/natives/<os>/ tree. Cross-OS rebuild that is otherwise a manual chore.
#
#   * linux         : native `cargo build --release`            -> lib<crate>.so
#   * macos         : cargo-zigbuild x86_64 + aarch64, lipo'd   -> lib<crate>.dylib (universal2)
#   * windows       : OPT-IN only (INCLUDE_WINDOWS=1)           -> <crate>.dll  (see WINDOWS note)
#
# macOS is cross-compiled from Linux via cargo-zigbuild: zig is the cross-linker and bundles a
# macOS libc, so these pure-compute JNI libs (link only libSystem, no Apple frameworks) build
# WITHOUT Xcode / the macOS SDK. The `xcrun ... failed` line during the build is a WARNING, not
# an error.
#
# WINDOWS: the currently-shipped .dll are msvc-ABI (static CRT: import only KERNEL32/ntdll). zig
# produces gnu-ABI DLLs (dynamic UCRT: import api-ms-win-crt-*, bcrypt, userenv, ws2_32). The JNI
# C ABI is identical and a gnu DLL loads fine on machines with UCRT, but it is a different CRT
# dependency profile, so we do NOT silently overwrite the msvc DLLs. Run with INCLUDE_WINDOWS=1
# to build+copy gnu DLLs anyway (deliberate ABI switch); default leaves Windows to an msvc build.
#
# Idempotent: re-running overwrites the same asset paths with fresh binaries. Echoes what it made.
#
# Prereqs (one-time): rustup targets x86_64-apple-darwin + aarch64-apple-darwin; `cargo install
# cargo-zigbuild`; a zig toolchain on PATH (auto-detected from ~/.local/zig-dl/zig-*/ if not).
# For windows also: rustup target add x86_64-pc-windows-gnu.
set -euo pipefail

# ---- per-repo config -------------------------------------------------------
MODID="optimizationsandtweaks"
# cdylib crates that ship a native (rlib-only crates are NOT listed):
CRATES=(
  optimizationsandtweaks_shared
  optimizationsandtweaks_pathfinding
  optimizationsandtweaks_blocksupdates
)
# rlib-only, intentionally NOT shipped: optimizationsandtweaks_profiler
# ---------------------------------------------------------------------------

INCLUDE_WINDOWS="${INCLUDE_WINDOWS:-0}"
MAC_X86="x86_64-apple-darwin"
MAC_ARM="aarch64-apple-darwin"
WIN="x86_64-pc-windows-gnu"

REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ASSETS="$REPO/src/main/resources/assets/$MODID/natives"

# locate zig for cargo-zigbuild if not already on PATH
if ! command -v zig >/dev/null 2>&1; then
  ZIGDIR="$(ls -d "$HOME"/.local/zig-dl/zig-*/ 2>/dev/null | sort -V | tail -1 || true)"
  if [ -n "${ZIGDIR:-}" ]; then export PATH="${ZIGDIR%/}:$PATH"; fi
fi
command -v zig >/dev/null 2>&1 || { echo "FATAL: zig not found on PATH (needed for macOS/windows cross)"; exit 1; }
LIPO="$(command -v llvm-lipo || command -v lipo)" || { echo "FATAL: no lipo/llvm-lipo"; exit 1; }

echo "== build_natives: $MODID  (zig $(zig version), lipo=$LIPO) =="
mkdir -p "$ASSETS/linux" "$ASSETS/macos" "$ASSETS/windows"
MADE=()

for crate in "${CRATES[@]}"; do
  echo "-- $crate --"
  cd "$REPO/$crate"

  # linux (native host build)
  cargo build --release
  cp -f "target/release/lib${crate}.so" "$ASSETS/linux/lib${crate}.so"
  MADE+=("linux/lib${crate}.so")

  # macOS universal2 (x86_64 + arm64)
  cargo zigbuild --release --target "$MAC_X86"
  cargo zigbuild --release --target "$MAC_ARM"
  "$LIPO" -create \
    "target/$MAC_X86/release/lib${crate}.dylib" \
    "target/$MAC_ARM/release/lib${crate}.dylib" \
    -output "$ASSETS/macos/lib${crate}.dylib"
  MADE+=("macos/lib${crate}.dylib")

  # windows (opt-in; gnu-ABI, see WINDOWS note at top)
  if [ "$INCLUDE_WINDOWS" = "1" ]; then
    cargo zigbuild --release --target "$WIN"
    cp -f "target/$WIN/release/${crate}.dll" "$ASSETS/windows/${crate}.dll"
    MADE+=("windows/${crate}.dll [gnu-ABI]")
  fi
done

echo "== produced =="
for m in "${MADE[@]}"; do echo "  $ASSETS/$m"; done
echo "== verify: file \"$ASSETS/macos/\"*.dylib =="
[ "$INCLUDE_WINDOWS" = "1" ] || echo "== windows: SKIPPED (msvc-ABI shipped; set INCLUDE_WINDOWS=1 to build gnu DLLs) =="
