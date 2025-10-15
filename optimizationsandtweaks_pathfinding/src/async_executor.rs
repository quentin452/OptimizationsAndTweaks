use std::sync::{Arc, Mutex};
use std::sync::atomic::{AtomicU64, AtomicUsize, Ordering};
use crossbeam::channel::{bounded, Sender, Receiver};
use std::thread;
use std::time::{Duration, Instant};

use crate::pathfinding::{PathFinder, PathEntity, path_finder::{EntityData, IBlockAccess}};
use crate::log_native_line;

/// Priority levels for pathfinding tasks
#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord)]
pub enum Priority {
    Background = 10,
    Low = 25,
    Normal = 50,
    High = 75,
    Critical = 100,
}

/// A pathfinding request with priority
pub struct PathfindingRequest {
    pub id: u64,
    pub priority: Priority,
    pub pathfinder_flags: PathFinderFlags,
    pub entity_data: EntityData,
    pub target_x: f64,
    pub target_y: f64,
    pub target_z: f64,
    pub max_distance: f32,
    pub world_data: WorldData,
    pub submitted_at: Instant,
}

/// PathFinder configuration flags
#[derive(Debug, Clone, Copy)]
pub struct PathFinderFlags {
    pub is_wooden_door_allowed: bool,
    pub is_movement_block_allowed: bool,
    pub is_pathing_in_water: bool,
    pub can_entity_drown: bool,
}

/// World data for pathfinding (cached block data)
pub struct WorldData {
    pub blocks: Vec<i8>,
    pub width: i32,
    pub height: i32,
    pub depth: i32,
    pub offset_x: i32,
    pub offset_y: i32,
    pub offset_z: i32,
}

/// Result of a pathfinding operation
pub struct PathfindingResult {
    pub id: u64,
    pub path: Option<PathEntity>,
    pub execution_time: Duration,
    pub success: bool,
}

/// Statistics for the async executor
#[derive(Debug, Clone)]
pub struct ExecutorStats {
    pub total_submitted: u64,
    pub total_completed: u64,
    pub total_failed: u64,
    pub total_timed_out: u64,
    pub queue_size: usize,
    pub active_workers: usize,
    pub worker_count: usize,
}

/// Async pathfinding executor using Rust threads
pub struct AsyncPathfindingExecutor {
    workers: Vec<thread::JoinHandle<()>>,
    request_tx: Sender<PathfindingRequest>,
    result_rx: Receiver<PathfindingResult>,
    result_tx: Sender<PathfindingResult>,
    
    // Statistics
    total_submitted: Arc<AtomicU64>,
    total_completed: Arc<AtomicU64>,
    total_failed: Arc<AtomicU64>,
    total_timed_out: Arc<AtomicU64>,
    active_workers: Arc<AtomicUsize>,
    
    // Shutdown flag
    shutdown: Arc<Mutex<bool>>,
}

impl AsyncPathfindingExecutor {
    /// Create a new async pathfinding executor
    /// 
    /// # Arguments
    /// * `worker_count` - Number of worker threads
    /// * `queue_size` - Maximum queue size for backpressure
    pub fn new(worker_count: usize, queue_size: usize) -> Self {
        let (request_tx, request_rx) = bounded::<PathfindingRequest>(queue_size);
        let (result_tx, result_rx) = bounded::<PathfindingResult>(queue_size * 2);
        
        let total_submitted = Arc::new(AtomicU64::new(0));
        let total_completed = Arc::new(AtomicU64::new(0));
        let total_failed = Arc::new(AtomicU64::new(0));
        let total_timed_out = Arc::new(AtomicU64::new(0));
        let active_workers = Arc::new(AtomicUsize::new(0));
        let shutdown = Arc::new(Mutex::new(false));
        
        // Spawn worker threads
        let mut workers = Vec::new();
        for worker_id in 0..worker_count {
            let request_rx = request_rx.clone();
            let result_tx = result_tx.clone();
            let active_workers = active_workers.clone();
            let total_completed = total_completed.clone();
            let total_failed = total_failed.clone();
            let shutdown = shutdown.clone();
            
            let handle = thread::Builder::new()
                .name(format!("RustPathfindingWorker-{}", worker_id))
                .spawn(move || {
                    Self::worker_loop(
                        worker_id,
                        request_rx,
                        result_tx,
                        active_workers,
                        total_completed,
                        total_failed,
                        shutdown,
                    );
                })
                .expect("Failed to spawn worker thread");
            
            workers.push(handle);
        }
        
        log_native_line(format!(
            "AsyncPathfindingExecutor initialized with {} workers and queue size {}",
            worker_count, queue_size
        ));
        
        AsyncPathfindingExecutor {
            workers,
            request_tx,
            result_rx,
            result_tx,
            total_submitted,
            total_completed,
            total_failed,
            total_timed_out,
            active_workers,
            shutdown,
        }
    }
    
