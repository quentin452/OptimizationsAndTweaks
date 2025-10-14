package fr.iamacat.optimizationsandtweaks.utils.natives;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch pathfinding helper for processing multiple pathfinding requests in parallel
 * This provides significant performance improvements when many entities need pathfinding
 */
public class RustPathfindingBatch {

    /**
     * Represents a single pathfinding request
     */
    public static class PathfindingRequest {
        public final Entity entity;
        public final double targetX;
        public final double targetY;
        public final double targetZ;
        public final float maxDistance;
        public final boolean isWoodenDoorAllowed;
        public final boolean isMovementBlockAllowed;
        public final boolean isPathingInWater;
        public final boolean canEntityDrown;

        public PathfindingRequest(Entity entity, double targetX, double targetY, double targetZ, 
                                 float maxDistance, boolean isWoodenDoorAllowed, 
                                 boolean isMovementBlockAllowed, boolean isPathingInWater, 
                                 boolean canEntityDrown) {
            this.entity = entity;
            this.targetX = targetX;
            this.targetY = targetY;
            this.targetZ = targetZ;
            this.maxDistance = maxDistance;
            this.isWoodenDoorAllowed = isWoodenDoorAllowed;
            this.isMovementBlockAllowed = isMovementBlockAllowed;
            this.isPathingInWater = isPathingInWater;
            this.canEntityDrown = canEntityDrown;
        }
    }

    /**
     * Process multiple pathfinding requests in parallel
     * 
     * @param world The world to pathfind in
     * @param requests List of pathfinding requests
     * @return List of PathEntity results (null if no path found)
     */
    public static List<PathEntity> findPathsBatch(IBlockAccess world, List<PathfindingRequest> requests) {
        if (!RustPathfinding.isAvailable() || requests.isEmpty()) {
            return new ArrayList<>();
        }

        int count = requests.size();

        // Prepare arrays for batch call
        long[] pathfinderHandles = new long[count];
        int[] offsetX = new int[count];
        int[] offsetY = new int[count];
        int[] offsetZ = new int[count];
        int[] widths = new int[count];
        int[] heights = new int[count];
        int[] depths = new int[count];
        byte[][] blockCodes = new byte[count][];
        long[] entityX = new long[count];
        long[] entityY = new long[count];
        long[] entityZ = new long[count];
        long[] targetX = new long[count];
        long[] targetY = new long[count];
        long[] targetZ = new long[count];
        int[] entityWidths = new int[count];
        int[] entityHeights = new int[count];
        int[] maxDistances = new int[count];
        int[] isInWater = new int[count];
        int[] maxSafePointTries = new int[count];

        // Build batch data
        for (int i = 0; i < count; i++) {
            PathfindingRequest req = requests.get(i);
            Entity entity = req.entity;

            // Get or create pathfinder handle
            pathfinderHandles[i] = getPathFinderHandle(
                req.isWoodenDoorAllowed,
                req.isMovementBlockAllowed,
                req.isPathingInWater,
                req.canEntityDrown
            );

            // Calculate bounding box for block cache
            int ex = (int) Math.floor(entity.posX);
            int ey = (int) Math.floor(entity.posY);
            int ez = (int) Math.floor(entity.posZ);
            int tx = (int) Math.floor(req.targetX);
            int ty = (int) Math.floor(req.targetY);
            int tz = (int) Math.floor(req.targetZ);

            int minX = Math.min(ex, tx) - 16;
            int minY = Math.min(ey, ty) - 4;
            int minZ = Math.min(ez, tz) - 16;
            int maxX = Math.max(ex, tx) + 16;
            int maxY = Math.max(ey, ty) + 8;
            int maxZ = Math.max(ez, tz) + 16;

            offsetX[i] = minX;
            offsetY[i] = minY;
            offsetZ[i] = minZ;
            widths[i] = Math.max(1, (maxX - minX) + 1);
            heights[i] = Math.max(1, (maxY - minY) + 1);
            depths[i] = Math.max(1, (maxZ - minZ) + 1);

            // Encode block cache
            blockCodes[i] = encodeBlockCache(world, minX, minY, minZ, widths[i], heights[i], depths[i]);

            // Entity and target positions (as long bits for doubles)
            entityX[i] = Double.doubleToRawLongBits(entity.posX);
            entityY[i] = Double.doubleToRawLongBits(entity.posY);
            entityZ[i] = Double.doubleToRawLongBits(entity.posZ);
            targetX[i] = Double.doubleToRawLongBits(req.targetX);
            targetY[i] = Double.doubleToRawLongBits(req.targetY);
            targetZ[i] = Double.doubleToRawLongBits(req.targetZ);

            // Entity dimensions and parameters (as int bits for floats)
            entityWidths[i] = Float.floatToRawIntBits((float) entity.width);
            entityHeights[i] = Float.floatToRawIntBits((float) entity.height);
            maxDistances[i] = Float.floatToRawIntBits(req.maxDistance);
            isInWater[i] = entity.isInWater() ? 1 : 0;
            maxSafePointTries[i] = entity.getMaxSafePointTries();
        }

        // Call batch pathfinding
        long[] pathHandles = RustPathfinding.findPathBatch(
            pathfinderHandles,
            offsetX, offsetY, offsetZ,
            widths, heights, depths,
            blockCodes,
            entityX, entityY, entityZ,
            targetX, targetY, targetZ,
            entityWidths, entityHeights,
            maxDistances,
            isInWater,
            maxSafePointTries
        );

        // Convert results to PathEntity objects
        List<PathEntity> results = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            if (pathHandles[i] == 0L) {
                results.add(null);
            } else {
                try (RustPathfinding.PathEntityHandle rustPath = new RustPathfinding.PathEntityHandle(pathHandles[i])) {
                    int[] allPoints = rustPath.getAllPoints();
                    if (allPoints == null || allPoints.length == 0) {
                        results.add(null);
                    } else {
                        int numPoints = allPoints.length / 3;
                        PathPoint[] points = new PathPoint[numPoints];
                        for (int j = 0; j < numPoints; j++) {
                            int x = allPoints[j * 3];
                            int y = allPoints[j * 3 + 1];
                            int z = allPoints[j * 3 + 2];
                            points[j] = new PathPoint(x, y, z);
                        }
                        results.add(new PathEntity(points));
                    }
                }
            }
        }

        return results;
    }

    /**
     * Encode block cache for a region
     */
    private static byte[] encodeBlockCache(IBlockAccess world, int minX, int minY, int minZ, 
                                          int width, int height, int depth) {
        byte[] codes = new byte[width * height * depth];

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    int wx = minX + x;
                    int wy = minY + y;
                    int wz = minZ + z;
                    codes[(y * width * depth) + (z * width) + x] = encodeBlock(world, wx, wy, wz);
                }
            }
        }

        return codes;
    }

    /**
     * Encode a single block type
     */
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

    /**
     * Get or create a pathfinder handle with the given parameters
     * Uses a simple encoding scheme to cache handles
     */
    private static long getPathFinderHandle(boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed,
                                           boolean isPathingInWater, boolean canEntityDrown) {
        // Encode flags into handle (same as Rust side)
        long handle = 0;
        if (isWoodenDoorAllowed) handle |= 1 << 0;
        if (isMovementBlockAllowed) handle |= 1 << 1;
        if (isPathingInWater) handle |= 1 << 2;
        if (canEntityDrown) handle |= 1 << 3;
        handle += 1;
        return handle;
    }
}
