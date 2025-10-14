use super::{Path, PathEntity, PathPoint};
use crate::log_native_line;
use std::collections::{HashMap, HashSet};
use std::collections::hash_map::Entry;

/// Trait for accessing block data in the world
/// This abstracts the world access to make the pathfinder reusable
pub trait IBlockAccess {
    /// Gets the block type at the given coordinates
    fn get_block(&self, x: i32, y: i32, z: i32) -> BlockType;
    
    /// Gets the block metadata at the given coordinates
    fn get_block_metadata(&self, x: i32, y: i32, z: i32) -> i32;
    
    /// Checks if a block can see the sky
    fn can_block_see_sky(&self, x: i32, y: i32, z: i32) -> bool;
}

/// block type enum for pathfinding
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum BlockType {
    Air,
    Water,
    FlowingWater,
    Lava,
    FlowingLava,
    WoodenDoor,
    Trapdoor,
    Fence,
    FenceGate,
    Slime,
    Vine,
    Ladder,
    Cobweb,
    Solid,
    NonSolid,
}

impl BlockType {
    /// Returns true if this block type blocks movement
    pub fn blocks_movement(&self) -> bool {
        matches!(
            self,
            BlockType::Solid | BlockType::WoodenDoor | BlockType::Fence | BlockType::FenceGate
        )
    }

    /// Returns true if this is a liquid block
    pub fn is_liquid(&self) -> bool {
        matches!(
            self,
            BlockType::Water | BlockType::FlowingWater | BlockType::Lava | BlockType::FlowingLava
        )
    }
}

/// Entity data needed for pathfinding
pub struct EntityData {
    pub pos_x: f64,
    pub pos_y: f64,
    pub pos_z: f64,
    pub width: f32,
    pub height: f32,
    pub is_in_water: bool,
    pub max_safe_point_tries: i32,
    /// Maximum number of whole blocks the entity can jump/step up
    pub max_jump_height: i32,
}

/// PathFinder implementation - finds paths through the world
pub struct PathFinder {
    /// The path being generated (priority queue)
    path: Path,
    /// Map of coordinates to path points
    point_map: HashMap<i32, PathPoint>,
    /// Selection of path options stored as hashes to avoid duplicating PathPoint structs
    path_options: [Option<i32>; 32],
    /// Should the PathFinder go through wooden door blocks
    is_wooden_door_allowed: bool,
    /// Should the PathFinder disregard BlockMovement type materials in its path
    is_movement_block_allowed: bool,
    /// Is the pathfinder currently pathing in water
    is_pathing_in_water: bool,
    /// Tells the PathFinder to not stop pathing underwater
    can_entity_drown: bool,
    /// Debug mode: when enabled, if no path is found, return a direct path to target
    debug_always_reach: bool,
    /// Cache for visited nodes to avoid reprocessing
    visited_cache: HashSet<i32>,
}

impl PathFinder {
    /// Creates a new PathFinder
    pub fn new(
        is_wooden_door_allowed: bool,
        is_movement_block_allowed: bool,
        is_pathing_in_water: bool,
        can_entity_drown: bool,
    ) -> Self {
        PathFinder {
            path: Path::new(),
            point_map: HashMap::new(),
            path_options: [None; 32],
            is_wooden_door_allowed,
            is_movement_block_allowed,
            is_pathing_in_water,
            can_entity_drown,
            debug_always_reach: false,
            visited_cache: HashSet::with_capacity(2048),
        }
    }

