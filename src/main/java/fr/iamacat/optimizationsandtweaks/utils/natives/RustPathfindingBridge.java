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

        return new PathEntity(points);
    }

    /**
     * Global block cache shared across all pathfinding operations.
     * Thread-safe LRU cache to prevent memory bloat.
     */
    private static class GlobalBlockCache {
        private final ConcurrentHashMap<Long, CachedBlock> cache;
        private final int maxSize;
        private volatile long accessCounter = 0;
        
        private static class CachedBlock {
            final byte blockType;
            final int metadata;
            volatile long lastAccess;
            
            CachedBlock(byte blockType, int metadata, long lastAccess) {
                this.blockType = blockType;
                this.metadata = metadata;
                this.lastAccess = lastAccess;
            }
        }
        
        public GlobalBlockCache(int maxSize) {
            this.maxSize = maxSize;
            this.cache = new ConcurrentHashMap<>(maxSize / 2);
        }
        
        private static long makeKey(int x, int y, int z) {
            // Pack coordinates into a long: x (21 bits) | y (11 bits) | z (21 bits)
            // Supports coordinates from -1M to +1M for x/z, -1024 to +1023 for y
            return ((long)(x & 0x1FFFFF) << 32) | ((long)(y & 0x7FF) << 21) | (long)(z & 0x1FFFFF);
        }
        
        public CachedBlock get(int x, int y, int z) {
            long key = makeKey(x, y, z);
            CachedBlock cached = cache.get(key);
            if (cached != null) {
                cached.lastAccess = ++accessCounter;
                return cached;
            }
            return null;
        }
        
        public void put(int x, int y, int z, byte blockType, int metadata) {
            // Evict old entries if cache is too large
            if (cache.size() >= maxSize) {
                evictOldEntries();
            }
            
            long key = makeKey(x, y, z);
            cache.put(key, new CachedBlock(blockType, metadata, ++accessCounter));
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
