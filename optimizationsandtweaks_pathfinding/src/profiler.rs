use std::time::{Duration, Instant};
use std::sync::atomic::{AtomicBool, AtomicU64, Ordering};
use std::sync::Mutex;
use std::collections::HashMap;
use crate::log_native_line;

/// Global profiler enable flag
static PROFILER_ENABLED: AtomicBool = AtomicBool::new(false);

/// Enable or disable the profiler
pub fn set_profiler_enabled(enabled: bool) {
    PROFILER_ENABLED.store(enabled, Ordering::Relaxed);
}

/// Check if profiler is enabled
pub fn is_profiler_enabled() -> bool {
    PROFILER_ENABLED.load(Ordering::Relaxed)
}

/// Profiling statistics for a single method
#[derive(Debug, Clone)]
pub struct MethodStats {
    pub call_count: u64,
    pub total_time_ns: u64,
    pub min_time_ns: u64,
    pub max_time_ns: u64,
    pub avg_time_ns: u64,
}

impl MethodStats {
    fn new() -> Self {
        MethodStats {
            call_count: 0,
            total_time_ns: 0,
            min_time_ns: u64::MAX,
            max_time_ns: 0,
            avg_time_ns: 0,
        }
    }

    fn record(&mut self, duration_ns: u64) {
        self.call_count += 1;
        self.total_time_ns += duration_ns;
        self.min_time_ns = self.min_time_ns.min(duration_ns);
        self.max_time_ns = self.max_time_ns.max(duration_ns);
        self.avg_time_ns = self.total_time_ns / self.call_count;
    }
}

/// Global profiler data
lazy_static::lazy_static! {
    static ref PROFILER_DATA: Mutex<HashMap<String, MethodStats>> = Mutex::new(HashMap::new());
}

/// Record a method execution time
pub fn record_method_time(method_name: &str, duration: Duration) {
    if !is_profiler_enabled() {
        return;
    }

    let duration_ns = duration.as_nanos() as u64;
    
    if let Ok(mut data) = PROFILER_DATA.lock() {
        let stats = data.entry(method_name.to_string()).or_insert_with(MethodStats::new);
        stats.record(duration_ns);
    }
}

/// Get profiling statistics for a method
pub fn get_method_stats(method_name: &str) -> Option<MethodStats> {
    if let Ok(data) = PROFILER_DATA.lock() {
        data.get(method_name).cloned()
    } else {
        None
    }
}

/// Get all profiling statistics
pub fn get_all_stats() -> HashMap<String, MethodStats> {
    if let Ok(data) = PROFILER_DATA.lock() {
        data.clone()
    } else {
        HashMap::new()
    }
}

/// Clear all profiling statistics
pub fn clear_stats() {
    if let Ok(mut data) = PROFILER_DATA.lock() {
        data.clear();
    }
}

/// Print profiling statistics to log
pub fn print_stats() {
    if !is_profiler_enabled() {
        return;
    }

    if let Ok(data) = PROFILER_DATA.lock() {
        log_native_line("\n=== Pathfinding Profiler Statistics ===");
        
        let mut methods: Vec<_> = data.iter().collect();
        methods.sort_by(|a, b| b.1.total_time_ns.cmp(&a.1.total_time_ns));
        
        for (method, stats) in methods {
            log_native_line(&format!(
                "{:<40} | Calls: {:>8} | Total: {:>10.2}ms | Avg: {:>8.2}µs | Min: {:>8.2}µs | Max: {:>8.2}µs",
                method,
                stats.call_count,
                stats.total_time_ns as f64 / 1_000_000.0,
                stats.avg_time_ns as f64 / 1_000.0,
                stats.min_time_ns as f64 / 1_000.0,
                stats.max_time_ns as f64 / 1_000.0
            ));
        }
        log_native_line("========================================");
    }
}

/// RAII guard for automatic method profiling
pub struct ProfileGuard {
    method_name: String,
    start: Instant,
}

impl ProfileGuard {
    pub fn new(method_name: &str) -> Self {
        ProfileGuard {
            method_name: method_name.to_string(),
            start: Instant::now(),
        }
    }
}

impl Drop for ProfileGuard {
    fn drop(&mut self) {
        if is_profiler_enabled() {
            let duration = self.start.elapsed();
            record_method_time(&self.method_name, duration);
        }
    }
}

