use optimizationsandtweaks_shared::log_native_line;
use std::collections::HashMap;
use std::sync::atomic::{AtomicBool, Ordering};
use std::sync::RwLock;
use std::time::Instant;


lazy_static::lazy_static! {
    static ref PROFILER_ENABLED: AtomicBool = AtomicBool::new(false);
    static ref STATS: RwLock<HashMap<&'static str, MethodStat>> = RwLock::new(HashMap::new());
}

#[derive(Clone, Copy, Debug)]
pub struct MethodStat {
    pub call_count: u64,
    pub total_time_ns: u64,
    pub min_time_ns: u64,
    pub max_time_ns: u64,
    pub avg_time_ns: u64,
    pub unstable_hits: u32,
}

impl Default for MethodStat {
    fn default() -> Self {
        MethodStat {
            call_count: 0,
            total_time_ns: 0,
            min_time_ns: u64::MAX,
            max_time_ns: 0,
            avg_time_ns: 0,
            unstable_hits: 0,
        }
    }
}

pub fn set_profiler_enabled(enabled: bool) {
    PROFILER_ENABLED.store(enabled, Ordering::Relaxed);
}

pub fn is_profiler_enabled() -> bool {
    PROFILER_ENABLED.load(Ordering::Relaxed)
}

pub fn clear_stats() {
    if let Ok(mut map) = STATS.write() {
        map.clear();
    }
}

pub fn get_all_stats() -> HashMap<&'static str, MethodStat> {
    STATS.read().map(|m| m.clone()).unwrap_or_default()
}

pub fn print_stats() {
    if !PROFILER_ENABLED.load(Ordering::Relaxed) {
        return;
    }

    let stats = get_all_stats();
    if stats.is_empty() {
        log_native_line("[Profiler] No stats collected");
        return;
    }

    log_native_line("=== OptimizationsAndTweaks Profiler ===");
    let mut entries: Vec<_> = stats.into_iter().collect();
    entries.sort_by(|a, b| b.1.total_time_ns.cmp(&a.1.total_time_ns));

    for (name, s) in entries {
        let total_ms = s.total_time_ns as f64 / 1_000_000.0;
        let avg_us = s.avg_time_ns as f64 / 1_000.0;
        let min_us = s.min_time_ns as f64 / 1_000.0;
        let max_us = s.max_time_ns as f64 / 1_000.0;
        let unstable = if s.unstable_hits > 0 { "⚠️" } else { " " };

        log_native_line(format!(
            "{:<40} | Calls: {:>8} | Total: {:>10.2}ms | Avg: {:>8.2}µs | Min: {:>8.2}µs | Max: {:>8.2}µs | {}",
            name, s.call_count, total_ms, avg_us, min_us, max_us, unstable
        ));
    }
}

pub struct ProfileGuard {
    name: &'static str,
    start_time: Instant,
}

impl ProfileGuard {
    pub fn new(name: &'static str) -> Self {
        ProfileGuard {
            name,
            start_time: Instant::now(),
        }
    }
}

impl Drop for ProfileGuard {
    fn drop(&mut self) {
        if !is_profiler_enabled() {
            return;
        }

        let elapsed_ns = self.start_time.elapsed().as_nanos() as u64;

        if let Ok(mut map) = STATS.write() {
            let stat = map.entry(self.name).or_insert_with(MethodStat::default);
            stat.call_count += 1;
            stat.total_time_ns += elapsed_ns;
            stat.min_time_ns = stat.min_time_ns.min(elapsed_ns);
            stat.max_time_ns = stat.max_time_ns.max(elapsed_ns);
            stat.avg_time_ns = stat.total_time_ns / stat.call_count;

            let elapsed_ms = elapsed_ns as f64 / 1_000_000.0;
            if elapsed_ms > 100.0 {
                stat.unstable_hits += 1;
                log_native_line(format!(
                    "[Profiler][Warning] {} seems unstable — time {:.2}ms",
                    self.name, elapsed_ms
                ));
            }
        }

        log_native_line(format!(
            "[Profiler] {:<40} | time: {:.3}ms",
            self.name,
            elapsed_ns as f64 / 1_000_000.0
        ));
    }
}
