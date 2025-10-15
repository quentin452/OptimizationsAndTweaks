use optimizationsandtweaks_shared::{init_panic_logging, log_native_line};
use jni::JNIEnv;
use jni::objects::{JClass, JString, JByteArray, JObject, JValue, JIntArray};
use jni::sys::{jstring, jlong, jint, jboolean, jintArray, jlongArray, jdouble, jfloat};
use std::sync::Mutex;
use std::collections::HashMap;
use std::fs::{OpenOptions, create_dir_all};
use std::io::Write;
use std::path::Path;
use std::time::{SystemTime, UNIX_EPOCH};
use std::os::raw::c_void;
use jni::sys::JNI_VERSION_1_6;
use std::panic;


// Pathfinding module
pub mod pathfinding;
pub mod profiler;
pub mod async_executor;

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
    
    log_native_line(&format!("Rust received: {}", message_str));
}

// ============================================================================
// Pathfinding JNI Functions
// ============================================================================

/// Creates a new PathFinder instance and returns its handle
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
    profiler::MEMORY_STATS.increment_pathfinder();

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
    profiler::MEMORY_STATS.decrement_pathfinder();
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
    if removed {
        profiler::MEMORY_STATS.decrement_path_entity();
    }
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

    let path_entity = {
        let _guard = if profiler::is_profiler_enabled() {
            Some(profiler::ProfileGuard::new("PathFinder::create_entity_path_to"))
        } else {
            None
        };
        
        pathfinder.create_entity_path_to(
            &world_access,
            &entity_data,
            target_x,
            target_y,
            target_z,
            max_distance,
        )
    };

    if let Some(path_entity) = path_entity {
        let id = get_next_id();
        PATH_ENTITIES.lock().unwrap().insert(id, path_entity);
        profiler::MEMORY_STATS.increment_path_entity();
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

    let path_entity = {
        let _guard = if profiler::is_profiler_enabled() {
            Some(profiler::ProfileGuard::new("PathFinder::create_entity_path_to_cached"))
        } else {
            None
        };
        
        pathfinder.create_entity_path_to(
            &world_access,
            &entity_data,
            target_x,
            target_y,
            target_z,
            max_distance,
        )
    };

    if let Some(path_entity) = path_entity {
        let id = get_next_id();
        PATH_ENTITIES.lock().unwrap().insert(id, path_entity);
        profiler::MEMORY_STATS.increment_path_entity();
        return id as jlong;
    }
    0 as jlong
}

// =====================================================================================
// Async Executor JNI Functions
// =====================================================================================

#[no_mangle]
pub unsafe extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_initAsyncExecutor(
    _env: JNIEnv,
    _class: JClass,
    worker_count: jint,
    queue_size: jint,
) {
    init_panic_logging();
    async_executor::init(worker_count as usize, queue_size as usize);
}

#[no_mangle]
pub unsafe extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_submitAsyncPathfinding(
    env: JNIEnv,
    _class: JClass,
    request_id: jlong,
    priority: jint,
    wd: jboolean,
    mb: jboolean,
    pw: jboolean,
    cd: jboolean,
    offset_x: jint,
    offset_y: jint,
    offset_z: jint,
    width: jint,
    height: jint,
    depth: jint,
    block_cache: JByteArray,
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
    init_panic_logging();

    let block_cache_vec: Vec<i8> = match env.convert_byte_array(block_cache) {
        Ok(arr) => arr.into_iter().map(|b| b as i8).collect(),
        Err(_) => {
            log_native_line("Failed to convert block_cache byte array (RustPathfinding.submitAsyncPathfinding)");
            return 0;
        }
    };

    let success = async_executor::submit_request(
        request_id,
        priority,
        wd != 0,
        mb != 0,
        pw != 0,
        cd != 0,
        offset_x,
        offset_y,
        offset_z,
        width,
        height,
        depth,
        block_cache_vec,
        entity_x,
        entity_y,
        entity_z,
        target_x,
        target_y,
        target_z,
        entity_width,
        entity_height,
        max_distance,
        is_in_water != 0,
        max_safe_point_tries,
    );

    if success { request_id } else { 0 }
}

#[no_mangle]
pub unsafe extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_getAsyncExecutorStats(
    env: JNIEnv,
    _class: JClass,
) -> jintArray {
    init_panic_logging();
    let stats = async_executor::get_stats();
    let result = env.new_int_array(7).unwrap();
    env.set_int_array_region(&result, 0, &stats).unwrap();
    result.into_raw()
}

