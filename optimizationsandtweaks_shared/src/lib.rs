use std::backtrace::Backtrace;
use std::env;
use std::fs::{create_dir_all, OpenOptions};
use std::io::Write;
use std::path::Path;
use std::sync::{Mutex, Once};
use std::time::{SystemTime, UNIX_EPOCH};

// Global init guard for installing a robust panic hook exactly once
static INIT_PANIC: Once = Once::new();

// Path for native logging
const DEFAULT_LOG_FILE_PATH: &str = "logs/optimizationsandtweaks/native.log";
fn log_file_path() -> String {
    std::env::var("OT_NATIVE_LOG").unwrap_or_else(|_| DEFAULT_LOG_FILE_PATH.to_string())
}

lazy_static::lazy_static! {
    static ref LOG_FILE: Mutex<Option<std::fs::File>> = Mutex::new(None);
}

fn ensure_logger() {
    let mut guard = LOG_FILE.lock().unwrap();
    if guard.is_none() {
        if let Some(parent) = Path::new(&log_file_path()).parent() {
            let _ = create_dir_all(parent);
        }
        match OpenOptions::new().create(true).write(true).truncate(true).open(&log_file_path()) {
            Ok(file) => {
                *guard = Some(file);
            }
            Err(e) => {
                eprintln!("[OptimizationsAndTweaks/native] Failed to open log file: {}", e);
            }
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
