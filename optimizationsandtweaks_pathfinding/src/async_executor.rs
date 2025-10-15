use std::sync::{
    atomic::{AtomicU64, AtomicUsize, Ordering},
    mpsc::{self, Receiver, SyncSender, TryRecvError},
    Arc, Mutex,
};
use std::thread::{self, JoinHandle};

use crate::log_native_line;
use crate::pathfinding::{self, PathFinder};
use crate::profiler;
use crate::{get_next_id, PATH_ENTITIES};

// =====================================================================================
// Internal cached world accessor identical to the one used in lib.rs for cached paths
// =====================================================================================

struct CachedWorldAccess {
    blocks: Vec<i8>,
    width: i32,
    height: i32,
    depth: i32,
    offset_x: i32,
    offset_y: i32,
    offset_z: i32,
}

impl CachedWorldAccess {
    fn new(
        blocks: Vec<i8>,
        width: i32,
        height: i32,
        depth: i32,
        offset_x: i32,
        offset_y: i32,
        offset_z: i32,
    ) -> Self {
        CachedWorldAccess {
            blocks,
            width,
            height,
            depth,
            offset_x,
            offset_y,
            offset_z,
        }
    }

    fn get_block_code(&self, x: i32, y: i32, z: i32) -> i8 {
        let local_x = x - self.offset_x;
        let local_y = y - self.offset_y;
        let local_z = z - self.offset_z;

        if local_x < 0 || local_y < 0 || local_z < 0 {
            return 0;
        }
        if local_x >= self.width || local_y >= self.height || local_z >= self.depth {
            return 0;
        }

        let w = self.width as i128;
        let d = self.depth as i128;
        let lx = local_x as i128;
        let ly = local_y as i128;
        let lz = local_z as i128;

        let idx128 = ly
            .checked_mul(w)
            .and_then(|v| v.checked_mul(d))
            .and_then(|v| v.checked_add(lz.checked_mul(w)?))
            .and_then(|v| v.checked_add(lx));

        if let Some(idx) = idx128 {
            if idx >= 0 {
                let index = idx as usize;
                if index < self.blocks.len() {
                    return self.blocks[index];
                }
            }
        }
        0
    }
}

impl pathfinding::path_finder::IBlockAccess for CachedWorldAccess {
    fn get_block(&self, x: i32, y: i32, z: i32) -> pathfinding::path_finder::BlockType {
        match self.get_block_code(x, y, z) {
            0 => pathfinding::path_finder::BlockType::Air,
            1 => pathfinding::path_finder::BlockType::Solid,
            2 => pathfinding::path_finder::BlockType::Water,
            3 => pathfinding::path_finder::BlockType::Lava,
            4 => pathfinding::path_finder::BlockType::WoodenDoor,
            5 => pathfinding::path_finder::BlockType::Trapdoor,
            6 => pathfinding::path_finder::BlockType::Fence,
            7 => pathfinding::path_finder::BlockType::FenceGate,
            8 => pathfinding::path_finder::BlockType::Slime,
            9 => pathfinding::path_finder::BlockType::Vine,
            10 => pathfinding::path_finder::BlockType::Ladder,
            11 => pathfinding::path_finder::BlockType::Cobweb,
            _ => pathfinding::path_finder::BlockType::NonSolid,
        }
    }

    fn get_block_metadata(&self, _x: i32, _y: i32, _z: i32) -> i32 {
        0
    }

    fn can_block_see_sky(&self, _x: i32, y: i32, _z: i32) -> bool {
        y >= self.offset_y + self.height - 1
    }
}

// =====================================================================================
// Async executor core
// =====================================================================================

#[derive(Clone)]
struct Job {
    request_id: i64,
    _priority: i32,
    // flags
    wd: bool,
    mb: bool,
    pw: bool,
    cd: bool,
    // cached volume
    offset_x: i32,
    offset_y: i32,
    offset_z: i32,
    width: i32,
    height: i32,
    depth: i32,
    block_cache: Vec<i8>,
    // entity and target
    entity_x: f64,
    entity_y: f64,
    entity_z: f64,
    target_x: f64,
    target_y: f64,
    target_z: f64,
    entity_width: f32,
    entity_height: f32,
    max_distance: f32,
    is_in_water: bool,
    max_safe_point_tries: i32,
}

