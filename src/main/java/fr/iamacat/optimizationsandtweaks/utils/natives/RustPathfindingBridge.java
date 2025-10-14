package fr.iamacat.optimizationsandtweaks.utils.natives;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.IBlockAccess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bridge class to convert between Minecraft pathfinding and Rust pathfinding

 */
public class RustPathfindingBridge {
    private static final Map<Integer, PathFinderEntry> PATHFINDER_CACHE = new ConcurrentHashMap<Integer, PathFinderEntry>();
    
    private static final GlobalBlockCache GLOBAL_BLOCK_CACHE = new GlobalBlockCache(16384);
    
    // Path result cache to prevent redundant pathfinding calls
    private static final PathResultCache PATH_RESULT_CACHE = new PathResultCache(512);
    private static class PathFinderEntry {
        RustPathfinding.PathFinderHandle handle;
        boolean woodenDoorAllowed;
        boolean movementBlockAllowed;
        boolean pathingInWater;
        boolean canEntityDrown;

        PathFinderEntry(RustPathfinding.PathFinderHandle handle, boolean w, boolean m, boolean p, boolean d) {
            this.handle = handle;
            this.woodenDoorAllowed = w;
            this.movementBlockAllowed = m;
            this.pathingInWater = p;
            this.canEntityDrown = d;
        }
    }

    private static RustPathfinding.PathFinderHandle getOrCreatePathFinder(Entity entity,
        boolean isWoodenDoorAllowed,
        boolean isMovementBlockAllowed,
        boolean isPathingInWater,
        boolean canEntityDrown) {
        int key = entity.getEntityId();
        PathFinderEntry entry = PATHFINDER_CACHE.get(key);
        if (entry != null) {
            if (entry.woodenDoorAllowed == isWoodenDoorAllowed
                && entry.movementBlockAllowed == isMovementBlockAllowed
                && entry.pathingInWater == isPathingInWater
                && entry.canEntityDrown == canEntityDrown) {
                return entry.handle;
            }
            // Flags changed: replace the handle
            try {
                entry.handle.close();
            } catch (Throwable t) {
                // ignore
            }
        }
        RustPathfinding.PathFinderHandle newHandle = new RustPathfinding.PathFinderHandle(
            isWoodenDoorAllowed, isMovementBlockAllowed, isPathingInWater, canEntityDrown);
        PATHFINDER_CACHE.put(key, new PathFinderEntry(newHandle, isWoodenDoorAllowed, isMovementBlockAllowed,
            isPathingInWater, canEntityDrown));
        return newHandle;
    }
    /**
     * Finds a path using Rust pathfinding with direct world access (no pre-encoding).
     * This method passes the world object directly to Rust, which queries blocks on-demand.
     * 
     * @param world               The world
     * @param entity              The entity
     * @param targetX             Target X coordinate
     * @param targetY             Target Y coordinate
     * @param targetZ             Target Z coordinate
     * @param maxDistance         Maximum pathfinding distance
     * @param isWoodenDoorAllowed Whether wooden doors are passable
     * @param isMovementBlockAllowed Whether movement-blocking blocks are allowed
     * @param isPathingInWater    Whether pathfinding can occur in water
     * @param canEntityDrown      Whether the entity can drown
     * @return PathEntity or null if no path found
     */
    public static PathEntity findPathDirect(IBlockAccess world, Entity entity,
            double targetX, double targetY, double targetZ, float maxDistance,
            boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed,
            boolean isPathingInWater, boolean canEntityDrown) {

        if (!RustPathfinding.isAvailable()) {
            return null;
        }
        
        // OPTIMIZATION: Check path result cache first to avoid redundant pathfinding
        int entityId = entity.getEntityId();
        PathResultCache.CachedPathResult cachedResult = PATH_RESULT_CACHE.get(
            entityId, entity.posX, entity.posY, entity.posZ, targetX, targetY, targetZ);
        
        if (cachedResult != null) {
            // Cache hit! Return cached path without recalculating
            return cachedResult.path;
        }
        
        RustPathfinding.PathFinderHandle handleObj = getOrCreatePathFinder(
            entity, isWoodenDoorAllowed, isMovementBlockAllowed, isPathingInWater, canEntityDrown);
        long handle = handleObj.getHandle();

        float width = (float) entity.width;
        float height = (float) entity.height;

        // Create a world adapter that Rust can query directly
        WorldAccessAdapter adapter = new WorldAccessAdapter(world);

        // Call Rust pathfinding with direct world access (no pre-encoding)
        long pathEntityHandle = RustPathfinding.findPathDirectWorld(
            handle,
            adapter,
            entity.posX, entity.posY, entity.posZ,
            targetX, targetY, targetZ,
            width, height,
            maxDistance,
            entity.isInWater(),
            entity.getMaxSafePointTries()
        );
        
        if (pathEntityHandle == 0L) {
            return null;
        }

        PathPoint[] points;
        try (RustPathfinding.PathEntityHandle rustPath = new RustPathfinding.PathEntityHandle(pathEntityHandle)) {
            int[] allPoints = rustPath.getAllPoints();
            if (allPoints == null || allPoints.length == 0) {
                return null;
            }
            int numPoints = allPoints.length / 3;
            points = new PathPoint[numPoints];
            for (int i = 0; i < numPoints; i++) {
                int x = allPoints[i * 3];
                int y = allPoints[i * 3 + 1];
                int z = allPoints[i * 3 + 2];
                points[i] = new PathPoint(x, y, z);
            }
        }

        PathEntity result = new PathEntity(points);
        
        // OPTIMIZATION: Cache the result for future requests
        PATH_RESULT_CACHE.put(entityId, result, entity.posX, entity.posY, entity.posZ,
                             targetX, targetY, targetZ);
        
        return result;
    }