    /// Creates a path from one entity to a target position
    pub fn create_entity_path_to<W: IBlockAccess>(
        &mut self,
        world: &W,
        entity: &EntityData,
        target_x: f64,
        target_y: f64,
        target_z: f64,
        max_distance: f32,
    ) -> Option<PathEntity> {
        self.path.clear_path();
        self.point_map.clear();
        self.visited_cache.clear();
        // Note: Block caching is now handled at the Java level (global shared cache)

        let mut start_y = (entity.pos_y + 0.5).floor() as i32;

        // Check the target is in the water or just above the water (to enter without jumping)
        let tbx = (target_x - (entity.width / 2.0) as f64).floor() as i32;
        let tby = target_y.floor() as i32;
        let tbz = (target_z - (entity.width / 2.0) as f64).floor() as i32;
        let target_block = world.get_block(tbx, tby, tbz);
        let below_block = world.get_block(tbx, tby - 1, tbz);
                if matches!(target_block, BlockType::Water | BlockType::FlowingWater)
            || matches!(below_block, BlockType::Water | BlockType::FlowingWater)
        {
            self.is_pathing_in_water = true;
        }
        // If the entity is already in water, activate the water mode for this search
        if entity.is_in_water {
            self.is_pathing_in_water = true;
        }

        // Adjustment if the entity is in water
        if self.can_entity_drown && entity.is_in_water {
            start_y = entity.pos_y.floor() as i32;

            // Find the surface of the water
            loop {
                let block = world.get_block(
                    entity.pos_x.floor() as i32,
                    start_y,
                    entity.pos_z.floor() as i32,
                );

                if block != BlockType::FlowingWater && block != BlockType::Water {
                    break;
                }
                start_y += 1;
            }

            self.is_pathing_in_water = true;
        }

        let start_point = self.open_point(
            entity.pos_x.floor() as i32,
            start_y,
            entity.pos_z.floor() as i32,
        );

        let end_point = self.open_point(
            (target_x - (entity.width / 2.0) as f64).floor() as i32,
            target_y.floor() as i32,
            (target_z - (entity.width / 2.0) as f64).floor() as i32,
        );

        let size_point = PathPoint::new(
            (entity.width + 1.0).floor() as i32,
            (entity.height + 1.0).floor() as i32,
            (entity.width + 1.0).floor() as i32,
        );

        self.add_to_path(world, entity, start_point, end_point, size_point, max_distance)
    }

