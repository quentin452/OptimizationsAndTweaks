package fr.iamacat.optimizationsandtweaks.utils.natives;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.common.FMLLog;

/**
 * Java wrapper for Rust async pathfinding executor
 * 
 * This provides a clean Java API over the native Rust thread pool,
 * giving you all the benefits of Rust's performance and safety
 * with Java's ease of use.
 * 
 * Benefits:
 * - Concurrent Processing: Multiple pathfinding requests processed simultaneously
 * - Non-blocking: Game thread never blocks waiting for pathfinding
 * - Resource Management: Controlled worker threads prevent system overload
 * - Progress Tracking: Check completion status without blocking
 * - Backpressure: Queue management prevents memory exhaustion
 */
public class AsyncPathfindingExecutor {
    
    private static boolean initialized = false;
    private static final AtomicLong nextRequestId = new AtomicLong(1);
    
    // Track pending requests and their callbacks
    private static final ConcurrentHashMap<Long, RequestContext> pendingRequests = new ConcurrentHashMap<>();
    
    /**
     * Initialize the async pathfinding executor
     * 
     * @param workerCount Number of worker threads (recommended: CPU cores / 2)
     * @param queueSize Maximum queue size for backpressure
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
            RustPathfinding.initAsyncExecutor(workerCount, queueSize);
            initialized = true;
            FMLLog.info("[AsyncPathfinding] Initialized with %d workers and queue size %d", 
                        workerCount, queueSize);
        } catch (UnsatisfiedLinkError e) {
            FMLLog.warning("[AsyncPathfinding] Async executor not available in native library: %s", e.getMessage());
            initialized = false;
            return;
        }
    }
    
    /**
     * Initialize with automatic worker count based on CPU cores
     */
    public static synchronized void initializeAuto() {
        int cores = Runtime.getRuntime().availableProcessors();
        // Use 50% of cores for pathfinding
        int workers = Math.max(2, cores / 2);
        // Queue size: 4x worker count
        int queueSize = workers * 4;
        initialize(workers, queueSize);
    }
    
    /**
     * Check if the executor is initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
    // TODO
    public boolean canBreakDoors(EntityLiving entity) {
        if (entity.tasks.taskEntries != null) {
            for (Object entryObj : entity.tasks.taskEntries) {
                // taskEntries is a List<EntityAITasks.EntityAITaskEntry>
                net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry entry =
                    (net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry) entryObj;
                if (entry.action instanceof net.minecraft.entity.ai.EntityAIBreakDoor) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Submit an async pathfinding request with explicit pathing flags
     *
     * @param isWoodenDoorAllowed whether wooden doors are considered passable
     * @param isMovementBlockAllowed whether movement-blocking tiles are considered passable
     * @param isPathingInWater whether the entity is pathing in water
     * @param canEntityDrown whether the entity can drown
     */
    public static long submitPathfinding(
            IBlockAccess world,
            Entity entity,
            double targetX,
            double targetY,
            double targetZ,
            float maxDistance,
            int priority,
            boolean isWoodenDoorAllowed,
            boolean isMovementBlockAllowed,
            boolean isPathingInWater,
            boolean canEntityDrown) {

        if (!initialized) {
            FMLLog.warning("[AsyncPathfinding] Not initialized");
            return 0;
        }

        long requestId = nextRequestId.getAndIncrement();

        // Prepare block cache
        int radius = (int) Math.ceil(maxDistance) + 16;
        int offsetX = (int) Math.floor(entity.posX) - radius;
        int offsetY = (int) Math.floor(entity.posY) - radius;
        int offsetZ = (int) Math.floor(entity.posZ) - radius;
        int width = radius * 2;
        int height = radius * 2;
        int depth = radius * 2;

        byte[] blockCache = RustPathfindingBridge.encodeBlockCache(
            world, offsetX, offsetY, offsetZ, width, height, depth
        );

        float pathfindingRange = 16F; // default fallback
        if (entity instanceof EntityLivingBase) {
            pathfindingRange = (float)((EntityLivingBase) entity)
                .getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
        }

        long submittedId = RustPathfinding.submitAsyncPathfinding(
            requestId,
            priority,
            isWoodenDoorAllowed,
            isMovementBlockAllowed,
            isPathingInWater,
            canEntityDrown,
            offsetX, offsetY, offsetZ,
            width, height, depth,
            blockCache,
            entity.posX, entity.posY, entity.posZ,
            targetX, targetY, targetZ,
            entity.width, entity.height,
            maxDistance,
            entity.isInWater(),
            (int) pathfindingRange
        );

        if (submittedId == 0) {
            return 0;
        }

        pendingRequests.put(requestId, new RequestContext(entity));
        return requestId;
    }

    /**
     * Submit with automatic priority and explicit pathing flags
     */
    public static long submitPathfinding(
            IBlockAccess world,
            Entity entity,
            double targetX,
            double targetY,
            double targetZ,
            float maxDistance,
            boolean isWoodenDoorAllowed,
            boolean isMovementBlockAllowed,
            boolean isPathingInWater,
            boolean canEntityDrown) {

        int priority = determinePriority(entity, world);
        return submitPathfinding(world, entity, targetX, targetY, targetZ, maxDistance, priority,
            isWoodenDoorAllowed, isMovementBlockAllowed, isPathingInWater, canEntityDrown);
    }
    
