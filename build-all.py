#!/usr/bin/env python3
import os
import subprocess
import shutil
import sys
from pathlib import Path

print("=== Building Rust FFI Libraries for All Platforms ===")

# Get the directory where this script is located
SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
os.chdir(SCRIPT_DIR)

# Projects to build: (relative path, crate name, base lib name without prefixes)
CRATES = [
    ("optimizationsandtweaks_pathfinding", "optimizationsandtweaks_pathfinding"),
    ("optimizationsandtweaks_blocksupdates", "optimizationsandtweaks_blocksupdates"),
    ("optimizationsandtweaks_shared", "optimizationsandtweaks_shared"),
]

# Destination base directory (relative to repo root, where this script sits)
DEST_BASE = os.path.abspath("src/main/resources/assets/optimizationsandtweaks/natives")

# Setup osxcross PATH if available
OSXCROSS_PATH = os.path.expanduser("~/osxcross/target/bin")
if os.path.isdir(OSXCROSS_PATH):
    os.environ["PATH"] = f"{OSXCROSS_PATH}:{os.environ.get('PATH', '')}"
    print(f"\u2713 osxcross found at {OSXCROSS_PATH}")


def run(cmd, check=True, cwd=None):
    print(f"$ {' '.join(cmd)}")
    subprocess.run(cmd, check=check, cwd=cwd)


def check_target(target):
    result = subprocess.run(["rustup", "target", "list"], capture_output=True, text=True)
    if f"{target} (installed)" not in result.stdout:
        print(f"Installing target {target}...")
        run(["rustup", "target", "add", target])


def ensure_dirs():
    for os_dir in ("linux", "windows", "macos"):
        d = os.path.join(DEST_BASE, os_dir)
        os.makedirs(d, exist_ok=True)


def build_crate_for_target(crate_dir, crate_name, target, os_dir, lib_base):
    print(f"\n=== Building {crate_name} for {target} ===")
    run(["cargo", "build", "--release", "--target", target], cwd=crate_dir)

    dest_dir = os.path.join(DEST_BASE, os_dir)
    os.makedirs(dest_dir, exist_ok=True)

    # Expected filenames per platform
    if os_dir == "windows":
        lib_name = f"{lib_base}.dll"
        src_path = os.path.join(crate_dir, "target", target, "release", lib_name)
    elif os_dir == "macos":
        lib_name = f"lib{lib_base}.dylib"
        src_path = os.path.join(crate_dir, "target", target, "release", lib_name)
    else:  # linux
        lib_name = f"lib{lib_base}.so"
        src_path = os.path.join(crate_dir, "target", target, "release", lib_name)

    if not os.path.isfile(src_path):
        print(f"\u2717 Warning: Library not found at {src_path}")
        return None

    dest_path = os.path.join(dest_dir, lib_name)
    shutil.copy2(src_path, dest_path)
    print(f"\u2713 Copied {lib_name} to {dest_path}")
    return dest_path


# Prepare
ensure_dirs()

# Linux x86_64
check_target("x86_64-unknown-linux-gnu")
for crate_rel, crate_name in CRATES:
    crate_dir = os.path.join(SCRIPT_DIR, crate_rel)
    build_crate_for_target(crate_dir, crate_name, "x86_64-unknown-linux-gnu", "linux", crate_name)

# Windows x86_64
check_target("x86_64-pc-windows-gnu")
if shutil.which("x86_64-w64-mingw32-gcc"):
    for crate_rel, crate_name in CRATES:
        crate_dir = os.path.join(SCRIPT_DIR, crate_rel)
        build_crate_for_target(crate_dir, crate_name, "x86_64-pc-windows-gnu", "windows", crate_name)
else:
    print("\u26a0 Warning: MinGW-w64 not found. Skipping Windows build.\n  Install with: sudo apt-get install mingw-w64")

# macOS - Build both architectures and create universal binaries per crate
macos_x86_ok = shutil.which("x86_64-apple-darwin23.5-clang") is not None or sys.platform == "darwin"
macos_arm_ok = shutil.which("aarch64-apple-darwin23.5-clang") is not None or sys.platform == "darwin"

if macos_x86_ok:
    check_target("x86_64-apple-darwin")
if macos_arm_ok:
    check_target("aarch64-apple-darwin")

for crate_rel, crate_name in CRATES:
    crate_dir = os.path.join(SCRIPT_DIR, crate_rel)
    x86_lib = None
    arm_lib = None

    if macos_x86_ok:
        run(["cargo", "build", "--release", "--target", "x86_64-apple-darwin"], cwd=crate_dir)
        x86_lib = os.path.join(crate_dir, "target", "x86_64-apple-darwin", "release", f"lib{crate_name}.dylib")
        if not os.path.isfile(x86_lib):
            print(f"\u26a0 Warning: macOS x86_64 lib missing for {crate_name}: {x86_lib}")
            x86_lib = None

    if macos_arm_ok:
        run(["cargo", "build", "--release", "--target", "aarch64-apple-darwin"], cwd=crate_dir)
        arm_lib = os.path.join(crate_dir, "target", "aarch64-apple-darwin", "release", f"lib{crate_name}.dylib")
        if not os.path.isfile(arm_lib):
            print(f"\u26a0 Warning: macOS ARM64 lib missing for {crate_name}: {arm_lib}")
            arm_lib = None

    dest_dir = os.path.join(DEST_BASE, "macos")
    os.makedirs(dest_dir, exist_ok=True)
    universal_lib = os.path.join(dest_dir, f"lib{crate_name}.dylib")

    if x86_lib and arm_lib:
        print(f"\n=== Creating macOS Universal Binary for {crate_name} ===")
        if shutil.which("lipo"):
            run(["lipo", "-create", "-output", universal_lib, x86_lib, arm_lib])
            print(f"\u2713 Created universal binary at {universal_lib}")
        else:
            print("\u26a0 Warning: 'lipo' not found. Copying ARM64 version only.")
            shutil.copy2(arm_lib, universal_lib)
            print(f"\u2713 Copied ARM64 library to {universal_lib}")
    elif x86_lib:
        shutil.copy2(x86_lib, universal_lib)
        print(f"\u2713 Copied x86_64 library to {universal_lib}")
    elif arm_lib:
        shutil.copy2(arm_lib, universal_lib)
        print(f"\u2713 Copied ARM64 library to {universal_lib}")

print("\n=== Build Complete ===")
print(f"Native libraries have been copied to:\n  {DEST_BASE}/linux/\n  {DEST_BASE}/windows/\n  {DEST_BASE}/macos/")