    /// Internal pathfinding algorithm (A* implementation) - Optimized
    fn add_to_path<W: IBlockAccess>(
        &mut self,
        world: &W,
        entity: &EntityData,
        mut start: PathPoint,
        end: PathPoint,
        size: PathPoint,
        max_distance: f32,
    ) -> Option<PathEntity> {
        start.total_path_distance = 0.0;
        start.distance_to_next = start.distance_to(&end);
        start.distance_to_target = start.distance_to_next;

        self.path.clear_path();

        // Insert start point
        self.point_map.insert(start.hash, start);
        
        let idx = {
            let mut getd = |h: i32| self
                .point_map
                .get(&h)
                .map(|p| p.distance_to_target)
                .unwrap_or(f32::INFINITY);
            self.path.add_point_hash(start.hash, &mut getd).expect("enqueue start")
        };
        
        self.point_map.get_mut(&start.hash).unwrap().index = idx as i32;
        start.index = idx as i32;

        let mut closest_hash = start.hash;
        let mut closest_distance = start.distance_to(&end);

        // Early exit if already at goal
        if closest_distance < 1.0 {
            let path = self.create_entity_path(&start, &start);
            self.point_map.clear();
            self.point_map.shrink_to_fit();
            self.path.clear_and_shrink();
            return Some(path);
        }

        // Reduced max iterations for better performance
        let max_iterations = 1500;
        let mut iterations = 0;
        let mut last_improve_iter = 0usize;
        
        // OPTIMIZED: Main pathfinding loop with caching and early exits
        while !self.path.is_path_empty() && iterations < max_iterations {
            iterations += 1;

            let cur_hash = match self.path.dequeue(|h| {
                self.point_map
                    .get(&h)
                    .map(|p| p.distance_to_target)
                    .unwrap_or(f32::INFINITY)
            }) {
                Some(h) => h,
                None => break,
            };
            
            // OPTIMIZATION: Skip if already visited (prevents reprocessing)
            if self.visited_cache.contains(&cur_hash) {
                continue;
            }
            self.visited_cache.insert(cur_hash);
            
            // Mark as visited in point_map
            self.point_map.get_mut(&cur_hash).unwrap().index = -1;
            
            // Get current point data (avoid cloning)
            let (current_x, current_y, current_z, current_dist, dist_to_end);
            {
                let current = self.point_map.get(&cur_hash).unwrap();
                current_x = current.x_coord;
                current_y = current.y_coord;
                current_z = current.z_coord;
                current_dist = current.total_path_distance;
                dist_to_end = current.distance_to(&end);
            }

            // Check if we reached the goal
            let goal_eps: f32 = if self.is_pathing_in_water { 1.5 } else { 1.0 };
            if dist_to_end < goal_eps {
                let current = *self.point_map.get(&cur_hash).unwrap();
                let path = self.create_entity_path(&start, &current);
                self.point_map.clear();
                self.point_map.shrink_to_fit();
                self.path.clear_and_shrink();
                return Some(path);
            }

            // Track closest point
            let improve_eps: f32 = if self.is_pathing_in_water { 0.25 } else { 0.5 };
            if dist_to_end + improve_eps < closest_distance {
                closest_distance = dist_to_end;
                closest_hash = cur_hash;
                last_improve_iter = iterations;
            }

            // Mark as visited
            self.point_map.get_mut(&cur_hash).unwrap().is_first = true;

            // Skip if too far
            if current_dist > max_distance * 2.0 {
                continue;
            }

            // Early termination if no improvement
            if iterations.saturating_sub(last_improve_iter) > 100 && !self.is_pathing_in_water {
                break;
            }

            // Find neighbors - need to clone current to avoid borrow checker issues
            let current_for_options = *self.point_map.get(&cur_hash).unwrap();
            let options_count =
                self.find_path_options(world, entity, &current_for_options, &size, &end, max_distance);

            // Process neighbors
            for i in 0..options_count {
                if let Some(neighbor_hash) = self.path_options[i] {
                    // OPTIMIZATION: Skip already visited neighbors
                    if self.visited_cache.contains(&neighbor_hash) {
                        continue;
                    }
                    
                    let (neighbor_x, neighbor_y, neighbor_z, neighbor_assigned, old_dist);
                    {
                        let neighbor = self.point_map.get(&neighbor_hash).unwrap();
                        neighbor_x = neighbor.x_coord;
                        neighbor_y = neighbor.y_coord;
                        neighbor_z = neighbor.z_coord;
                        neighbor_assigned = neighbor.is_assigned();
                        old_dist = neighbor.total_path_distance;
                    }

                    // OPTIMIZATION: Use cached distance calculation
                    let dx = (current_x - neighbor_x) as f32;
                    let dy = (current_y - neighbor_y) as f32;
                    let dz = (current_z - neighbor_z) as f32;
                    let step_dist = (dx * dx + dy * dy + dz * dz).sqrt();
                    
                    let mut new_distance = current_dist + step_dist;

                    // Penalize water tiles if not in water
                    // Note: Block queries are cached at Java level (global shared cache)
                    if !self.is_pathing_in_water {
                        let neighbor_block = world.get_block(neighbor_x, neighbor_y, neighbor_z);
                        if matches!(neighbor_block, BlockType::Water | BlockType::FlowingWater) {
                            new_distance += 10.0;
                        }
                    }

                    if new_distance > max_distance {
                        continue;
                    }

                    let should_update = !neighbor_assigned || new_distance < old_dist;

                    if should_update {
                        // Update neighbor
                        let neighbor = self.point_map.get_mut(&neighbor_hash).unwrap();
                        neighbor.previous_hash = Some(cur_hash);
                        neighbor.total_path_distance = new_distance;
                        neighbor.distance_to_next = neighbor.distance_to(&end);
                        neighbor.distance_to_target = neighbor.total_path_distance + neighbor.distance_to_next;

                        if neighbor_assigned {
                            let mut getd = |h: i32| self
                                .point_map
                                .get(&h)
                                .map(|p| p.distance_to_target)
                                .unwrap_or(f32::INFINITY);
                            let _ = self.path.reheapify_by_hash(neighbor_hash, &mut getd);
                        } else {
                            let mut getd = |h: i32| self
                                .point_map
                                .get(&h)
                                .map(|p| p.distance_to_target)
                                .unwrap_or(f32::INFINITY);
                            if let Ok(idx) = self.path.add_point_hash(neighbor_hash, &mut getd) {
                                self.point_map.get_mut(&neighbor_hash).unwrap().index = idx as i32;
                            }
                        }
                    }
                }
            }
        }

        let closest_point = *self.point_map.get(&closest_hash).unwrap();
        let path = self.create_entity_path(&start, &closest_point);
        let path_len = path.len();
        let reach_tolerance: f32 = if self.is_pathing_in_water { 2.5 } else { 1.0 };

        if closest_distance > reach_tolerance && path_len <= 1 {
            if self.debug_always_reach {
                let direct = PathEntity::new(vec![
                    PathPoint::new(start.x_coord, start.y_coord, start.z_coord),
                    PathPoint::new(end.x_coord, end.y_coord, end.z_coord),
                ]);
                self.point_map.clear();
                self.point_map.shrink_to_fit();
                self.path.clear_and_shrink();
                return Some(direct);
            }
            self.point_map.clear();
            self.point_map.shrink_to_fit();
            self.path.clear_and_shrink();
            return None;
        }

        self.point_map.clear();
        self.point_map.shrink_to_fit();
        self.path.clear_and_shrink();
        Some(path)
    }