    /**
     * Path result cache to prevent redundant pathfinding calculations.
     * Caches recent pathfinding results and reuses them if entity/target haven't moved significantly.
     */
    private static class PathResultCache {
        private final ConcurrentHashMap<Integer, CachedPathResult> cache;
        private final int maxSize;
        private volatile long accessCounter = 0;
        
        private static class CachedPathResult {
            final PathEntity path;
            final double entityX, entityY, entityZ;
            final double targetX, targetY, targetZ;
            final long timestamp;
            volatile long lastAccess;
            
            CachedPathResult(PathEntity path, double entityX, double entityY, double entityZ,
                           double targetX, double targetY, double targetZ, long timestamp, long lastAccess) {
                this.path = path;
                this.entityX = entityX;
                this.entityY = entityY;
                this.entityZ = entityZ;
                this.targetX = targetX;
                this.targetY = targetY;
                this.targetZ = targetZ;
                this.timestamp = timestamp;
                this.lastAccess = lastAccess;
            }
            
            /**
             * Check if cached path is still valid given current positions.
             * Returns true if entity and target haven't moved significantly.
             */
            boolean isStillValid(double curEntityX, double curEntityY, double curEntityZ,
                               double curTargetX, double curTargetY, double curTargetZ,
                               long currentTime) {
                // Cache expires after 1 second (20 ticks)
                if (currentTime - timestamp > 1000) {
                    return false;
                }
                
                // Check if entity moved significantly (more than 0.5 blocks)
                double entityDist = distanceSquared(entityX, entityY, entityZ, curEntityX, curEntityY, curEntityZ);
                if (entityDist > 0.25) { // 0.5^2
                    return false;
                }
                
                // Check if target moved significantly (more than 1.0 blocks)
                double targetDist = distanceSquared(targetX, targetY, targetZ, curTargetX, curTargetY, curTargetZ);
                if (targetDist > 1.0) { // 1.0^2
                    return false;
                }
                
                return true;
            }
            
            private static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
                double dx = x2 - x1;
                double dy = y2 - y1;
                double dz = z2 - z1;
                return dx * dx + dy * dy + dz * dz;
            }
        }
        
        public PathResultCache(int maxSize) {
            this.maxSize = maxSize;
            this.cache = new ConcurrentHashMap<>(maxSize / 2);
        }
        
        public CachedPathResult get(int entityId, double entityX, double entityY, double entityZ,
                                   double targetX, double targetY, double targetZ) {
            CachedPathResult cached = cache.get(entityId);
            if (cached != null) {
                long currentTime = System.currentTimeMillis();
                if (cached.isStillValid(entityX, entityY, entityZ, targetX, targetY, targetZ, currentTime)) {
                    cached.lastAccess = ++accessCounter;
                    return cached;
                } else {
                    // Invalid cache entry, remove it
                    cache.remove(entityId);
                }
            }
            return null;
        }
        
        public void put(int entityId, PathEntity path, double entityX, double entityY, double entityZ,
                       double targetX, double targetY, double targetZ) {
            // Evict old entries if cache is too large
            if (cache.size() >= maxSize) {
                evictOldEntries();
            }
            
            long currentTime = System.currentTimeMillis();
            cache.put(entityId, new CachedPathResult(path, entityX, entityY, entityZ,
                                                     targetX, targetY, targetZ, currentTime, ++accessCounter));
        }
        
        private void evictOldEntries() {
            // Remove ~25% of least recently used entries
            int toRemove = maxSize / 4;
            long threshold = accessCounter - (maxSize * 2L);
            
            cache.entrySet().removeIf(entry -> {
                return entry.getValue().lastAccess < threshold && toRemove > 0;
            });
        }
        