    /**
     * Submit pathfinding with a callback
     * 
     * @param onComplete Callback invoked when pathfinding completes (may be null)
     * @param onFailure Callback invoked when pathfinding fails (may be null)
     * @return Request ID, or 0 if queue is full
     */
    public static long submitPathfindingWithCallback(
            IBlockAccess world,
            Entity entity,
            double targetX,
            double targetY,
            double targetZ,
            float maxDistance,
            Consumer<PathEntity> onComplete,
            Consumer<String> onFailure,
            boolean isWoodenDoorAllowed,
            boolean isMovementBlockAllowed,
            boolean isPathingInWater,
            boolean canEntityDrown) {

        long requestId = submitPathfinding(
            world,
            entity,
            targetX,
            targetY,
            targetZ,
            maxDistance,
            determinePriority(entity, world),
            isWoodenDoorAllowed,
            isMovementBlockAllowed,
            isPathingInWater,
            canEntityDrown
        );

        if (requestId != 0) {
            RequestContext context = pendingRequests.get(requestId);
            if (context != null) {
                context.onComplete = onComplete;
                context.onFailure = onFailure;
            }
        }
        return requestId;
    }
    
    /**
     * Poll for completed pathfinding results
     * Call this periodically (e.g., in a tick handler) to process results
     * 
     * @return Number of results processed
     */
    public static int pollResults() {
        if (!initialized) {
            return 0;
        }
        
        int processed = 0;
        int[] outRequestId = new int[1];
        
        // Process all available results
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
                    // Convert Rust path to Java PathEntity
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
            }
            
            processed++;
        }
        
        return processed;
    }
    
    /**
     * Get current executor statistics
     * Returns: [total_submitted, total_completed, total_failed, total_timed_out, queue_size, active_workers, worker_count]
     */
    public static int[] getStatistics() {
        if (!initialized) {
            return new int[7];
        }
        return RustPathfinding.getAsyncExecutorStats();
    }
    
    /**
     * Get a formatted statistics string
     */
    public static String getStatisticsString() {
        int[] stats = getStatistics();
        return String.format(
            "Async Pathfinding Stats - Submitted: %d, Completed: %d, Failed: %d, Queue: %d/%d, Active: %d/%d",
            stats[0], stats[1], stats[2], stats[4], stats[6] * 4, stats[5], stats[6]
        );
    }
    
    /**
     * Cancel a pending request
     * Note: If the request is already being processed, it cannot be cancelled
     */
    public static void cancelRequest(long requestId) {
        pendingRequests.remove(requestId);
    }
    
    /**
     * Get the number of pending requests
     */
    public static int getPendingCount() {
        return pendingRequests.size();
    }
    
    /**
     * Shutdown the executor gracefully
     */
    public static synchronized void shutdown() {
        if (!initialized) {
            return;
        }
        
        FMLLog.info("[AsyncPathfinding] Shutting down - %d pending requests", pendingRequests.size());
        FMLLog.info(getStatisticsString());
        
        RustPathfinding.shutdownAsyncExecutor();
        pendingRequests.clear();
        initialized = false;
    }
    
    /**
     * Determine priority based on entity characteristics
     */
    private static int determinePriority(Entity entity, IBlockAccess world) {
        if (entity instanceof net.minecraft.entity.player.EntityPlayer) {
            return 100;
        }
        
        if ((entity instanceof EntityLiving && ((EntityLiving) entity).hasCustomNameTag()) ||
            (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getMaxHealth() > 100.0f)) {
            return 100;
        }
        
        // Hostile mobs
        if (entity instanceof net.minecraft.entity.monster.EntityMob) {
            // Distance to nearest player
            if (world instanceof net.minecraft.world.World) {
                net.minecraft.world.World worldObj = (net.minecraft.world.World) world;
                net.minecraft.entity.player.EntityPlayer nearestPlayer = 
                    worldObj.getClosestPlayerToEntity(entity, -1.0);
                
                if (nearestPlayer != null) {
                    double distance = entity.getDistanceToEntity(nearestPlayer);
                    if (distance < 32.0) {
                        return 75; // HIGH - hostile near player
                    } else if (distance < 64.0) {
                        return 50; // NORMAL
                    }
                }
            }
            return 50; // NORMAL
        }
        
        // Passive mobs - check distance to player
        if (world instanceof net.minecraft.world.World) {
            net.minecraft.world.World worldObj = (net.minecraft.world.World) world;
            net.minecraft.entity.player.EntityPlayer nearestPlayer = 
                worldObj.getClosestPlayerToEntity(entity, -1.0);
            
            if (nearestPlayer != null) {
                double distance = entity.getDistanceToEntity(nearestPlayer);
                if (distance < 64.0) {
                    return 50; // NORMAL
                } else if (distance < 128.0) {
                    return 25; // LOW
                }
            }
        }
        
        return 10; // BACKGROUND
    }
    
    /**
     * Convert Rust path handle to Java PathEntity
     */
    private static PathEntity convertRustPath(long pathHandle) {
        if (pathHandle == 0) {
            return null;
        }
        
        try (RustPathfinding.PathEntityHandle rustPath = new RustPathfinding.PathEntityHandle(pathHandle)) {
            int[] allPoints = rustPath.getAllPoints();
            
            if (allPoints.length == 0) {
                return null;
            }
            
            // Convert flattened array to PathPoint array
            int pointCount = allPoints.length / 3;
            PathPoint[] points = new PathPoint[pointCount];
            
            for (int i = 0; i < pointCount; i++) {
                int x = allPoints[i * 3];
                int y = allPoints[i * 3 + 1];
                int z = allPoints[i * 3 + 2];
                points[i] = new PathPoint(x, y, z);
            }
            
            return new PathEntity(points);
        } catch (Exception e) {
            FMLLog.warning("[AsyncPathfinding] Error converting path: %s", e.getMessage());
            return null;
        }
    }
    
    /**
     * Context for a pending pathfinding request
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
