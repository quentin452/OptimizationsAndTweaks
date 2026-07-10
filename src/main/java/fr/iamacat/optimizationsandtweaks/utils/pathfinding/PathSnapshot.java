package fr.iamacat.optimizationsandtweaks.utils.pathfinding;

import java.nio.ByteBuffer;

import fr.iamacat.optimizationsandtweaks.utils.natives.DirectSnapshotPool;

/**
 * Immutable description of one async pathfinding request: the block-region snapshot (heap {@code byte[]}
 * or a pooled off-heap {@link DirectSnapshotPool.Slot}) plus the request parameters, all captured on the
 * server thread. It is the {@code S} of the {@link fr.iamacat.exec.Job Job&lt;PathSnapshot, PathEntity&gt;}
 * routed through the matoulib execution seam (hub doc 28): once built it is read-only, so the Rust worker
 * threads that run the A* over it never touch the live {@code World}.
 *
 * <p>
 * Exactly one of {@link #heapCache} / {@link #slot} is non-null: {@code slot} for the zero-copy direct
 * path, {@code heapCache} for the legacy heap path (see {@link AsyncPathRequestDispatcher}).
 */
public final class PathSnapshot {

    public final int offsetX, offsetY, offsetZ;
    public final int width, height, depth;

    /** Legacy heap-encoded region (layout [y][z][x]); null when {@link #slot} is used. */
    public final byte[] heapCache;
    /** Pooled off-heap region; null when {@link #heapCache} is used. */
    public final DirectSnapshotPool.Slot slot;

    public final double targetX, targetY, targetZ;
    public final float maxDistance;
    public final int priority;

    public final boolean isWoodenDoorAllowed;
    public final boolean isMovementBlockAllowed;
    public final boolean isPathingInWater;
    public final boolean canEntityDrown;

    private PathSnapshot(int offsetX, int offsetY, int offsetZ, int width, int height, int depth, byte[] heapCache,
        DirectSnapshotPool.Slot slot, double targetX, double targetY, double targetZ, float maxDistance, int priority,
        boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed, boolean isPathingInWater, boolean canEntityDrown) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.heapCache = heapCache;
        this.slot = slot;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.maxDistance = maxDistance;
        this.priority = priority;
        this.isWoodenDoorAllowed = isWoodenDoorAllowed;
        this.isMovementBlockAllowed = isMovementBlockAllowed;
        this.isPathingInWater = isPathingInWater;
        this.canEntityDrown = canEntityDrown;
    }

    /** Zero-copy variant: the region has been encoded into the pooled off-heap {@code slot}. */
    public static PathSnapshot direct(int offsetX, int offsetY, int offsetZ, int width, int height, int depth,
        DirectSnapshotPool.Slot slot, double targetX, double targetY, double targetZ, float maxDistance, int priority,
        boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed, boolean isPathingInWater, boolean canEntityDrown) {
        return new PathSnapshot(
            offsetX,
            offsetY,
            offsetZ,
            width,
            height,
            depth,
            null,
            slot,
            targetX,
            targetY,
            targetZ,
            maxDistance,
            priority,
            isWoodenDoorAllowed,
            isMovementBlockAllowed,
            isPathingInWater,
            canEntityDrown);
    }

    /** Legacy variant: the region has been copied into a heap {@code byte[]}. */
    public static PathSnapshot heap(int offsetX, int offsetY, int offsetZ, int width, int height, int depth,
        byte[] heapCache, double targetX, double targetY, double targetZ, float maxDistance, int priority,
        boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed, boolean isPathingInWater, boolean canEntityDrown) {
        return new PathSnapshot(
            offsetX,
            offsetY,
            offsetZ,
            width,
            height,
            depth,
            heapCache,
            null,
            targetX,
            targetY,
            targetZ,
            maxDistance,
            priority,
            isWoodenDoorAllowed,
            isMovementBlockAllowed,
            isPathingInWater,
            canEntityDrown);
    }

    /** The off-heap buffer Rust reads in place (direct path only). */
    public ByteBuffer directBuffer() {
        return slot.buffer();
    }
}
