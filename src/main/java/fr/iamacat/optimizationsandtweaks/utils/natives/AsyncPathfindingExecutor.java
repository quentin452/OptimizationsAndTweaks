package fr.iamacat.optimizationsandtweaks.utils.natives;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;

import cpw.mods.fml.common.FMLLog;

/**
 * Java front-end for the native Rust async pathfinding executor.
 *
 * <p>
 * Threading contract (this is the whole point of the class):
 * <ul>
 * <li>{@link #submitSnapshotPathfinding} is called on the <b>server thread</b> during an
 * entity AI tick. The caller has already copied the relevant block region into an immutable
 * {@code byte[]} snapshot, so the Rust worker threads never touch the live {@code World}.</li>
 * <li>{@link #pollResults()} is called on the <b>server tick</b> only. It drains completed
 * results and runs their callbacks on the server thread, so applying a path
 * ({@code navigator.setPath}) is a same-thread mutation.</li>
 * </ul>
 * Neither world reads nor entity mutation ever happen on a worker thread. That is the
 * difference from the previous "direct world access via JNI" implementation, which read the
 * world and mutated entities off-thread and could crash/corrupt intermittently.
 */
public class AsyncPathfindingExecutor {

    private static boolean initialized = false;
    private static final AtomicLong nextRequestId = new AtomicLong(1);

    // Track pending requests and their callbacks (keyed by request id).
    private static final ConcurrentHashMap<Long, RequestContext> pendingRequests = new ConcurrentHashMap<>();

    /**
     * Initialize the native Rust async executor.
     *
     * @param workerCount Number of Rust worker threads
     * @param queueSize   Maximum queue size for backpressure
     */
    public static synchronized void initialize(int workerCount, int queueSize) {
        if (initialized) {
            FMLLog.warning("[AsyncPathfinding] Already initialized");
            return;
        }

        if (!RustPathfinding.isAvailable()) {
            FMLLog.warning("[AsyncPathfinding] Rust pathfinding not available");
            return;
        }

        try {
            RustPathfinding.initAsyncExecutor(Math.max(1, workerCount), Math.max(1, queueSize));
            initialized = true;
            FMLLog.info(
                "[AsyncPathfinding] Native executor initialized with %d workers (immutable region snapshots)",
                Math.max(1, workerCount));
        } catch (Throwable e) {
            FMLLog.warning("[AsyncPathfinding] Failed to initialize native executor: %s", e.getMessage());
            initialized = false;
        }
    }

    /**
     * Initialize with automatic worker count based on CPU cores.
     */
    public static synchronized void initializeAuto() {
        int cores = Runtime.getRuntime()
            .availableProcessors();
        // Use 50% of cores for pathfinding
        int workers = Math.max(2, cores / 2);
        // Queue size: 4x worker count
        int queueSize = workers * 4;
        initialize(workers, queueSize);
    }

