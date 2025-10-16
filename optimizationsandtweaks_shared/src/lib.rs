use std::backtrace::Backtrace;
use std::env;
use std::fs::{create_dir_all, OpenOptions};
use std::io::Write;
use std::path::Path;
use std::sync::{Mutex, Once};
use std::time::{SystemTime, UNIX_EPOCH};
use jni::sys::JNI_VERSION_1_6;
use std::os::raw::c_void;
use jni::sys::{jint};

// Enable leaktracer allocator in debug builds to trace allocations made by this crate
#[cfg(debug_assertions)]
#[global_allocator]
static ALLOCATOR: leaktracer::LeaktracerAllocator = leaktracer::LeaktracerAllocator::init();

// Global init guard for installing a robust panic hook exactly once
static INIT_PANIC: Once = Once::new();

// Path for native logging
const DEFAULT_LOG_FILE_PATH: &str = "logs/optimizationsandtweaks/native.log";
fn log_file_path() -> String {
    std::env::var("OT_NATIVE_LOG").unwrap_or_else(|_| DEFAULT_LOG_FILE_PATH.to_string())
}

lazy_static::lazy_static! {
    static ref LOG_FILE: Mutex<Option<std::fs::File>> = Mutex::new(None);
    static ref PANIC_GUARD_ENABLED: std::sync::atomic::AtomicBool = std::sync::atomic::AtomicBool::new(true);
}

pub fn init_log_file() {
    if let Some(parent) = Path::new(&log_file_path()).parent() {
        let _ = std::fs::create_dir_all(parent);
    }

    // Vider le fichier une seule fois
    let _ = std::fs::OpenOptions::new()
        .write(true)
        .truncate(true)
        .create(true)
        .open(&log_file_path());
}

fn ensure_logger() {
    let mut guard = LOG_FILE.lock().unwrap();
    if guard.is_none() {
        match OpenOptions::new()
            .append(true)
            .create(true)
            .open(&log_file_path())
        {
            Ok(file) => *guard = Some(file),
            Err(e) => eprintln!("[OptimizationsAndTweaks/native] Failed to open log file: {}", e),
        }
    }
}

pub fn log_native_line<S: AsRef<str>>(msg: S) {
    ensure_logger();
    let ts = SystemTime::now().duration_since(UNIX_EPOCH).map(|d| d.as_secs()).unwrap_or(0);

    if let Some(ref mut file) = *LOG_FILE.lock().unwrap() {
        let _ = writeln!(file, "[{}][native] {}", ts, msg.as_ref());
        let _ = file.flush();
    } else {
        eprintln!("[{}][native] {}", ts, msg.as_ref());
    }
}

// === Panic guard enable/disable flag ===
pub fn set_panic_guard_enabled(enabled: bool) {
    PANIC_GUARD_ENABLED.store(enabled, std::sync::atomic::Ordering::Relaxed);
    log_native_line(format!("Panic guard {}", if enabled { "enabled" } else { "disabled" }));
}
pub fn is_panic_guard_enabled() -> bool {
    PANIC_GUARD_ENABLED.load(std::sync::atomic::Ordering::Relaxed)
}

pub fn init_panic_logging() {
    INIT_PANIC.call_once(|| {
        // Enable backtraces if not already enabled by environment
        let _ = std::env::set_var("RUST_BACKTRACE", "1");
        std::panic::set_hook(Box::new(|info: &std::panic::PanicHookInfo<'_>| {
            // Extract panic metadata
            let thread_name = std::thread::current().name().unwrap_or("unnamed").to_string();
            let location = info
                .location()
                .map(|l| format!("{}:{}:{}", l.file(), l.line(), l.column()))
                .unwrap_or_else(|| "unknown".to_string());
            let msg = if let Some(s) = info.payload().downcast_ref::<&str>() {
                (*s).to_string()
            } else if let Some(s) = info.payload().downcast_ref::<String>() {
                s.clone()
            } else {
                "panic payload not string".to_string()
            };

            // Capture backtrace (force to include frames even if env var not set)
            let bt = format!("{:?}", Backtrace::force_capture());

            // Write to native log
            log_native_line(format!(
                "[PANIC] thread='{}' at {}: {}",
                thread_name, location, msg
            ));
            log_native_line("[PANIC] backtrace begin");
            log_native_line(bt);
            log_native_line("[PANIC] backtrace end");

            // Also write to stderr which Forge/FML usually captures into fml-client-latest.log
            eprintln!(
                "[OptimizationsAndTweaks/native][PANIC] thread='{}' at {}: {} (see native.log for backtrace)",
                thread_name, location, msg
            );
        }));
    });
}

