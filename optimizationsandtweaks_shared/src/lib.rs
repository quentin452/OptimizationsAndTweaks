use jni::sys::jint;
use jni::sys::JNI_VERSION_1_6;
use std::os::raw::c_void;

// Enable the leaktracer allocator in debug builds to trace allocations made by this crate. A
// #[global_allocator] must be defined in the final cdylib (not the linked rlib), so it stays here.
#[cfg(debug_assertions)]
#[global_allocator]
static ALLOCATOR: leaktracer::LeaktracerAllocator = leaktracer::LeaktracerAllocator::init();

// The FFI panic guard, native logger and off-heap helper now live in `matoulib_native_shared`
// (linked rlib, hub docs/23). Re-export the same symbols so the sub-crates
// (optimizationsandtweaks_pathfinding / _blocksupdates / _profiler) that
// `use optimizationsandtweaks_shared::{ffi_panic_guard, log_native_line, ...}` compile UNCHANGED.
pub use matoulib_native_shared::{
    ffi_panic_guard, ffi_panic_guard_void, init_log_file, init_panic_logging, is_panic_guard_enabled,
    log_native_line, off_heap, set_native_context, set_panic_guard_enabled,
};

// === Package-qualified JNI exports — stay here (symbol names encode OaT's Java package) ===

#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_setPanicGuardEnabled(
    _env: jni::JNIEnv,
    _class: jni::objects::JClass,
    enabled: jni::sys::jboolean,
) {
    set_panic_guard_enabled(enabled != 0);
}

#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_isPanicGuardEnabled(
    _env: jni::JNIEnv,
    _class: jni::objects::JClass,
) -> jni::sys::jboolean {
    if is_panic_guard_enabled() {
        1
    } else {
        0
    }
}

#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_initLogFile(
    _env: jni::JNIEnv,
    _class: jni::objects::JClass,
) {
    init_log_file();
}

// Initialize logging + panic hook when the JNI library loads; dump leaktracer stats on unload.
#[no_mangle]
pub unsafe extern "system" fn JNI_OnLoad(_vm: *mut jni::sys::JavaVM, _reserved: *mut c_void) -> jint {
    matoulib_native_shared::on_load(
        "OptimizationsAndTweaks",
        "logs/optimizationsandtweaks/native.log",
    );

    // Initialize the leaktracer symbol table for this crate in debug builds only (OaT-specific).
    #[cfg(debug_assertions)]
    {
        leaktracer::init_symbol_table(&["optimizationsandtweaks_pathfinding"]);
        log_native_line("Leaktracer initialized (debug build)");
    }

    JNI_VERSION_1_6
}

#[no_mangle]
pub unsafe extern "system" fn JNI_OnUnload(_vm: *mut jni::sys::JavaVM, _reserved: *mut c_void) {
    matoulib_native_shared::on_unload();

    // Dump leaktracer stats when unloading (debug builds only).
    #[cfg(debug_assertions)]
    {
        match leaktracer::with_symbol_table(|table| {
            log_native_line("Leaktracer stats begin");
            for (name, symbol) in table.iter() {
                log_native_line(format!(
                    "symbol='{}' allocated={} count={}",
                    name,
                    symbol.allocated(),
                    symbol.count()
                ));
            }
            log_native_line(format!("Total allocated bytes: {}", ALLOCATOR.allocated()));
            log_native_line("Leaktracer stats end");
        }) {
            Ok(_) => {}
            Err(_e) => {
                log_native_line("Leaktracer: failed to access symbol table");
            }
        }
    }
}
