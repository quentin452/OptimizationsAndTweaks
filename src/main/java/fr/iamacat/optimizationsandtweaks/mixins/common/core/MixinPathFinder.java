package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.devtools.AiEventTrace;
import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.AsyncPathCaches;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.AsyncPathRequestDispatcher;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.CachedPath;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.PendingPathRequest;

/**
 * Routes vanilla pathfinding through the async Rust executor.
 *
 * <p>
 * On the server thread this snapshots an immutable block region around the entity/target and
 * submits it; the A* search runs on Rust worker threads against that snapshot only (never the
 * live world). Completed paths are applied on the server tick (see
 * {@link fr.iamacat.optimizationsandtweaks.eventshandler.AsyncPathfindingTickHandler}), so no
 * world read or entity mutation ever happens off-thread.
 *
 * <p>
 * The first request for a given (entity, target) returns null so vanilla is cancelled for that
 * tick; the async result is applied a few ticks later. If the region would be too large, or the
 * native queue is full, this declines and lets vanilla run synchronously.
 */
@Mixin(PathFinder.class)
public abstract class MixinPathFinder {

    @Shadow
    private IBlockAccess worldMap;

    @Shadow
    private boolean isWoddenDoorAllowed;

    @Shadow
    private boolean isMovementBlockAllowed;

    @Shadow
    private boolean isPathingInWater;

    @Shadow
    private boolean canEntityDrown;

    // Request outcome
    @Unique
    private static final int OPT$DECLINE = 0; // run vanilla synchronously
    @Unique
    private static final int OPT$READY = 1; // cached path available
    @Unique
    private static final int OPT$PENDING = 2; // async in flight, skip vanilla this tick

    @Unique
    private static boolean optimizationsAndTweaks$asyncPathfindingEnabled = false;

    /**
     * (Re)initialize the async executor if needed (handles world restarts).
     */
    @Unique
    private static void optimizationsAndTweaks$checkAsyncPathfinding() {
        optimizationsAndTweaks$asyncPathfindingEnabled = AsyncPathfindingExecutor.isInitialized();
        if (!optimizationsAndTweaks$asyncPathfindingEnabled) {
            AsyncPathfindingExecutor.initializeAuto();
            optimizationsAndTweaks$asyncPathfindingEnabled = AsyncPathfindingExecutor.isInitialized();
        }
    }

    @Inject(
        method = "createEntityPathTo(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/pathfinding/PathEntity;",
        at = @At("HEAD"),
        cancellable = true)
    private void optimizationsAndTweaks$createEntityPathToEntity(Entity entity, Entity target, float maxDistance,
        CallbackInfoReturnable<PathEntity> cir) {
        optimizationsAndTweaks$dispatch(entity, target.posX, target.boundingBox.minY, target.posZ, maxDistance, cir);
    }

    @Inject(
        method = "createEntityPathTo(Lnet/minecraft/entity/Entity;IIIF)Lnet/minecraft/pathfinding/PathEntity;",
        at = @At("HEAD"),
        cancellable = true)
    private void optimizationsAndTweaks$createEntityPathToCoords(Entity entity, int x, int y, int z, float maxDistance,
        CallbackInfoReturnable<PathEntity> cir) {
        optimizationsAndTweaks$dispatch(entity, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, maxDistance, cir);
    }

    @Unique
    private void optimizationsAndTweaks$dispatch(Entity entity, double targetX, double targetY, double targetZ,
        float maxDistance, CallbackInfoReturnable<PathEntity> cir) {
        optimizationsAndTweaks$checkAsyncPathfinding();
        if (!optimizationsAndTweaks$asyncPathfindingEnabled) {
            return; // vanilla
        }

        try {
            PathEntity[] out = new PathEntity[1];
            int status = optimizationsAndTweaks$requestPath(entity, targetX, targetY, targetZ, maxDistance, out);
            switch (status) {
                case OPT$READY:
                    cir.setReturnValue(out[0]);
                    return;
                case OPT$PENDING:
                    // Skip the heavy vanilla search while the async result is in flight.
                    cir.setReturnValue(null);
                    return;
                case OPT$DECLINE:
                default:
                    return; // let vanilla run
            }
        } catch (Exception e) {
            cpw.mods.fml.common.FMLLog.warning(
                "[OptimizationsAndTweaks] Async pathfinding failed, falling back to vanilla: %s",
                e.getMessage());
        }
    }