// =====================================================================================
// FFI panic safety guards
// =====================================================================================

/// Guard a closure executed from an FFI boundary, optionally catching panics and returning a default value.
/// When enabled, ensures panics do not unwind across the FFI boundary (which is undefined behavior).
pub fn ffi_panic_guard<R, F>(label: &str, default: R, f: F) -> R
where
    F: FnOnce() -> R + std::panic::UnwindSafe,
{
    if is_panic_guard_enabled() {
        match std::panic::catch_unwind(std::panic::AssertUnwindSafe(f)) {
            Ok(v) => v,
            Err(p) => {
                let msg = if let Some(s) = p.downcast_ref::<&str>() {
                    (*s).to_string()
                } else if let Some(s) = p.downcast_ref::<String>() {
                    s.clone()
                } else {
                    "non-string panic payload".to_string()
                };
                log_native_line(format!("[FFI][PANIC] in {}: {}", label, msg));
                default
            }
        }
    } else {
        // Panic guard disabled: execute without catching panics
        f()
    }
}

/// Guard a void-returning closure executed from an FFI boundary, optionally catching panics.
/// When enabled, ensures panics do not unwind across the FFI boundary (which is undefined behavior).
pub fn ffi_panic_guard_void<F>(label: &str, f: F)
where
    F: FnOnce() + std::panic::UnwindSafe,
{
    if is_panic_guard_enabled() {
        let _ = std::panic::catch_unwind(std::panic::AssertUnwindSafe(f)).map_err(|p| {
            let msg = if let Some(s) = p.downcast_ref::<&str>() {
                (*s).to_string()
            } else if let Some(s) = p.downcast_ref::<String>() {
                s.clone()
            } else {
                "non-string panic payload".to_string()
            };
            log_native_line(format!("[FFI][PANIC] in {}: {}", label, msg));
        });
    } else {
        f();
    }
}

#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_setPanicGuardEnabled(
    _env: jni::JNIEnv,
    _class: jni::objects::JClass,
    enabled: jni::sys::jboolean,
) {
    let rust_enabled = enabled != 0;
    set_panic_guard_enabled(rust_enabled);
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
    if let Some(parent) = Path::new(&log_file_path()).parent() {
        let _ = std::fs::create_dir_all(parent);
    }

    let _ = std::fs::OpenOptions::new()
        .write(true)
        .truncate(true)
        .create(true)
        .open(&log_file_path());
}

// Initialize leaktracer when the JNI library is loaded and dump stats on unload
#[no_mangle]
pub unsafe extern "system" fn JNI_OnLoad(_vm: *mut jni::sys::JavaVM, _reserved: *mut c_void) -> jint {
    init_log_file();
    // Install global panic hook for robust crash reporting
    init_panic_logging();

    // Ensure our native logger is ready
    log_native_line("JNI_OnLoad: native library loaded");

    // Initialize leaktracer symbol table for this crate in debug builds only
    #[cfg(debug_assertions)]
    {
        // Filter symbols to our crate to keep backtraces relevant
        leaktracer::init_symbol_table(&["optimizationsandtweaks_pathfinding"]);
        log_native_line("Leaktracer initialized (debug build)");
    }

    JNI_VERSION_1_6
}

#[no_mangle]
pub unsafe extern "system" fn JNI_OnUnload(_vm: *mut jni::sys::JavaVM, _reserved: *mut c_void) {
    log_native_line("JNI_OnUnload: native library unloading");

    // Dump leaktracer stats when unloading (debug builds only)
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