#[derive(Clone, Copy)]
struct Completed {
    request_id: i64,
    path_handle: i64, // >0 valid handle, <0 no-path sentinel
}

struct ExecutorState {
    job_tx: SyncSender<Job>,
    result_rx: Receiver<Completed>,
    workers: Vec<JoinHandle<()>>,
    worker_count: usize,
    queue_len: Arc<AtomicUsize>,
    active_workers: Arc<AtomicUsize>,
    total_submitted: Arc<AtomicU64>,
    total_completed: Arc<AtomicU64>,
    total_failed: Arc<AtomicU64>,
}

lazy_static::lazy_static! {
    static ref EXECUTOR: Mutex<Option<ExecutorState>> = Mutex::new(None);
}

pub fn init(worker_count: usize, queue_size: usize) {
    let mut guard = EXECUTOR.lock().unwrap();
    if guard.is_some() {
        log_native_line("AsyncExecutor already initialized");
        return;
    }

    let (job_tx, job_rx_base) = mpsc::sync_channel::<Job>(queue_size.max(1));
    let job_rx = Arc::new(Mutex::new(job_rx_base));
    let (result_tx, result_rx) = mpsc::channel::<Completed>();

    let mut workers = Vec::with_capacity(worker_count.max(1));

    let queue_len = Arc::new(AtomicUsize::new(0));
    let active_workers = Arc::new(AtomicUsize::new(0));
    let total_submitted = Arc::new(AtomicU64::new(0));
    let total_completed = Arc::new(AtomicU64::new(0));
    let total_failed = Arc::new(AtomicU64::new(0));

    for i in 0..worker_count.max(1) {
        let rx = Arc::clone(&job_rx);
        let rtx = result_tx.clone();
        let ql = Arc::clone(&queue_len);
        let aw = Arc::clone(&active_workers);
        let tc = Arc::clone(&total_completed);
        let tf = Arc::clone(&total_failed);

        let handle = thread::Builder::new()
            .name(format!("async-path-worker-{}", i))
            .spawn(move || loop {
                match rx.lock().unwrap().recv() {
                    Ok(job) => {
                        // One item leaves the queue
                        ql.fetch_sub(1, Ordering::Relaxed);
                        aw.fetch_add(1, Ordering::Relaxed);

                        let req = job.request_id;
                        let completed = match std::panic::catch_unwind(|| process_job(job)) {
                            Ok(c) => c,
                            Err(_) => {
                                log_native_line("AsyncExecutor worker panicked during process_job");
                                Completed { request_id: req, path_handle: -1 }
                            }
                        };
                        if completed.path_handle > 0 {
                            tc.fetch_add(1, Ordering::Relaxed);
                        } else {
                            tf.fetch_add(1, Ordering::Relaxed);
                        }
                        let _ = rtx.send(completed);
                        aw.fetch_sub(1, Ordering::Relaxed);
                    }
                    Err(_) => break, // shutdown
                }
            })
            .expect("Failed to spawn async path worker");
        workers.push(handle);
    }

    *guard = Some(ExecutorState {
        job_tx,
        result_rx,
        workers,
        worker_count: worker_count.max(1),
        queue_len,
        active_workers,
        total_submitted,
        total_completed,
        total_failed,
    });

    log_native_line(format!(
        "AsyncExecutor initialized: workers={} queue_size={}",
        worker_count.max(1),
        queue_size.max(1)
    ));
}

pub fn shutdown() {
    let mut guard = EXECUTOR.lock().unwrap();
    if let Some(mut state) = guard.take() {
        // Drop sender to stop workers, then join
        drop(state.job_tx);
        for h in state.workers.drain(..) {
            let _ = h.join();
        }
        log_native_line("AsyncExecutor shutdown complete");
    }
}