    /// Finds available neighboring path options
    fn find_path_options<W: IBlockAccess>(
        &mut self,
        world: &W,
        entity: &EntityData,
        current: &PathPoint,
        size: &PathPoint,
        target: &PathPoint,
        max_distance: f32,
    ) -> usize {
        let mut count = 0;
        let mut vertical_offset = 0;

        // Check if we can move up
        let up_check = self.get_vertical_offset(world, entity, current.x_coord, current.y_coord + 1, current.z_coord, size);
        if up_check == 1 && !self.is_pathing_in_water {
            vertical_offset = 1;
        }

        // Check all four cardinal directions
        let directions = [
            (0, 0, 1),   // +Z
            (-1, 0, 0),  // -X
            (1, 0, 0),   // +X
            (0, 0, -1),  // -Z
        ];
        for (dx, dy, dz) in directions.iter() {
            // --- Safety check : avoid dangerous diagonal jumps into the void ---
            let below_front_x = current.x_coord + dx;
            let mut below_front_y = current.y_coord - 1;
            let below_front_z = current.z_coord + dz;

            // Block queries are cached at Java level (global shared cache)
            let front_block = world.get_block(below_front_x, current.y_coord, below_front_z);
            let below_front_block = world.get_block(below_front_x, below_front_y, below_front_z);

            // Search for a support block to a small depth
            let mut safe_to_fall = false;
            for depth in 1..=3 {
                let block_below = world.get_block(below_front_x, current.y_coord - depth, below_front_z);
                match block_below {
                    // If we find a block that cushions or stops the fall → authorized
                    BlockType::Water
                    | BlockType::FlowingWater
                    | BlockType::Slime
                    | BlockType::Vine 
                    | BlockType::Ladder
                    | BlockType::Cobweb 
                    | BlockType::Solid => {
                        safe_to_fall = true;
                        break;
                    }
                    BlockType::Air | BlockType::NonSolid => continue,
                    _ => {}
                }
            }
            
            // If in front it is empty and there is no safe support in the 3 blocks below → block
            if matches!(front_block, BlockType::Air | BlockType::NonSolid) && !safe_to_fall {
                continue;
            }

            let nx = current.x_coord + dx;
            let ny = current.y_coord + dy;
            let nz = current.z_coord + dz;

            if let Some(mut point) = self.get_safe_point(
                world,
                entity,
                nx,
                ny,
                nz,
                size,
                vertical_offset,
            ) {
                if !point.is_first && point.distance_to(target) < max_distance {
                    if count < self.path_options.len() {
                        self.path_options[count] = Some(point.hash);
                        count += 1;
                    }
                }
            } else {
                // Direct neighbor blocked; try stepping/jumping up to max_jump_height
                // Disallow stepping over fences or closed fence gates: route around instead
                let base_block = world.get_block(nx, ny, nz);
                let is_closed_gate = matches!(base_block, BlockType::FenceGate) && !self.is_fence_gate_open(world, nx, ny, nz);
                if matches!(base_block, BlockType::Fence) || is_closed_gate {
                    continue;
                }
                let mut accepted = false;
                let max_step = entity.max_jump_height.max(0);
                'outer: for step in 1..=max_step {
                    let y2 = ny + step;

                    // Ensure headroom is clear at each intermediate level
                    for k in 1..=step {
                        let ck = self.get_vertical_offset(world, entity, nx, ny + k, nz, size);
                        // Blocked volumes or lava/fence prevent stepping
                        if ck == 0 || ck == -2 || ck == -3 {
                            continue 'outer;
                        }
                    }

                    // Final landing check: clear volume (1) or standable (2)
                    let top_check = self.get_vertical_offset(world, entity, nx, y2, nz, size);
                    // Prevent stepping into water; only allow exiting water (landing onto non-water)
                    let block_at_top = world.get_block(nx, y2, nz);

                    if (top_check == 1 || top_check == 2)
                        && !matches!(block_at_top, BlockType::Water | BlockType::FlowingWater)
                    {
                        let candidate = self.open_point(nx, y2, nz);
                        if !candidate.is_first && candidate.distance_to(target) < max_distance {
                            if count < self.path_options.len() {
                                self.path_options[count] = Some(candidate.hash);
                                count += 1;
                                accepted = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        count
    }

    /// Returns a point that the entity can safely move to
    fn get_safe_point<W: IBlockAccess>(
        &mut self,
        world: &W,
        entity: &EntityData,
        x: i32,
        mut y: i32,
        z: i32,
        size: &PathPoint,
        vertical_offset: i32,
    ) -> Option<PathPoint> {
        let vertical_check = self.get_vertical_offset(world, entity, x, y, z, size);

        // Can stand here
        if vertical_check == 2 {
            return Some(self.open_point(x, y, z));
        }

        let mut result = if vertical_check == 1 {
            Some(self.open_point(x, y, z))
        } else {
            None
        };

        // Try moving up if allowed
        if result.is_none() && vertical_offset > 0 && vertical_check != -3 && vertical_check != -4 {
            if self.get_vertical_offset(world, entity, x, y + vertical_offset, z, size) == 1 {
                result = Some(self.open_point(x, y + vertical_offset, z));
                y += vertical_offset;
            }
        }

        // Try moving down to find solid ground
        if let Some(_) = result {
            let mut tries = 0;
            let mut fall_distance = 0;

            while y > 0 {
                let check = self.get_vertical_offset(world, entity, x, y - 1, z, size);

                // Never path into flowing/standing water when not already in water mode
                if !self.is_pathing_in_water && check == -1 {
                    return None;
                }

                tries += 1;
                if tries >= entity.max_safe_point_tries {
                    return None;
                }

                if self.is_pathing_in_water {
                    if check == 2 {
                        // Get out of the water to land on solid
                        y -= 1;
                        result = Some(self.open_point(x, y, z));
                    }
                    // In water: do not continue to descend through volumes of water
                    break;
                }

                if check == 1 {
                    // Free space but not supported below: track fall distance.
                    fall_distance += 1;
                    // Emulate vanilla: do not step into ledges if drop is more than one block.
                    if fall_distance > 1 {
                        return None;
                    }
                    y -= 1;
                    result = Some(self.open_point(x, y, z));
                    continue;
                } else if check == 2 {
                    // Found a solid surface directly below.
                    // If we already fell more than one block, abort to avoid cliffing.
                    if fall_distance > 1 {
                        return None;
                    }
                    y -= 1;
                    result = Some(self.open_point(x, y, z));
                    break;
                } else {
                    // Blocked, lava, barrier, etc.
                    break;
                }
            }

            let final_check = self.get_vertical_offset(world, entity, x, y, z, size);
            if final_check == -2 {
                return None;
            }
        }

        result
    }

    /// Gets or creates a path point at the given coordinates - Optimized to avoid cloning
    fn open_point(&mut self, x: i32, y: i32, z: i32) -> PathPoint {
        let hash = PathPoint::make_hash(x, y, z);

        match self.point_map.entry(hash) {
            // Return existing canonical entry; repair in-place without allocating if inconsistent
            Entry::Occupied(mut entry) => {
                let p = entry.get_mut();
                if p.hash != hash || p.x_coord != x || p.y_coord != y || p.z_coord != z {
                    // In-place repair: overwrite fields to the canonical coordinates and hash
                    *p = PathPoint::new(x, y, z);
                }
                *p
            }
            // Insert only when truly absent
            Entry::Vacant(entry) => {
                let p = PathPoint::new(x, y, z);
                entry.insert(p);
                p
            }
        }
    }

    /// Checks vertical offset for pathfinding
    /// Returns: 2 = clear and standable (solid surface directly below), 1 = clear but not standable, 0 = blocked by solid in volume, -1 = water (if avoiding), -2 = lava, -3 = fence
    fn get_vertical_offset<W: IBlockAccess>(
        &self,
        world: &W,
        _entity: &EntityData,
        x: i32,
        y: i32,
        z: i32,
        size: &PathPoint,
    ) -> i32 {
        for dx in 0..size.x_coord {
            for dy in 0..size.y_coord {
                for dz in 0..size.z_coord {
                    let bx = x + dx;
                    let by = y + dy;
                    let bz = z + dz;
                    match world.get_block(bx, by, bz) {
                        BlockType::Air | BlockType::NonSolid
                        | BlockType::Slime | BlockType::Vine | BlockType::Ladder | BlockType::Cobweb => {
                            // volume clear / safe to fall
                        }
                        BlockType::Trapdoor => {
                            if !self.is_pathing_in_water {
                                return -1;
                            }
                        }
                        BlockType::Water | BlockType::FlowingWater => {
                            if !self.is_pathing_in_water {
                                return -1;
                            }
                            return 1;
                        }
                        BlockType::WoodenDoor => {
                            if !self.is_wooden_door_allowed {
                                return 0;
                            }
                        }
                        BlockType::Fence => return -3,
                        BlockType::FenceGate => {
                            if self.is_fence_gate_open(world, bx, by, bz) {
                                // open gate: treat as clear space
                            } else {
                                return -3;
                            }
                        },
                        BlockType::Lava | BlockType::FlowingLava => return -2,
                        BlockType::Solid => return 0,
                    }
                }
            }
        }

        // Check the ground under the entity
        let below_y = y - 1;
        for dx in 0..size.x_coord {
            for dz in 0..size.z_coord {
                let b = world.get_block(x + dx, below_y, z + dz);
                if matches!(b, BlockType::Solid) {
                    return 2;
                }
            }
        }
        1
    }

    fn is_fence_gate_open<W: IBlockAccess>(&self, world: &W, x: i32, y: i32, z: i32) -> bool {
        // Minecraft 1.7.10: fence gate open state uses bit 0x4 in metadata
        (world.get_block_metadata(x, y, z) & 0x4) != 0
    }



    /// Creates a PathEntity by backtracking from end to start
    fn create_entity_path(&self, start: &PathPoint, end: &PathPoint) -> PathEntity {
        let mut points = Vec::new();
        let mut current = PathPoint {
            x_coord: end.x_coord,
            y_coord: end.y_coord,
            z_coord: end.z_coord,
            hash: end.hash,
            index: end.index,
            total_path_distance: end.total_path_distance,
            distance_to_next: end.distance_to_next,
            distance_to_target: end.distance_to_target,
            previous_hash: end.previous_hash,
            is_first: end.is_first,
        };

        // Backtrack from end to start using stable hashes
        loop {
            points.push(PathPoint {
                x_coord: current.x_coord,
                y_coord: current.y_coord,
                z_coord: current.z_coord,
                hash: current.hash,
                index: current.index,
                total_path_distance: current.total_path_distance,
                distance_to_next: current.distance_to_next,
                distance_to_target: current.distance_to_target,
                previous_hash: current.previous_hash,
                is_first: current.is_first,
            });

            if current == *start {
                break;
            }

            if let Some(prev_hash) = current.previous_hash {
                if let Some(prev_point) = self.point_map.get(&prev_hash) {
                    current = PathPoint {
                        x_coord: prev_point.x_coord,
                        y_coord: prev_point.y_coord,
                        z_coord: prev_point.z_coord,
                        hash: prev_point.hash,
                        index: prev_point.index,
                        total_path_distance: prev_point.total_path_distance,
                        distance_to_next: prev_point.distance_to_next,
                        distance_to_target: prev_point.distance_to_target,
                        previous_hash: prev_point.previous_hash,
                        is_first: prev_point.is_first,
                    };
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        // Reverse to get start -> end order
        points.reverse();
        PathEntity::new(points)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    struct TestWorld;
    
    impl IBlockAccess for TestWorld {
        fn get_block(&self, _x: i32, y: i32, _z: i32) -> BlockType {
            if y < 0 {
                BlockType::Solid
            } else if y == 0 {
                BlockType::Solid // Ground
            } else {
                BlockType::Air
            }
        }
        
        fn get_block_metadata(&self, _x: i32, _y: i32, _z: i32) -> i32 {
            0
        }
        
        fn can_block_see_sky(&self, _x: i32, y: i32, _z: i32) -> bool {
            y > 0
        }
    }

    // World with a solid block at y=1 to test vertical offset semantics
    struct SolidAtY1World;
    impl IBlockAccess for SolidAtY1World {
        fn get_block(&self, _x: i32, y: i32, _z: i32) -> BlockType {
            if y == 1 { BlockType::Solid } else if y <= 0 { BlockType::Solid } else { BlockType::Air }
        }
        fn get_block_metadata(&self, _x: i32, _y: i32, _z: i32) -> i32 { 0 }
        fn can_block_see_sky(&self, _x: i32, y: i32, _z: i32) -> bool { y > 0 }
    }
}