    /**
     * Check if the executor is initialized.
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Submit a pathfinding request against a pre-encoded, immutable block-region snapshot.
     * <b>Must be called on the server thread</b> (the snapshot must be taken there too).
     *
     * @param priority           scheduling hint for the Rust queue
     * @param offsetX/Y/Z        world coordinate of the snapshot's minimum corner
     * @param width/height/depth snapshot dimensions (x/y/z)
     * @param blockCache         encoded block codes, layout [y][z][x] (x fastest) — see
     *                           {@link RustPathfindingBridge#encodeBlockCache}
     * @param onComplete         run on the server tick when a path is found (may be null)
     * @param onFailure          run on the server tick when no path is found (may be null)
     * @return request id, or 0 if the native queue is full (caller should fall back to vanilla)
     */
    public static long submitSnapshotPathfinding(Entity entity, int priority, boolean isWoodenDoorAllowed,
        boolean isMovementBlockAllowed, boolean isPathingInWater, boolean canEntityDrown, int offsetX, int offsetY,
        int offsetZ, int width, int height, int depth, byte[] blockCache, double targetX, double targetY,
        double targetZ, float maxDistance, Consumer<PathEntity> onComplete, Consumer<String> onFailure) {

        if (!initialized) {
            return 0;
        }

        long requestId = nextRequestId.getAndIncrement();

        RequestContext context = new RequestContext(entity);
        context.onComplete = onComplete;
        context.onFailure = onFailure;
        pendingRequests.put(requestId, context);

        long accepted = RustPathfinding.submitAsyncPathfinding(
            requestId,
            priority,
            isWoodenDoorAllowed,
            isMovementBlockAllowed,
            isPathingInWater,
            canEntityDrown,
            offsetX,
            offsetY,
            offsetZ,
            width,
            height,
            depth,
            blockCache,
            entity.posX,
            entity.posY,
            entity.posZ,
            targetX,
            targetY,
            targetZ,
            (float) entity.width,
            (float) entity.height,
            maxDistance,
            entity.isInWater(),
            entity.getMaxSafePointTries());

        if (accepted == 0) {
            // Queue full or executor unavailable: drop the context, caller runs vanilla.
            pendingRequests.remove(requestId);
            return 0;
        }

        return requestId;
    }

    /**
     * Poll for completed pathfinding results and run their callbacks.
     * <b>Must be called on the server tick only</b> so callbacks mutate entities on the
     * server thread.
     *
     * @return number of results processed
     */
    public static int pollResults() {
        if (!initialized) {
            return 0;
        }

        int processed = 0;
        int[] outRequestId = new int[1];

        while (true) {
            long pathHandle = RustPathfinding.tryRecvAsyncResult(outRequestId);

            if (pathHandle == 0) {
                // No more results available
                break;
            }

            long requestId = outRequestId[0];
            RequestContext context = pendingRequests.remove(requestId);

            if (context != null) {
                try {
                    // pathHandle < 0 is the "no path" sentinel; convertRustPath returns null for it.
                    PathEntity path = convertRustPath(pathHandle);

                    if (path != null && context.onComplete != null) {
                        context.onComplete.accept(path);
                    } else if (path == null && context.onFailure != null) {
                        context.onFailure.accept("No path found");
                    }
                } catch (Exception e) {
                    if (context.onFailure != null) {
                        context.onFailure.accept("Error: " + e.getMessage());
                    }
                    FMLLog.warning("[AsyncPathfinding] Error processing result: %s", e.getMessage());
                }
            } else {
                // Orphan result (entity died / request cancelled): free the native handle.
                if (pathHandle > 0) {
                    try (RustPathfinding.PathEntityHandle h = new RustPathfinding.PathEntityHandle(pathHandle)) {
                        // close() frees it
                    } catch (Throwable ignore) {}
                }
            }

            processed++;
        }

        return processed;
    }

    /**
     * Get current executor statistics.
     * Returns: [total_submitted, total_completed, total_failed, total_timed_out, queue_size, active_workers,
     * worker_count]
     */
    public static int[] getStatistics() {
        if (!initialized) {
            return new int[7];
        }
        return RustPathfinding.getAsyncExecutorStats();
    }

    /**
     * Get a formatted statistics string.
     */
    public static String getStatisticsString() {
        int[] stats = getStatistics();
        return String.format(
            "Async Pathfinding Stats - Submitted: %d, Completed: %d, Failed: %d, Queue: %d/%d, Active: %d/%d",
            stats[0],
            stats[1],
            stats[2],
            stats[4],
            stats[6] * 4,
            stats[5],
            stats[6]);
    }

    /**
     * Cancel a pending request. If the result already came back it is a no-op.
     */
    public static void cancelRequest(long requestId) {
        pendingRequests.remove(requestId);
    }

    /**
     * Get the number of pending requests.
     */
    public static int getPendingCount() {
        return pendingRequests.size();
    }