pub fn submit_request(
    request_id: i64,
    priority: i32,
    wd: bool,
    mb: bool,
    pw: bool,
    cd: bool,
    offset_x: i32,
    offset_y: i32,
    offset_z: i32,
    width: i32,
    height: i32,
    depth: i32,
    block_cache: Vec<i8>,
    entity_x: f64,
    entity_y: f64,
    entity_z: f64,
    target_x: f64,
    target_y: f64,
    target_z: f64,
    entity_width: f32,
    entity_height: f32,
    max_distance: f32,
    is_in_water: bool,
    max_safe_point_tries: i32,
) -> bool {
    let guard = EXECUTOR.lock().unwrap();
    if let Some(state) = guard.as_ref() {
        let job = Job {
            request_id,
            _priority: priority,
            wd,
            mb,
            pw,
            cd,
            offset_x,
            offset_y,
            offset_z,
            width,
            height,
            depth,
            block_cache,
            entity_x,
            entity_y,
            entity_z,
            target_x,
            target_y,
            target_z,
            entity_width,
            entity_height,
            max_distance,
            is_in_water,
            max_safe_point_tries,
        };

        match state.job_tx.try_send(job) {
            Ok(_) => {
                state.queue_len.fetch_add(1, Ordering::Relaxed);
                state.total_submitted.fetch_add(1, Ordering::Relaxed);
                true
            }
            Err(mpsc::TrySendError::Full(_)) => false,
            Err(mpsc::TrySendError::Disconnected(_)) => false,
        }
    } else {
        false
    }
}

pub fn try_recv() -> Option<(i64, i64)> {
    let guard = EXECUTOR.lock().unwrap();
    if let Some(state) = guard.as_ref() {
        match state.result_rx.try_recv() {
            Ok(path_handle) => Some((path_handle.request_id, path_handle.path_handle)),
            Err(TryRecvError::Empty) => None,
            Err(TryRecvError::Disconnected) => None,
        }
    } else {
        None
    }
}

pub fn get_stats() -> [i32; 7] {
    let guard = EXECUTOR.lock().unwrap();
    if let Some(state) = guard.as_ref() {
        [
            state.total_submitted.load(Ordering::Relaxed) as i32, // total_submitted
            state.total_completed.load(Ordering::Relaxed) as i32, // total_completed
            state.total_failed.load(Ordering::Relaxed) as i32,    // total_failed
            0,                                                   // total_timed_out (not implemented)
            state.queue_len.load(Ordering::Relaxed) as i32,       // queue_size (current queued)
            state.active_workers.load(Ordering::Relaxed) as i32,  // active_workers
            state.worker_count as i32,                            // worker_count
        ]
    } else {
        [0; 7]
    }
}

// ===============
// Job processing
// ===============
fn process_job(job: Job) -> Completed {
    let Job {
        request_id,
        wd,
        mb,
        pw,
        cd,
        offset_x,
        offset_y,
        offset_z,
        width,
        height,
        depth,
        block_cache,
        entity_x,
        entity_y,
        entity_z,
        target_x,
        target_y,
        target_z,
        entity_width,
        entity_height,
        max_distance,
        is_in_water,
        max_safe_point_tries,
        ..
    } = job;

    let path_handle = {
        let _guard = if profiler::is_profiler_enabled() {
            Some(profiler::ProfileGuard::new("AsyncExecutor::process_job"))
        } else {
            None
        };

        let mut pathfinder = PathFinder::new(wd, mb, pw, cd);
        let world_access = CachedWorldAccess::new(
            block_cache,
            width,
            height,
            depth,
            offset_x,
            offset_y,
            offset_z,
        );

        let entity_data = pathfinding::path_finder::EntityData {
            pos_x: entity_x,
            pos_y: entity_y,
            pos_z: entity_z,
            width: entity_width,
            height: entity_height,
            is_in_water,
            max_safe_point_tries,
            max_jump_height: 1,
        };

        let path_entity = pathfinder.create_entity_path_to(
            &world_access,
            &entity_data,
            target_x,
            target_y,
            target_z,
            max_distance,
        );

        if let Some(path_entity) = path_entity {
            let id = get_next_id();
            PATH_ENTITIES.lock().unwrap().insert(id, path_entity);
            profiler::MEMORY_STATS.increment_path_entity();
            id
        } else {
            -1 // special sentinel for no-path; must be non-zero
        }
    };

    Completed {
        request_id,
        path_handle,
    }
}