/// Macro for easy method profiling
#[macro_export]
macro_rules! profile {
    ($name:expr) => {
        let _profile_guard = if $crate::profiler::is_profiler_enabled() {
            Some($crate::profiler::ProfileGuard::new($name))
        } else {
            None
        };
    };
}

/// Memory usage tracking
pub struct MemoryStats {
    pub pathfinder_count: AtomicU64,
    pub path_entity_count: AtomicU64,
    pub point_map_size: AtomicU64,
    pub visited_cache_size: AtomicU64,
}

impl MemoryStats {
    pub const fn new() -> Self {
        MemoryStats {
            pathfinder_count: AtomicU64::new(0),
            path_entity_count: AtomicU64::new(0),
            point_map_size: AtomicU64::new(0),
            visited_cache_size: AtomicU64::new(0),
        }
    }

    pub fn increment_pathfinder(&self) {
        self.pathfinder_count.fetch_add(1, Ordering::Relaxed);
    }

    pub fn decrement_pathfinder(&self) {
        self.pathfinder_count.fetch_sub(1, Ordering::Relaxed);
    }

    pub fn increment_path_entity(&self) {
        self.path_entity_count.fetch_add(1, Ordering::Relaxed);
    }

    pub fn decrement_path_entity(&self) {
        self.path_entity_count.fetch_sub(1, Ordering::Relaxed);
    }

    pub fn set_point_map_size(&self, size: u64) {
        self.point_map_size.store(size, Ordering::Relaxed);
    }

    pub fn set_visited_cache_size(&self, size: u64) {
        self.visited_cache_size.store(size, Ordering::Relaxed);
    }

    pub fn print(&self) {
        if !is_profiler_enabled() {
            return;
        }

        log_native_line("\n=== Pathfinding Memory Statistics ===");
        log_native_line(&format!("Active PathFinders: {}", self.pathfinder_count.load(Ordering::Relaxed)));
        log_native_line(&format!("Active PathEntities: {}", self.path_entity_count.load(Ordering::Relaxed)));
        log_native_line(&format!("Point Map Size: {}", self.point_map_size.load(Ordering::Relaxed)));
        log_native_line(&format!("Visited Cache Size: {}", self.visited_cache_size.load(Ordering::Relaxed)));
        log_native_line("======================================\n");
    }
}

/// Global memory statistics
pub static MEMORY_STATS: MemoryStats = MemoryStats::new();

#[cfg(test)]
mod tests {
    use super::*;
    use std::thread;
    use std::time::Duration;

    #[test]
    fn test_profiler_enable_disable() {
        set_profiler_enabled(true);
        assert!(is_profiler_enabled());
        
        set_profiler_enabled(false);
        assert!(!is_profiler_enabled());
    }

    #[test]
    fn test_method_stats() {
        set_profiler_enabled(true);
        clear_stats();

        record_method_time("test_method", Duration::from_micros(100));
        record_method_time("test_method", Duration::from_micros(200));
        record_method_time("test_method", Duration::from_micros(150));

        let stats = get_method_stats("test_method").unwrap();
        assert_eq!(stats.call_count, 3);
        assert_eq!(stats.min_time_ns, 100_000);
        assert_eq!(stats.max_time_ns, 200_000);
        assert_eq!(stats.avg_time_ns, 150_000);

        set_profiler_enabled(false);
    }

    #[test]
    fn test_profile_guard() {
        set_profiler_enabled(true);
        clear_stats();

        {
            let _guard = ProfileGuard::new("test_guard");
            thread::sleep(Duration::from_millis(10));
        }

        let stats = get_method_stats("test_guard").unwrap();
        assert_eq!(stats.call_count, 1);
        assert!(stats.total_time_ns >= 10_000_000); // At least 10ms

        set_profiler_enabled(false);
    }

    #[test]
    fn test_memory_stats() {
        MEMORY_STATS.increment_pathfinder();
        MEMORY_STATS.increment_pathfinder();
        assert_eq!(MEMORY_STATS.pathfinder_count.load(Ordering::Relaxed), 2);

        MEMORY_STATS.decrement_pathfinder();
        assert_eq!(MEMORY_STATS.pathfinder_count.load(Ordering::Relaxed), 1);

        MEMORY_STATS.set_point_map_size(1000);
        assert_eq!(MEMORY_STATS.point_map_size.load(Ordering::Relaxed), 1000);
    }
}