        public void clear() {
            cache.clear();
            accessCounter = 0;
        }
        
        public void invalidate(int entityId) {
            cache.remove(entityId);
        }
        
        public int size() {
            return cache.size();
        }
    }
    
    /**
     * Global block cache shared across all pathfinding operations.
     * OPTIMIZED: Uses simple random eviction instead of expensive LRU traversal.
     */
    private static class GlobalBlockCache {
        private final ConcurrentHashMap<Long, CachedBlock> cache;
        private final int maxSize;
        private final int evictionThreshold;
        private volatile boolean isEvicting = false;
        
        private static class CachedBlock {
            final byte blockType;
            final int metadata;
            
            CachedBlock(byte blockType, int metadata) {
                this.blockType = blockType;
                this.metadata = metadata;
            }
        }
        
        public GlobalBlockCache(int maxSize) {
            this.maxSize = maxSize;
            // Start eviction when we reach 90% capacity
            this.evictionThreshold = (int)(maxSize * 0.9);
            this.cache = new ConcurrentHashMap<>(maxSize / 2);
        }
        
        private static long makeKey(int x, int y, int z) {
            // Pack coordinates into a long: x (21 bits) | y (11 bits) | z (21 bits)
            // Supports coordinates from -1M to +1M for x/z, -1024 to +1023 for y
            return ((long)(x & 0x1FFFFF) << 32) | ((long)(y & 0x7FF) << 21) | (long)(z & 0x1FFFFF);
        }
        
        public CachedBlock get(int x, int y, int z) {
            long key = makeKey(x, y, z);
            return cache.get(key);
        }
        
        public void put(int x, int y, int z, byte blockType, int metadata) {
            long key = makeKey(x, y, z);
            
            // Fast path: just insert if below threshold
            int currentSize = cache.size();
            if (currentSize < evictionThreshold) {
                cache.put(key, new CachedBlock(blockType, metadata));
                return;
            }
            
            // OPTIMIZATION: Use simple random eviction instead of expensive LRU
            // Only one thread should evict at a time
            if (currentSize >= maxSize && !isEvicting) {
                if (tryEvictRandomEntries()) {
                    // Eviction successful, now insert
                    cache.put(key, new CachedBlock(blockType, metadata));
                } else {
                    // Another thread is evicting, just insert anyway (may exceed maxSize temporarily)
                    cache.put(key, new CachedBlock(blockType, metadata));
                }
            } else {
                cache.put(key, new CachedBlock(blockType, metadata));
            }
        }
        
        /**
         * OPTIMIZED: Fast random eviction instead of expensive LRU traversal.
         * Removes ~10% of entries randomly, which is O(n/10) instead of O(n) for full scan.
         */
        private boolean tryEvictRandomEntries() {
            // Try to acquire eviction lock
            if (!isEvicting) {
                synchronized (this) {
                    if (isEvicting) {
                        return false;
                    }
                    isEvicting = true;
                }
                
                try {
                    int targetRemove = maxSize / 10; // Remove 10% of entries
                    int removed = 0;
                    int checked = 0;
                    int maxCheck = maxSize / 5; // Check at most 20% of entries
                    
                    // Use iterator for efficient removal
                    var iterator = cache.entrySet().iterator();
                    while (iterator.hasNext() && removed < targetRemove && checked < maxCheck) {
                        iterator.next();
                        checked++;
                        
                        // Remove every other entry we check (50% probability)
                        if ((checked & 1) == 0) {
                            iterator.remove();
                            removed++;
                        }
                    }
                    
                    return true;
                } finally {
                    isEvicting = false;
                }
            }
            return false;
        }
        
        public void clear() {
            cache.clear();
        }
        
        public int size() {
            return cache.size();
        }
    }
    
    /**
     * Clear the global block cache. Should be called when world changes or periodically.
     */
    public static void clearGlobalBlockCache() {
        GLOBAL_BLOCK_CACHE.clear();
    }
    
    /**
     * Invalidate a specific block position in the cache.
     * Should be called when a block is placed, broken, or modified.
     */
    public static void invalidateBlockCache(int x, int y, int z) {
        long key = GlobalBlockCache.makeKey(x, y, z);
        GLOBAL_BLOCK_CACHE.cache.remove(key);
    }
    
    /**
     * Invalidate a region of blocks in the cache.
     * Useful for chunk updates or large block changes.
     */
    public static void invalidateBlockCacheRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    invalidateBlockCache(x, y, z);
                }
            }
        }
    }
    
    /**
     * Get global block cache statistics for monitoring.
     */
    public static int getGlobalBlockCacheSize() {
        return GLOBAL_BLOCK_CACHE.size();
    }
    
    /**
     * Clear the path result cache. Should be called when entities are removed or world changes.
     */
    public static void clearPathResultCache() {
        PATH_RESULT_CACHE.clear();
    }
    
    /**
     * Invalidate a specific entity's cached path.
     * Should be called when an entity's pathfinding parameters change.
     */
    public static void invalidateEntityPathCache(int entityId) {
        PATH_RESULT_CACHE.invalidate(entityId);
    }
    
    /**
     * Get path result cache statistics for monitoring.
     */
    public static int getPathResultCacheSize() {
        return PATH_RESULT_CACHE.size();
    }

    /**
     * World access adapter that Rust can query via JNI.
     * This allows Rust to encode blocks on-demand during pathfinding.
     * Now uses a global shared cache to avoid redundant queries.
     */
    public static class WorldAccessAdapter {
        private final IBlockAccess world;

        public WorldAccessAdapter(IBlockAccess world) {
            this.world = world;
        }

        /**
         * Called by Rust via JNI to get block type code.
         * Returns: 0=Air,1=Solid,2=Water,3=Lava,4=WoodenDoor,5=Trapdoor,6=Fence,7=FenceGate,etc.
         * OPTIMIZED: Uses global shared cache to avoid redundant world queries.
         */
        public byte getBlockTypeCode(int x, int y, int z) {
            // Check global cache first
            GlobalBlockCache.CachedBlock cached = GLOBAL_BLOCK_CACHE.get(x, y, z);
            if (cached != null) {
                return cached.blockType;
            }
            
            // Cache miss - query world and cache result
            byte blockType = encodeBlock(world, x, y, z);
            int metadata = 0;
            try {
                metadata = world.getBlockMetadata(x, y, z);
            } catch (Throwable ignore) {}
            
            GLOBAL_BLOCK_CACHE.put(x, y, z, blockType, metadata);
            return blockType;
        }

        /**
         * Called by Rust via JNI to get block metadata.
         * OPTIMIZED: Uses global shared cache.
         */
        public int getBlockMetadata(int x, int y, int z) {
            // Check global cache first
            GlobalBlockCache.CachedBlock cached = GLOBAL_BLOCK_CACHE.get(x, y, z);
            if (cached != null) {
                return cached.metadata;
            }
            
            // Cache miss - query world
            try {
                return world.getBlockMetadata(x, y, z);
            } catch (Throwable t) {
                return 0;
            }
        }

        /**
         * Called by Rust via JNI to check if block can see sky.
         */
        public boolean canBlockSeeSky(int x, int y, int z) {
            try {
                return world.getBlock(x, y, z).getMaterial().isOpaque() == false;
            } catch (Throwable t) {
                return false;
            }
        }
    }

    private static byte encodeBlock(IBlockAccess world, int x, int y, int z) {
        try {
            net.minecraft.block.Block b = world.getBlock(x, y, z);
            int id = net.minecraft.block.Block.getIdFromBlock(b);
            int meta = 0;
            try { meta = world.getBlockMetadata(x, y, z); } catch (Throwable ignore) {}

            if (id == 0) return 0; // Air
            if (id == 8 || id == 9) return 2; // Water
            if (id == 10 || id == 11) return 3; // Lava

            // Doors
            if (id == 64 || id == 71) {
                int m = meta;
                if ((m & 0x8) != 0) { // upper half
                    try { m = world.getBlockMetadata(x, y - 1, z); } catch (Throwable ignore) {}
                }
                boolean open = (m & 0x4) != 0;
                return open ? (byte) 0 : (byte) 4;
            }

            // Trapdoor
            if (id == 96) {
                boolean open = (meta & 0x4) != 0;
                return open ? (byte) 0 : (byte) 5;
            }

            // Fences
            if (id == 85 || id == 113 || (id >= 188 && id <= 192)) return 6;

            // FenceGate
            if (id == 107) {
                boolean open = (meta & 0x4) != 0;
                return open ? (byte) 0 : (byte) 7;
            }

            // Slabs & stairs
            if (id == 44 || id == 126) return 0;
            switch (id) {
                case 53: case 67: case 108: case 109: case 114: case 128:
                case 134: case 135: case 136: case 156: case 163: case 164:
                    return 0;
                default:
            }

            // Slime, Vine, Ladder, Cobweb
            if (id == 165) return 8; // Slime Block
            if (id == 106) return 9; // Vine
            if (id == 65) return 10; // Ladder
            if (id == 30) return 11; // Cobweb

            // Default solid check
            try {
                net.minecraft.block.material.Material m = b.getMaterial();
                boolean solid = m != null && m.blocksMovement();
                return solid ? (byte) 1 : (byte) 0;
            } catch (Throwable t) {
                return 0;
            }
        } catch (Throwable t) {
            return 0;
        }
    }
}