    /**
     * Shutdown the native executor gracefully.
     */
    public static synchronized void shutdown() {
        if (!initialized) {
            return;
        }

        FMLLog.info("[AsyncPathfinding] Shutting down - %d pending requests", pendingRequests.size());
        FMLLog.info(getStatisticsString());

        try {
            RustPathfinding.shutdownAsyncExecutor();
        } catch (Throwable ignore) {}
        pendingRequests.clear();
        initialized = false;
    }

    /**
     * Convert a Rust path handle to a Java PathEntity, freeing the native handle.
     */
    private static PathEntity convertRustPath(long pathHandle) {
        if (pathHandle <= 0) {
            return null;
        }

        try (RustPathfinding.PathEntityHandle rustPath = new RustPathfinding.PathEntityHandle(pathHandle)) {
            int[] allPoints = rustPath.getAllPoints();

            if (allPoints.length == 0) {
                return null;
            }

            int pointCount = allPoints.length / 3;
            PathPoint[] points = new PathPoint[pointCount];

            for (int i = 0; i < pointCount; i++) {
                int x = allPoints[i * 3];
                int y = allPoints[i * 3 + 1];
                int z = allPoints[i * 3 + 2];
                points[i] = new PathPoint(x, y, z);
            }
            // Safety: ensure at least 2 points to avoid navigator indexing past length
            if (pointCount == 1) {
                PathPoint p = points[0];
                PathPoint[] doubled = new PathPoint[] { p, new PathPoint(p.xCoord, p.yCoord, p.zCoord) };
                return new PathEntity(doubled);
            }

            return new PathEntity(points);
        } catch (Exception e) {
            FMLLog.warning("[AsyncPathfinding] Error converting path: %s", e.getMessage());
            return null;
        }
    }

    /**
     * Determine priority based on entity characteristics. Called on the server thread.
     */
    public static int determinePriority(Entity entity) {
        try {
            if (entity instanceof net.minecraft.entity.EntityLiving) {
                net.minecraft.entity.EntityLiving el = (net.minecraft.entity.EntityLiving) entity;
                if (el.getAttackTarget() != null && el.getAttackTarget()
                    .isEntityAlive()) {
                    return 80; // high priority: actively chasing
                }
            }
        } catch (Throwable ignore) {}

        if (entity instanceof net.minecraft.entity.player.EntityPlayer) {
            return 100;
        }

        if ((entity instanceof net.minecraft.entity.EntityLiving
            && ((net.minecraft.entity.EntityLiving) entity).hasCustomNameTag())
            || (entity instanceof net.minecraft.entity.EntityLivingBase
                && ((net.minecraft.entity.EntityLivingBase) entity).getMaxHealth() > 100.0f)) {
            return 100;
        }

        if (entity instanceof net.minecraft.entity.monster.EntityMob) {
            if (entity.worldObj != null) {
                net.minecraft.entity.player.EntityPlayer nearestPlayer = entity.worldObj
                    .getClosestPlayerToEntity(entity, -1.0);
                if (nearestPlayer != null) {
                    double distance = entity.getDistanceToEntity(nearestPlayer);
                    if (distance < 32.0) {
                        return 75;
                    } else if (distance < 64.0) {
                        return 50;
                    }
                }
            }
            return 50;
        }

        if (entity.worldObj != null) {
            net.minecraft.entity.player.EntityPlayer nearestPlayer = entity.worldObj
                .getClosestPlayerToEntity(entity, -1.0);
            if (nearestPlayer != null) {
                double distance = entity.getDistanceToEntity(nearestPlayer);
                if (distance < 64.0) {
                    return 50;
                } else if (distance < 128.0) {
                    return 25;
                }
            }
        }

        return 10; // background
    }

    /**
     * Context for a pending pathfinding request.
     */
    private static class RequestContext {

        final Entity entity;
        Consumer<PathEntity> onComplete;
        Consumer<String> onFailure;
        final long submittedAt;

        RequestContext(Entity entity) {
            this.entity = entity;
            this.submittedAt = System.currentTimeMillis();
        }
    }
}