    /// Worker thread loop
    fn worker_loop(
        worker_id: usize,
        request_rx: Receiver<PathfindingRequest>,
        result_tx: Sender<PathfindingResult>,
        active_workers: Arc<AtomicUsize>,
        total_completed: Arc<AtomicU64>,
        total_failed: Arc<AtomicU64>,
        shutdown: Arc<Mutex<bool>>,
    ) {
        log_native_line(format!("Worker {} started", worker_id));
        
        loop {
            // Check shutdown flag
            if *shutdown.lock().unwrap() {
                break;
            }
            
            // Wait for a request with timeout
            match request_rx.recv_timeout(Duration::from_millis(100)) {
                Ok(request) => {
                    active_workers.fetch_add(1, Ordering::Relaxed);
                    
                    let start = Instant::now();
                    let result = Self::process_request(request);
                    let execution_time = start.elapsed();
                    
                    if result.success {
                        total_completed.fetch_add(1, Ordering::Relaxed);
                    } else {
                        total_failed.fetch_add(1, Ordering::Relaxed);
                    }
                    
                    // Send result back
                    let _ = result_tx.send(PathfindingResult {
                        id: result.id,
                        path: result.path,
                        execution_time,
                        success: result.success,
                    });
                    
                    active_workers.fetch_sub(1, Ordering::Relaxed);
                }
                Err(crossbeam::channel::RecvTimeoutError::Timeout) => {
                    // Timeout - continue loop
                    continue;
                }
                Err(crossbeam::channel::RecvTimeoutError::Disconnected) => {
                    // Channel disconnected - shutdown
                    break;
                }
            }
        }
        
        log_native_line(format!("Worker {} stopped", worker_id));
    }
    
    /// Process a single pathfinding request
    fn process_request(request: PathfindingRequest) -> PathfindingResult {
        // Create world access from cached data
        let world_access = CachedWorldAccess {
            blocks: request.world_data.blocks,
            width: request.world_data.width,
            height: request.world_data.height,
            depth: request.world_data.depth,
            offset_x: request.world_data.offset_x,
            offset_y: request.world_data.offset_y,
            offset_z: request.world_data.offset_z,
        };
        
        // Create pathfinder
        let mut pathfinder = PathFinder::new(
            request.pathfinder_flags.is_wooden_door_allowed,
            request.pathfinder_flags.is_movement_block_allowed,
            request.pathfinder_flags.is_pathing_in_water,
            request.pathfinder_flags.can_entity_drown,
        );
        
        // Execute pathfinding
        let path = pathfinder.create_entity_path_to(
            &world_access,
            &request.entity_data,
            request.target_x,
            request.target_y,
            request.target_z,
            request.max_distance,
        );

        let success = path.is_some();

        PathfindingResult {
            id: request.id,
            path,
            execution_time: Duration::from_secs(0),
            success,
        }
    }
    
