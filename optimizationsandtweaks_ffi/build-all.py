#!/usr/bin/env python3
import os
import subprocess
import shutil
import sys

print("=== Building Rust FFI Library for All Platforms ===")

# Get the directory where this script is located
SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
os.chdir(SCRIPT_DIR)

# Destination base directory
DEST_BASE = os.path.abspath("../src/main/resources/assets/optimizationsandtweaks/natives")

# Setup osxcross PATH if available
OSXCROSS_PATH = os.path.expanduser("~/osxcross/target/bin")
if os.path.isdir(OSXCROSS_PATH):
    os.environ["PATH"] = f"{OSXCROSS_PATH}:{os.environ.get('PATH', '')}"
    print(f"✓ osxcross found at {OSXCROSS_PATH}")

def run(cmd, check=True):
    print(f"$ {' '.join(cmd)}")
    subprocess.run(cmd, check=check)

def check_target(target):
    result = subprocess.run(["rustup", "target", "list"], capture_output=True, text=True)
    if f"{target} (installed)" not in result.stdout:
        print(f"Installing target {target}...")
        run(["rustup", "target", "add", target])

def build_for_target(target, os_dir, lib_name):
    print(f"\n=== Building for {target} ===")
    run(["cargo", "build", "--release", "--target", target])

    dest_dir = os.path.join(DEST_BASE, os_dir)
    os.makedirs(dest_dir, exist_ok=True)

    # Try with "lib" prefix first (Linux/macOS convention)
    src_path = os.path.join(SCRIPT_DIR, "target", target, "release", lib_name)
    src_path_with_lib = os.path.join(SCRIPT_DIR, "target", target, "release", f"lib{lib_name}")
    
    # Check which file exists
    if os.path.isfile(src_path_with_lib):
        src_path = src_path_with_lib
    elif not os.path.isfile(src_path):
        print(f"✗ Warning: Library not found at {src_path} or {src_path_with_lib}")
        return
    
    dest_path = os.path.join(dest_dir, lib_name)
    shutil.copy2(src_path, dest_path)
    print(f"✓ Copied {lib_name} to {dest_path}")

# Linux x86_64
check_target("x86_64-unknown-linux-gnu")
build_for_target("x86_64-unknown-linux-gnu", "linux", "liboptimizationsandtweaks_ffi.so")

# Windows x86_64
check_target("x86_64-pc-windows-gnu")
if shutil.which("x86_64-w64-mingw32-gcc"):
    build_for_target("x86_64-pc-windows-gnu", "windows", "optimizationsandtweaks_ffi.dll")
else:
    print("⚠ Warning: MinGW-w64 not found. Skipping Windows build.\n  Install with: sudo apt-get install mingw-w64")

# macOS - Build both architectures and create universal binary
macos_x86_built = False
macos_arm_built = False

check_target("x86_64-apple-darwin")
if sys.platform == "darwin" or shutil.which("x86_64-apple-darwin23.5-clang"):
    print("\n=== Building for x86_64-apple-darwin ===")
    run(["cargo", "build", "--release", "--target", "x86_64-apple-darwin"])
    macos_x86_built = True
else:
    print("⚠ Warning: macOS x86_64 build requires macOS host or osxcross. Skipping.")

check_target("aarch64-apple-darwin")
if sys.platform == "darwin" or shutil.which("aarch64-apple-darwin23.5-clang"):
    print("\n=== Building for aarch64-apple-darwin ===")
    run(["cargo", "build", "--release", "--target", "aarch64-apple-darwin"])
    macos_arm_built = True
else:
    print("⚠ Warning: macOS ARM64 build requires macOS host or osxcross. Skipping.")

# Create universal binary if both architectures were built
if macos_x86_built and macos_arm_built:
    print("\n=== Creating macOS Universal Binary ===")
    dest_dir = os.path.join(DEST_BASE, "macos")
    os.makedirs(dest_dir, exist_ok=True)
    
    x86_lib = os.path.join(SCRIPT_DIR, "target", "x86_64-apple-darwin", "release", "liboptimizationsandtweaks_ffi.dylib")
    arm_lib = os.path.join(SCRIPT_DIR, "target", "aarch64-apple-darwin", "release", "liboptimizationsandtweaks_ffi.dylib")
    universal_lib = os.path.join(dest_dir, "liboptimizationsandtweaks_ffi.dylib")
    
    if shutil.which("lipo"):
        run(["lipo", "-create", "-output", universal_lib, x86_lib, arm_lib])
        print(f"✓ Created universal binary at {universal_lib}")
    else:
        print("⚠ Warning: 'lipo' not found. Copying ARM64 version only.")
        shutil.copy2(arm_lib, universal_lib)
        print(f"✓ Copied ARM64 library to {universal_lib}")
elif macos_x86_built:
    dest_dir = os.path.join(DEST_BASE, "macos")
    os.makedirs(dest_dir, exist_ok=True)
    x86_lib = os.path.join(SCRIPT_DIR, "target", "x86_64-apple-darwin", "release", "liboptimizationsandtweaks_ffi.dylib")
    dest_path = os.path.join(dest_dir, "liboptimizationsandtweaks_ffi.dylib")
    shutil.copy2(x86_lib, dest_path)
    print(f"✓ Copied x86_64 library to {dest_path}")
elif macos_arm_built:
    dest_dir = os.path.join(DEST_BASE, "macos")
    os.makedirs(dest_dir, exist_ok=True)
    arm_lib = os.path.join(SCRIPT_DIR, "target", "aarch64-apple-darwin", "release", "liboptimizationsandtweaks_ffi.dylib")
    dest_path = os.path.join(dest_dir, "liboptimizationsandtweaks_ffi.dylib")
    shutil.copy2(arm_lib, dest_path)
    print(f"✓ Copied ARM64 library to {dest_path}")

print("\n=== Build Complete ===")
print(f"Native libraries have been copied to:\n  {DEST_BASE}/linux/\n  {DEST_BASE}/windows/\n  {DEST_BASE}/macos/")