    /**
     * Returns a cached path if fresh, otherwise snapshots the region and submits an async
     * request. Runs on the server thread.
     */
    @Unique
    private int optimizationsAndTweaks$requestPath(Entity entity, double targetX, double targetY, double targetZ,
        float maxDistance, PathEntity[] out) {

        final int entityId = entity.getEntityId();
        final long now = System.currentTimeMillis();

        final boolean traced = AiEventTrace.watched(entityId);

        // Fresh cached path?
        CachedPath cached = AsyncPathCaches.cachedPaths.get(entityId);
        if (cached != null && cached.isValid(entity.posX, entity.posY, entity.posZ, targetX, targetY, targetZ, now)) {
            out[0] = cached.getPath();
            if (traced) AiEventTrace.record(entityId, "pathfinder.requestPath -> READY (cached)");
            return OPT$READY;
        }

        // Already in flight for (roughly) this target?
        PendingPathRequest pending = AsyncPathCaches.pendingPaths.get(entityId);
        if (pending != null && pending.isStillValid(targetX, targetY, targetZ, now)) {
            if (traced) AiEventTrace.record(entityId, "pathfinder.requestPath -> PENDING (in flight, return null)");
            return OPT$PENDING;
        }

        // Unreachable-target backoff (issue #49): a recent request to (roughly) this target already found no path.
        // Re-computing it every AI interval is wasted work — it floods the async queue and churns the AI while the
        // mob keeps failing to reach an unreachable goal (e.g. a player on a ledge it cannot climb). Skip until the
        // backoff expires or the target moves; returning no path here also stops the mob walking toward the dead goal.
        if (AsyncPathCaches.isNoPathCooling(entityId, targetX, targetY, targetZ, now)) {
            if (traced) AiEventTrace.record(entityId, "pathfinder.requestPath -> NOPATH_BACKOFF (skip)");
            return OPT$PENDING;
        }

        final double fTargetX = targetX, fTargetY = targetY, fTargetZ = targetZ;
        long requestId = AsyncPathRequestDispatcher.submit(
            entity,
            worldMap,
            targetX,
            targetY,
            targetZ,
            maxDistance,
            isWoddenDoorAllowed,
            isMovementBlockAllowed,
            isPathingInWater,
            canEntityDrown,
            // onComplete — runs on the server tick (see poll handler), so setPath is safe.
            path -> {
                AsyncPathCaches.cachedPaths.put(
                    entityId,
                    new CachedPath(
                        path,
                        entity.posX,
                        entity.posY,
                        entity.posZ,
                        fTargetX,
                        fTargetY,
                        fTargetZ,
                        System.currentTimeMillis()));
                AsyncPathCaches.pendingPaths.remove(entityId);
                // A path was found: the target is reachable, drop any unreachable-backoff (#49).
                AsyncPathCaches.clearNoPath(entityId);
                try {
                    if (!entity.isDead && path != null && entity instanceof EntityLiving) {
                        // Restore the speed the AI originally asked for (panic 2.0, follow, ...),
                        // captured by MixinPathNavigate; default 1.0 if never recorded.
                        Double reqSpeed = AsyncPathCaches.requestedSpeed.get(entityId);
                        double speed = reqSpeed != null ? reqSpeed : 1.0D;
                        ((EntityLiving) entity).getNavigator()
                            .setPath(path, speed);
                        if (AiEventTrace.watched(entityId)) {
                            AiEventTrace.record(
                                entityId,
                                "pathfinder.asyncApply setPath nodes=" + path.getCurrentPathLength()
                                    + " speed="
                                    + speed);
                        }
                    } else if (AiEventTrace.watched(entityId)) {
                        AiEventTrace.record(entityId, "pathfinder.asyncApply skipped (dead/null path)");
                    }
                } catch (Throwable ignored) {}
            },
            // onFailure — no path: clear the in-flight marker and start the unreachable-backoff (#49).
            error -> {
                AsyncPathCaches.pendingPaths.remove(entityId);
                AsyncPathCaches.recordNoPath(entityId, fTargetX, fTargetY, fTargetZ, System.currentTimeMillis());
            });

        if (requestId == 0) {
            // Region too large or native queue full: let vanilla run this tick.
            if (traced) AiEventTrace.record(entityId, "pathfinder.requestPath -> DECLINE (queue full/region big)");
            return OPT$DECLINE;
        }

        AsyncPathCaches.pendingPaths.put(entityId, new PendingPathRequest(requestId, targetX, targetY, targetZ, now));
        if (traced)
            AiEventTrace.record(entityId, "pathfinder.requestPath -> SUBMIT id=" + requestId + " (return null)");
        return OPT$PENDING;
    }
}