    /// Submit a pathfinding request
    /// Returns the request ID, or None if the queue is full
    pub fn submit(&self, request: PathfindingRequest) -> Option<u64> {
        let id = request.id;
        self.total_submitted.fetch_add(1, Ordering::Relaxed);
        
        match self.request_tx.try_send(request) {
            Ok(_) => Some(id),
            Err(_) => {
                // Queue full - request rejected
                None
            }
        }
    }
    
    /// Try to receive a completed result (non-blocking)
    pub fn try_recv_result(&self) -> Option<PathfindingResult> {
        self.result_rx.try_recv().ok()
    }
    
    /// Receive a completed result with timeout
    pub fn recv_result_timeout(&self, timeout: Duration) -> Option<PathfindingResult> {
        self.result_rx.recv_timeout(timeout).ok()
    }
    
    /// Get current statistics
    pub fn get_stats(&self) -> ExecutorStats {
        ExecutorStats {
            total_submitted: self.total_submitted.load(Ordering::Relaxed),
            total_completed: self.total_completed.load(Ordering::Relaxed),
            total_failed: self.total_failed.load(Ordering::Relaxed),
            total_timed_out: self.total_timed_out.load(Ordering::Relaxed),
            queue_size: self.request_tx.len(),
            active_workers: self.active_workers.load(Ordering::Relaxed),
            worker_count: self.workers.len(),
        }
    }
    
    /// Shutdown the executor gracefully
    pub fn shutdown(self) {
        log_native_line("Shutting down AsyncPathfindingExecutor");
        
        // Set shutdown flag
        *self.shutdown.lock().unwrap() = true;
        
        // Drop sender to signal workers
        drop(self.request_tx);
        drop(self.result_tx);
        
        // Wait for workers to finish
        for (i, worker) in self.workers.into_iter().enumerate() {
            if let Err(e) = worker.join() {
                log_native_line(format!("Worker {} failed to join: {:?}", i, e));
            }
        }
        
        log_native_line("AsyncPathfindingExecutor shutdown complete");
    }
}

/// Cached world access implementation
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

impl IBlockAccess for CachedWorldAccess {
    fn get_block(&self, x: i32, y: i32, z: i32) -> crate::pathfinding::path_finder::BlockType {
        use crate::pathfinding::path_finder::BlockType;
        
        match self.get_block_code(x, y, z) {
            0 => BlockType::Air,
            1 => BlockType::Solid,
            2 => BlockType::Water,
            3 => BlockType::Lava,
            4 => BlockType::WoodenDoor,
            5 => BlockType::Trapdoor,
            6 => BlockType::Fence,
            7 => BlockType::FenceGate,
            8 => BlockType::Slime,
            9 => BlockType::Vine,
            10 => BlockType::Ladder,
            11 => BlockType::Cobweb,
            _ => BlockType::NonSolid,
        }
    }
    
    fn get_block_metadata(&self, _x: i32, _y: i32, _z: i32) -> i32 {
        0
    }
    
    fn can_block_see_sky(&self, _x: i32, y: i32, _z: i32) -> bool {
        y >= self.offset_y + self.height - 1
    }
}

/// Global executor instance
static mut GLOBAL_EXECUTOR: Option<AsyncPathfindingExecutor> = None;
static EXECUTOR_INIT: std::sync::Once = std::sync::Once::new();

/// Initialize the global executor
pub fn init_global_executor(worker_count: usize, queue_size: usize) {
    unsafe {
        EXECUTOR_INIT.call_once(|| {
            GLOBAL_EXECUTOR = Some(AsyncPathfindingExecutor::new(worker_count, queue_size));
        });
    }
}

/// Get the global executor
pub fn get_global_executor() -> Option<&'static AsyncPathfindingExecutor> {
    unsafe { GLOBAL_EXECUTOR.as_ref() }
}

/// Shutdown the global executor
pub fn shutdown_global_executor() {
    unsafe {
        if let Some(executor) = GLOBAL_EXECUTOR.take() {
            executor.shutdown();
        }
    }
}
