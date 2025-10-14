use jni::JNIEnv;
use jni::objects::{JClass, JString, JByteArray, JObject, JValue};
use jni::sys::{jstring, jlong, jint, jboolean, jintArray, jdouble, jfloat};
use std::sync::Mutex;
use std::collections::HashMap;
use std::fs::{OpenOptions, create_dir_all};
use std::io::Write;
use std::path::Path;
use std::time::{SystemTime, UNIX_EPOCH};
use std::os::raw::c_void;
use jni::sys::JNI_VERSION_1_6;
use std::panic;
use std::backtrace::Backtrace;
use std::sync::Once;
use std::env;

static INIT_PANIC: Once = Once::new();

fn init_panic_logging() {
    INIT_PANIC.call_once(|| {
        // Enable backtraces if not already enabled by environment
        let _ = env::set_var("RUST_BACKTRACE", "1");
        panic::set_hook(Box::new(|info| {
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

// Pathfinding module
pub mod pathfinding;

use pathfinding::{PathFinder, PathEntity};

// Global storage for PathFinder instances
// We use a Mutex to ensure thread-safety
lazy_static::lazy_static! {
    static ref PATH_ENTITIES: Mutex<HashMap<i64, PathEntity>> = Mutex::new(HashMap::new());
    static ref NEXT_ID: Mutex<i64> = Mutex::new(1);
    static ref PATHFINDER_CACHE: Mutex<HashMap<i64, PathFinder>> = Mutex::new(HashMap::new());
}

// Enable leaktracer allocator in debug builds to trace allocations made by this crate
#[cfg(debug_assertions)]
#[global_allocator]
static ALLOCATOR: leaktracer::LeaktracerAllocator = leaktracer::LeaktracerAllocator::init();

// Logging utilities
const LOG_FILE_PATH: &str = "/home/iamacat/.local/share/gdlauncher_carbon/data/instances/Forge 1.7.10 TEST/instance/logs/optimizationsandtweaks/native.log";

lazy_static::lazy_static! {
    static ref LOG_FILE: Mutex<Option<std::fs::File>> = Mutex::new(None);
}

fn ensure_logger() {
    let mut guard = LOG_FILE.lock().unwrap();
    if guard.is_none() {
        if let Some(parent) = Path::new(LOG_FILE_PATH).parent() {
            let _ = create_dir_all(parent);
        }
        match OpenOptions::new().create(true).write(true).truncate(true).open(LOG_FILE_PATH) {
            Ok(file) => {
                *guard = Some(file);
            }
            Err(e) => {
                eprintln!("[OptimizationsAndTweaks/native] Failed to open log file {}: {}", LOG_FILE_PATH, e);
            }
        }
    }
}

pub(crate) fn log_native_line<S: AsRef<str>>(msg: S) {
    ensure_logger();
    let ts = SystemTime::now().duration_since(UNIX_EPOCH).map(|d| d.as_secs()).unwrap_or(0);

    if let Some(ref mut file) = *LOG_FILE.lock().unwrap() {
        let _ = writeln!(file, "[{}][native] {}", ts, msg.as_ref());
        let _ = file.flush();
    } else {
        eprintln!("[{}][native] {}", ts, msg.as_ref());
    }
}

fn get_next_id() -> i64 {
    let mut id = NEXT_ID.lock().unwrap();
    let current = *id;
    *id += 1;
    current
}

// Initialize leaktracer when the JNI library is loaded and dump stats on unload
#[no_mangle]
pub unsafe extern "system" fn JNI_OnLoad(_vm: *mut jni::sys::JavaVM, _reserved: *mut c_void) -> jint {
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

/// Prints "Hello World from Rust!" to stdout
/// JNI signature: ()V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_rust_1hello_1world(
    _env: JNIEnv,
    _class: JClass,
) {
    log_native_line("Hello World from Rust!");
}

/// Returns a "Hello World from Rust!" string that can be used in Java
/// JNI signature: ()Ljava/lang/String;
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_rust_1get_1hello_1string(
    env: JNIEnv,
    _class: JClass,
) -> jstring {
    let output = env.new_string("Hello World from Rust!")
        .expect("Couldn't create java string!");
    output.into_raw()
}

/// Prints a custom message passed from Java
/// JNI signature: (Ljava/lang/String;)V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustFFI_rust_1print_1message(
    mut env: JNIEnv,
    _class: JClass,
    message: JString,
) {
    let message_str: String = env.get_string(&message)
        .expect("Couldn't get java string!")
        .into();
    
    println!("Rust received: {}", message_str);
}

// ============================================================================
// Pathfinding JNI Functions
// ============================================================================

/// Creates a new PathFinder instance and returns its handle
/// JNI signature: (ZZZZ)J
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_createPathFinder(
    _env: JNIEnv,
    _class: JClass,
    is_wooden_door_allowed: jboolean,
    is_movement_block_allowed: jboolean,
    is_pathing_in_water: jboolean,
    can_entity_drown: jboolean,
) -> jlong {

    // Encode flags into handle
    let mut handle: i64 = 0;

    if is_wooden_door_allowed != 0 {
        handle |= 1 << 0;
    }

    if is_movement_block_allowed != 0 {
        handle |= 1 << 1;
    }

    if is_pathing_in_water != 0 {
        handle |= 1 << 2;
    }

    if can_entity_drown != 0 {
        handle |= 1 << 3;
    }
    handle += 1;
    handle as jlong
}


/// Destroys a PathFinder instance
/// JNI signature: (J)V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_destroyPathFinder(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) {
}

/// Destroys a PathEntity instance
/// JNI signature: (J)V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_destroyPathEntity(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) {
    let removed = PATH_ENTITIES.lock().unwrap().remove(&handle).is_some();
}

/// Gets the current path index from a PathEntity
/// JNI signature: (J)I
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_pathEntityGetCurrentIndex(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) -> jint {
    let entities = PATH_ENTITIES.lock().unwrap();
    if let Some(entity) = entities.get(&handle) {
        entity.get_current_path_index() as jint
    } else {
        0
    }
}

/// Sets the current path index for a PathEntity
/// JNI signature: (JI)V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_pathEntitySetCurrentIndex(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    index: jint,
) {
    let mut entities = PATH_ENTITIES.lock().unwrap();
    if let Some(entity) = entities.get_mut(&handle) {
        entity.set_current_path_index(index as usize);
    }
}

/// Gets the path length from a PathEntity
/// JNI signature: (J)I
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_pathEntityGetLength(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) -> jint {
    let entities = PATH_ENTITIES.lock().unwrap();
    if let Some(entity) = entities.get(&handle) {
        entity.get_current_path_length() as jint
    } else {
        0
    }
}

/// Checks if a PathEntity is finished
/// JNI signature: (J)Z
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_pathEntityIsFinished(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) -> jboolean {
    let entities = PATH_ENTITIES.lock().unwrap();
    if let Some(entity) = entities.get(&handle) {
        entity.is_finished() as jboolean
    } else {
        1
    }
}

/// Gets a path point from a PathEntity at the given index
/// Returns an array [x, y, z]
/// JNI signature: (JI)[I
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_pathEntityGetPoint(
    env: JNIEnv,
    _class: JClass,
    handle: jlong,
    index: jint,
) -> jintArray {
    let entities = PATH_ENTITIES.lock().unwrap();
    
    if let Some(entity) = entities.get(&handle) {
        if let Some(point) = entity.get_path_point_from_index(index as usize) {
            let coords = [point.x_coord, point.y_coord, point.z_coord];
            let result = env.new_int_array(3).expect("Failed to create int array");
            env.set_int_array_region(&result, 0, &coords).expect("Failed to set array region");
            return result.into_raw();
        }
    }
    
    // Return empty array on error
    let result = env.new_int_array(0).expect("Failed to create int array");
    result.into_raw()
}

/// Gets all path points from a PathEntity
/// Returns a flattened array [x1, y1, z1, x2, y2, z2, ...]
/// JNI signature: (J)[I
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_pathEntityGetAllPoints(
    mut env: JNIEnv,
    _class: JClass,
    handle: jlong,
) -> jintArray {
    let entities = PATH_ENTITIES.lock().unwrap();
    
    if let Some(entity) = entities.get(&handle) {
        let mut coords = Vec::new();
        for point in &entity.points {
            coords.push(point.x_coord);
            coords.push(point.y_coord);
            coords.push(point.z_coord);
        }
        
        let result = env.new_int_array(coords.len() as i32).expect("Failed to create int array");
        env.set_int_array_region(&result, 0, &coords).expect("Failed to set array region");
        return result.into_raw();
    }
    
    // Return empty array on error
    let result = env.new_int_array(0).expect("Failed to create int array");
    result.into_raw()
}

// ============================================================================
// World Access Bridge - Simplified version using block ID cache
// ============================================================================

/// Simplified world access that uses a compact block type cache (byte codes) passed from Java
/// This avoids expensive JNI callbacks for every block check
struct CachedWorldAccess {
    // Each entry is a compact type code:
    // 0=Air, 1=Solid, 2=Water, 3=Lava, 4=WoodenDoor, 5=Trapdoor, 6=Fence, 7=FenceGate
    blocks: Vec<i8>,
    width: i32,
    height: i32,
    depth: i32,
    offset_x: i32,
    offset_y: i32,
    offset_z: i32,
}

impl CachedWorldAccess {
    fn new(blocks: Vec<i8>, width: i32, height: i32, depth: i32, offset_x: i32, offset_y: i32, offset_z: i32) -> Self {
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
    
    // Returns a compact block type code
    fn get_block_code(&self, x: i32, y: i32, z: i32) -> i8 {
        let local_x = x - self.offset_x;
        let local_y = y - self.offset_y;
        let local_z = z - self.offset_z;

        // Quick bounds checks
        if local_x < 0 || local_y < 0 || local_z < 0 {
            return 0;
        }
        if local_x >= self.width || local_y >= self.height || local_z >= self.depth {
            return 0; // Outside bounds -> treat as air
        }

        // Compute flattened index using wide integer math to avoid overflow
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
        let code = self.get_block_code(x, y, z);
        match code {
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
        0 // Simplified - metadata not needed for basic pathfinding
    }
    
    fn can_block_see_sky(&self, _x: i32, y: i32, _z: i32) -> bool {
        y >= self.offset_y + self.height - 1
    }
}

// JNI-backed world access adapter with LRU caching to minimize JNI overhead
struct JniWorldAccess<'b> {
    env: std::cell::RefCell<JNIEnv<'b>>, 
    adapter: JObject<'b>,
    // Simple LRU cache for block codes (position -> block code)
    block_cache: std::cell::RefCell<lru::LruCache<(i32, i32, i32), i8>>,
}

impl<'b> JniWorldAccess<'b> {
    fn new(env: JNIEnv<'b>, adapter: JObject<'b>) -> Self {
        // Cache up to 8192 block queries to reduce JNI overhead
        JniWorldAccess {
            env: std::cell::RefCell::new(env),
            adapter,
            block_cache: std::cell::RefCell::new(lru::LruCache::new(
                std::num::NonZeroUsize::new(8192).unwrap()
            )),
        }
    }

    fn jni_get_block_code(&self, x: i32, y: i32, z: i32) -> i8 {
        // Check cache first
        let key = (x, y, z);
        {
            let mut cache = self.block_cache.borrow_mut();
            if let Some(&code) = cache.get(&key) {
                return code;
            }
        }

        // Cache miss - query via JNI
        let mut env = self.env.borrow_mut();
        let code = match env.call_method(
            &self.adapter,
            "getBlockTypeCode",
            "(III)B",
            &[
                JValue::from(x as jint),
                JValue::from(y as jint),
                JValue::from(z as jint),
            ],
        ) {
            Ok(v) => v.b().unwrap_or(0) as i8,
            Err(_e) => { 
                log_native_line(format!("JNI getBlockTypeCode failed at ({},{},{})", x, y, z)); 
                0 
            },
        };

        // Store in cache
        self.block_cache.borrow_mut().put(key, code);
        code
    }

    fn jni_get_block_metadata(&self, x: i32, y: i32, z: i32) -> i32 {
        let mut env = self.env.borrow_mut();
        match env.call_method(
            &self.adapter,
            "getBlockMetadata",
            "(III)I",
            &[
                JValue::from(x as jint),
                JValue::from(y as jint),
                JValue::from(z as jint),
            ],
        ) {
            Ok(v) => v.i().unwrap_or(0),
            Err(_e) => { log_native_line("JNI getBlockMetadata failed"); 0 },
        }
    }

    fn jni_can_block_see_sky(&self, x: i32, y: i32, z: i32) -> bool {
        let mut env = self.env.borrow_mut();
        match env.call_method(
            &self.adapter,
            "canBlockSeeSky",
            "(III)Z",
            &[
                JValue::from(x as jint),
                JValue::from(y as jint),
                JValue::from(z as jint),
            ],
        ) {
            Ok(v) => v.z().unwrap_or(false),
            Err(_e) => { log_native_line("JNI canBlockSeeSky failed"); false },
        }
    }
}


impl<'b> pathfinding::path_finder::IBlockAccess for JniWorldAccess<'b> {
    fn get_block(&self, x: i32, y: i32, z: i32) -> pathfinding::path_finder::BlockType {
        match self.jni_get_block_code(x, y, z) {
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

    fn get_block_metadata(&self, x: i32, y: i32, z: i32) -> i32 {
        self.jni_get_block_metadata(x, y, z)
    }

    fn can_block_see_sky(&self, x: i32, y: i32, z: i32) -> bool {
        self.jni_can_block_see_sky(x, y, z)
    }
}

/// JNI signature: (JLjava/lang/Object;DDDDDDFFFZI)J
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_findPathDirect(
    mut env: JNIEnv,
    _class: JClass,
    pathfinder_handle: jlong,
    world_adapter: JObject,
    entity_x: jdouble,
    entity_y: jdouble,
    entity_z: jdouble,
    target_x: jdouble,
    target_y: jdouble,
    target_z: jdouble,
    entity_width: jfloat,
    entity_height: jfloat,
    max_distance: jfloat,
    is_in_water: jboolean,
    max_safe_point_tries: jint,
) -> jlong {
    // Decode flags from handle
    let raw = (pathfinder_handle as i64).saturating_sub(1);
    let wd = (raw & 1) != 0;
    let mb = (raw & (1 << 1)) != 0;
    let pw = (raw & (1 << 2)) != 0;
    let cd = (raw & (1 << 3)) != 0;

    // Get or create PathFinder for this handle
    let mut cache = PATHFINDER_CACHE.lock().unwrap();
    let pathfinder = cache.entry(pathfinder_handle as i64)
        .or_insert_with(|| PathFinder::new(wd, mb, pw, cd));

    // Build JNI-backed world accessor with caching
    let world_access = JniWorldAccess::new(env, world_adapter);

    let entity_data = pathfinding::path_finder::EntityData {
        pos_x: entity_x,
        pos_y: entity_y,
        pos_z: entity_z,
        width: entity_width,
        height: entity_height,
        is_in_water: is_in_water != 0,
        max_safe_point_tries,
        max_jump_height: 1,
    };

    if let Some(path_entity) = pathfinder.create_entity_path_to(
        &world_access,
        &entity_data,
        target_x,
        target_y,
        target_z,
        max_distance,
    ) {
        let id = get_next_id();
        PATH_ENTITIES.lock().unwrap().insert(id, path_entity);
        return id as jlong;
    }

    0 as jlong
}

/// JNI signature: (JIIIIILjava/lang/Object;DDDDDDFFFZI)J
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_findPathWithCache(
    mut env: JNIEnv,
    _class: JClass,
    pathfinder_handle: jlong,
    offset_x: jint,
    offset_y: jint,
    offset_z: jint,
    width: jint,
    height: jint,
    depth: jint,
    block_codes: JByteArray,
    entity_x: jdouble,
    entity_y: jdouble,
    entity_z: jdouble,
    target_x: jdouble,
    target_y: jdouble,
    target_z: jdouble,
    entity_width: jfloat,
    entity_height: jfloat,
    max_distance: jfloat,
    is_in_water: jboolean,
    max_safe_point_tries: jint,
) -> jlong {
    // Decode flags from handle
    let raw = (pathfinder_handle as i64).saturating_sub(1);
    let wd = (raw & 1) != 0;
    let mb = (raw & (1 << 1)) != 0;
    let pw = (raw & (1 << 2)) != 0;
    let cd = (raw & (1 << 3)) != 0;

    // Get or create PathFinder for this handle
    let mut cache = PATHFINDER_CACHE.lock().unwrap();
    let pathfinder = cache.entry(pathfinder_handle as i64)
        .or_insert_with(|| PathFinder::new(wd, mb, pw, cd));

    // Convert Java byte[] to Rust Vec<i8>
    let codes_u8: Vec<u8> = match env.convert_byte_array(&block_codes) {
        Ok(v) => v,
        Err(_e) => Vec::new(),
    };
    let blocks: Vec<i8> = codes_u8.into_iter().map(|b| b as i8).collect();

    let world_access = CachedWorldAccess::new(
        blocks,
        width as i32,
        height as i32,
        depth as i32,
        offset_x as i32,
        offset_y as i32,
        offset_z as i32,
    );

    let entity_data = pathfinding::path_finder::EntityData {
        pos_x: entity_x,
        pos_y: entity_y,
        pos_z: entity_z,
        width: entity_width,
        height: entity_height,
        is_in_water: is_in_water != 0,
        max_safe_point_tries,
        max_jump_height: 1,
    };

    if let Some(path_entity) = pathfinder.create_entity_path_to(
        &world_access,
        &entity_data,
        target_x,
        target_y,
        target_z,
        max_distance,
    ) {
        let id = get_next_id();
        PATH_ENTITIES.lock().unwrap().insert(id, path_entity);
        return id as jlong;
    }
    0 as jlong
}

//
               