package fr.iamacat.optimizationsandtweaks.utils.natives;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.IBlockAccess;

/**
 * Bridge class to convert between Minecraft pathfinding and Rust pathfinding
 * 
 */
public class RustPathfindingBridge {

    private static final Map<Integer, PathFinderEntry> PATHFINDER_CACHE = new ConcurrentHashMap<Integer, PathFinderEntry>();

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

    private static RustPathfinding.PathFinderHandle getOrCreatePathFinder(Entity entity, boolean isWoodenDoorAllowed,
        boolean isMovementBlockAllowed, boolean isPathingInWater, boolean canEntityDrown) {
        int key = entity.getEntityId();
        PathFinderEntry entry = PATHFINDER_CACHE.get(key);
        if (entry != null) {
            if (entry.woodenDoorAllowed == isWoodenDoorAllowed && entry.movementBlockAllowed == isMovementBlockAllowed
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
            isWoodenDoorAllowed,
            isMovementBlockAllowed,
            isPathingInWater,
            canEntityDrown);
        PATHFINDER_CACHE.put(
            key,
            new PathFinderEntry(
                newHandle,
                isWoodenDoorAllowed,
                isMovementBlockAllowed,
                isPathingInWater,
                canEntityDrown));
        return newHandle;
    }

    /**
     * Finds a path using Rust pathfinding with direct world access (no pre-encoding).
     * This method passes the world object directly to Rust, which queries blocks on-demand.
     * 
     * @param world                  The world
     * @param entity                 The entity
     * @param targetX                Target X coordinate
     * @param targetY                Target Y coordinate
     * @param targetZ                Target Z coordinate
     * @param maxDistance            Maximum pathfinding distance
     * @param isWoodenDoorAllowed    Whether wooden doors are passable
     * @param isMovementBlockAllowed Whether movement-blocking blocks are allowed
     * @param isPathingInWater       Whether pathfinding can occur in water
     * @param canEntityDrown         Whether the entity can drown
     * @return PathEntity or null if no path found
     */
    public static PathEntity findPathDirect(IBlockAccess world, Entity entity, double targetX, double targetY,
        double targetZ, float maxDistance, boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed,
        boolean isPathingInWater, boolean canEntityDrown) {

        if (!RustPathfinding.isAvailable()) {
            return null;
        }

        RustPathfinding.PathFinderHandle handleObj = getOrCreatePathFinder(
            entity,
            isWoodenDoorAllowed,
            isMovementBlockAllowed,
            isPathingInWater,
            canEntityDrown);
        long handle = handleObj.getHandle();

        float width = (float) entity.width;
        float height = (float) entity.height;

        // Create a world adapter that Rust can query directly
        WorldAccessAdapter adapter = new WorldAccessAdapter(world);

        // Call Rust pathfinding with direct world access (no pre-encoding)
        long pathEntityHandle = RustPathfinding.findPathDirectWorld(
            handle,
            adapter,
            entity.posX,
            entity.posY,
            entity.posZ,
            targetX,
            targetY,
            targetZ,
            width,
            height,
            maxDistance,
            entity.isInWater(),
            entity.getMaxSafePointTries());

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
            // Cache miss - query world and cache result
            byte blockType = encodeBlock(world, x, y, z);
            int metadata = 0;
            try {
                metadata = world.getBlockMetadata(x, y, z);
            } catch (Throwable ignore) {}

            return blockType;
        }

        /**
         * Called by Rust via JNI to get block metadata.
         * OPTIMIZED: Uses global shared cache.
         */
        public int getBlockMetadata(int x, int y, int z) {
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
                return world.getBlock(x, y, z)
                    .getMaterial()
                    .isOpaque() == false;
            } catch (Throwable t) {
                return false;
            }
        }
    }

    /**
     * Encode a region of blocks into a byte array for async pathfinding
     * This is used by AsyncPathfindingExecutor to prepare block data
     */
    public static byte[] encodeBlockCache(IBlockAccess world, int offsetX, int offsetY, int offsetZ, int width,
        int height, int depth) {
        byte[] cache = new byte[width * height * depth];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    int worldX = offsetX + x;
                    int worldY = offsetY + y;
                    int worldZ = offsetZ + z;

                    cache[index++] = encodeBlock(world, worldX, worldY, worldZ);
                }
            }
        }

        return cache;
    }

    private static byte encodeBlock(IBlockAccess world, int x, int y, int z) {
        try {
            net.minecraft.block.Block b = world.getBlock(x, y, z);
            int id = net.minecraft.block.Block.getIdFromBlock(b);
            int meta = 0;
            try {
                meta = world.getBlockMetadata(x, y, z);
            } catch (Throwable ignore) {}

            if (id == 0) return 0; // Air
            if (id == 8 || id == 9) return 2; // Water
            if (id == 10 || id == 11) return 3; // Lava

            // Doors
            if (id == 64 || id == 71) {
                int m = meta;
                if ((m & 0x8) != 0) { // upper half
                    try {
                        m = world.getBlockMetadata(x, y - 1, z);
                    } catch (Throwable ignore) {}
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
                case 53:
                case 67:
                case 108:
                case 109:
                case 114:
                case 128:
                case 134:
                case 135:
                case 136:
                case 156:
                case 163:
                case 164:
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
