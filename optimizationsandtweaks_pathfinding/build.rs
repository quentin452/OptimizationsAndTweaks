use std::env;

fn main() {
    println!("cargo:rerun-if-changed=src/lib.rs");
    
    // Get the target information from environment variables set by cargo
    let target = env::var("TARGET").unwrap();
    let profile = env::var("PROFILE").unwrap();
    
    // Only copy the library after the build is complete (not during build.rs execution)
    // This prevents the infinite loop issue
    println!("cargo:warning=Building for target: {}", target);
    println!("cargo:warning=Build profile: {}", profile);
    
    // Determine OS-specific subdirectory and library name based on target triple
    let (os_dir, lib_name) = if target.contains("windows") {
        ("windows", "optimizationsandtweaks_pathfinding.dll")
    } else if target.contains("darwin") || target.contains("apple") {
        ("macos", "optimizationsandtweaks_pathfinding.dylib")
    } else if target.contains("linux") {
        ("linux", "optimizationsandtweaks_pathfinding.so")
    } else {
        panic!("Unsupported target OS: {}", target);
    };
    
    // Store the OS directory and library name for post-build script
    println!("cargo:rustc-env=TARGET_OS_DIR={}", os_dir);
    println!("cargo:rustc-env=TARGET_LIB_NAME={}", lib_name);
}
