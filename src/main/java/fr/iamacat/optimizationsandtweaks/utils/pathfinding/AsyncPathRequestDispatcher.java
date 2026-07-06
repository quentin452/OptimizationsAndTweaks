package fr.iamacat.optimizationsandtweaks.utils.pathfinding;

import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;
import fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfindingBridge;

/**
 * Shared logic for turning a pathfinding request into an immutable region snapshot and handing
 * it to the async executor. Used by both the vanilla {@code PathFinder} mixin and the CoroUtil
 * {@code PFQueue} overrides.
 *
 * <p>
 * <b>Must be called on the server thread</b> — it reads the live world to build the snapshot.
 * The A* search then runs on Rust worker threads against the snapshot only, and the completion
 * callback is invoked from the server tick poll, so nothing touches the world or the entity
 * off-thread.
 */
public final class AsyncPathRequestDispatcher {

    // Region sizing. Larger = better paths around obstacles, but more block reads on the server thread.
    private static final int MARGIN_XZ_MIN = 8;
    private static final int MARGIN_XZ_MAX = 24;
    private static final int MARGIN_Y = 8;
    /** Block cap before we decline and let the caller fall back to a synchronous path. */
    private static final long MAX_VOLUME = 220_000L;

    private AsyncPathRequestDispatcher() {}

    /**
     * Snapshot the region around the entity/target and submit an async request.
     *
     * @return the request id, or {@code 0} if the request was declined (region too large) or the
     *         native queue is full — the caller should fall back to a synchronous path.
     */
    public static long submit(Entity entity, IBlockAccess world, double targetX, double targetY, double targetZ,
        float maxDistance, boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed, boolean isPathingInWater,
        boolean canEntityDrown, Consumer<PathEntity> onComplete, Consumer<String> onFailure) {

        if (world == null || !AsyncPathfindingExecutor.isInitialized()) {
            return 0;
        }

        int sx = MathHelper.floor_double(entity.posX);
        int sy = MathHelper.floor_double(entity.posY);
        int sz = MathHelper.floor_double(entity.posZ);
        int tx = MathHelper.floor_double(targetX);
        int ty = MathHelper.floor_double(targetY);
        int tz = MathHelper.floor_double(targetZ);

        int marginXZ = (int) Math.ceil(maxDistance);
        if (marginXZ < MARGIN_XZ_MIN) marginXZ = MARGIN_XZ_MIN;
        if (marginXZ > MARGIN_XZ_MAX) marginXZ = MARGIN_XZ_MAX;

        int minX = Math.min(sx, tx) - marginXZ - 1;
        int maxX = Math.max(sx, tx) + marginXZ + 1;
        int minZ = Math.min(sz, tz) - marginXZ - 1;
        int maxZ = Math.max(sz, tz) + marginXZ + 1;
        int minY = Math.max(0, Math.min(sy, ty) - MARGIN_Y);
        int maxY = Math.min(255, Math.max(sy, ty) + MARGIN_Y);

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int depth = maxZ - minZ + 1;

        if (width <= 0 || height <= 0 || depth <= 0 || (long) width * height * depth > MAX_VOLUME) {
            return 0;
        }

        // Copy the region on the server thread (safe world read).
        byte[] snapshot = RustPathfindingBridge.encodeBlockCache(world, minX, minY, minZ, width, height, depth);

        int priority = AsyncPathfindingExecutor.determinePriority(entity);

        return AsyncPathfindingExecutor.submitSnapshotPathfinding(
            entity,
            priority,
            isWoodenDoorAllowed,
            isMovementBlockAllowed,
            isPathingInWater,
            canEntityDrown,
            minX,
            minY,
            minZ,
            width,
            height,
            depth,
            snapshot,
            targetX,
            targetY,
            targetZ,
            maxDistance,
            onComplete,
            onFailure);
    }
}