#[no_mangle]
pub unsafe extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_shutdownAsyncExecutor(
    _env: JNIEnv,
    _class: JClass,
) {
    init_panic_logging();
    async_executor::shutdown();
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_nothing_optimizationsandtweaks_OptimizationsAndTweaks_submitAsyncPathfinding(
    env: JNIEnv,
    _class: JClass,
    request_id: jlong,
    priority: jint,
    wd: jboolean,
    mb: jboolean,
    pw: jboolean,
    cd: jboolean,
    offset_x: jint,
    offset_y: jint,
    offset_z: jint,
    width: jint,
    height: jint,
    depth: jint,
    block_cache: JByteArray,
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
) -> jboolean {
    init_panic_logging();
    
    let block_cache_vec: Vec<i8> = if let Ok(arr) = env.convert_byte_array(block_cache) {
        arr.into_iter().map(|b| b as i8).collect()
    } else {
        log_native_line("Failed to convert block_cache byte array");
        return false as jboolean;
    };

    let success = async_executor::submit_request(
        request_id,
        priority,
        wd != 0,
        mb != 0,
        pw != 0,
        cd != 0,
        offset_x,
        offset_y,
        offset_z,
        width,
        height,
        depth,
        block_cache_vec,
        entity_x,
        entity_y,
        entity_z,
        target_x,
        target_y,
        target_z,
        entity_width,
        entity_height,
        max_distance,
        is_in_water != 0,
        max_safe_point_tries,
    );

    success as jboolean
}

#[no_mangle]
pub unsafe extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_tryRecvAsyncResult(
    env: JNIEnv,
    _class: JClass,
    out_request_id: jintArray,
) -> jlong {
    init_panic_logging();

    let out_request_id = JIntArray::from_raw(out_request_id);

    if let Some((request_id, path_handle)) = async_executor::try_recv() {
        let buf = [request_id as jint];
        env.set_int_array_region(&out_request_id, 0, &buf)
            .expect("Failed to write to out_request_id");

        path_handle as jlong
    } else {
        0
    }
}



#[no_mangle]
pub unsafe extern "system" fn Java_org_nothing_optimizationsandtweaks_OptimizationsAndTweaks_getAsyncExecutorStats(
    env: JNIEnv,
    _class: JClass,
) -> jintArray {
    init_panic_logging();
    
    let stats = async_executor::get_stats();
    let result = env.new_int_array(7).unwrap();
    env.set_int_array_region(&result, 0, &stats).unwrap();
    result.into_raw()
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_nothing_optimizationsandtweaks_OptimizationsAndTweaks_shutdownAsyncExecutor(
    _env: JNIEnv,
    _class: JClass,
) {
    init_panic_logging();
    async_executor::shutdown();
}

//

// =====================================================================================
// Profiler JNI Functions  
// =====================================================================================

/// Enable or disable the profiler
/// JNI signature: (Z)V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_setProfilerEnabled(
    _env: JNIEnv,
    _class: JClass,
    enabled: jboolean,
) {
    profiler::set_profiler_enabled(enabled != 0);
    log_native_line(format!("Profiler {}", if enabled != 0 { "enabled" } else { "disabled" }));
}

/// Check if profiler is enabled
/// JNI signature: ()Z
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_isProfilerEnabled(
    _env: JNIEnv,
    _class: JClass,
) -> jboolean {
    profiler::is_profiler_enabled() as jboolean
}

/// Clear profiler statistics
/// JNI signature: ()V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_clearProfilerStats(
    _env: JNIEnv,
    _class: JClass,
) {
    profiler::clear_stats();
    log_native_line("Profiler stats cleared");
}

/// Print profiler statistics to log
/// JNI signature: ()V
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_printProfilerStats(
    _env: JNIEnv,
    _class: JClass,
) {
    profiler::print_stats();
    profiler::MEMORY_STATS.print();
}

/// Get profiler statistics as a formatted string
/// JNI signature: ()Ljava/lang/String;
#[no_mangle]
pub extern "system" fn Java_fr_iamacat_optimizationsandtweaks_utils_natives_RustPathfinding_getProfilerStatsString(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    let stats = profiler::get_all_stats();
    
    let mut output = String::from("=== Pathfinding Profiler Statistics ===\n");
    
    let mut methods: Vec<_> = stats.iter().collect();
    methods.sort_by(|a, b| b.1.total_time_ns.cmp(&a.1.total_time_ns));
    
    for (method, stat) in methods {
        output.push_str(&format!(
            "{:<40} | Calls: {:>8} | Total: {:>10.2}ms | Avg: {:>8.2}µs | Min: {:>8.2}µs | Max: {:>8.2}µs\n",
            method,
            stat.call_count,
            stat.total_time_ns as f64 / 1_000_000.0,
            stat.avg_time_ns as f64 / 1_000.0,
            stat.min_time_ns as f64 / 1_000.0,
            stat.max_time_ns as f64 / 1_000.0
        ));
    }
    
    output.push_str("\n=== Memory Statistics ===\n");
    output.push_str(&format!("Active PathFinders: {}\n", profiler::MEMORY_STATS.pathfinder_count.load(std::sync::atomic::Ordering::Relaxed)));
    output.push_str(&format!("Active PathEntities: {}\n", profiler::MEMORY_STATS.path_entity_count.load(std::sync::atomic::Ordering::Relaxed)));
    output.push_str(&format!("Point Map Size: {}\n", profiler::MEMORY_STATS.point_map_size.load(std::sync::atomic::Ordering::Relaxed)));
    output.push_str(&format!("Visited Cache Size: {}\n", profiler::MEMORY_STATS.visited_cache_size.load(std::sync::atomic::Ordering::Relaxed)));
    
    let result = env.new_string(output).expect("Failed to create string");
    result.into_raw()
}


//
